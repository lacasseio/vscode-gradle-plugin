/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lacasse.vscode.gradle.internal;

import io.lacasse.vscode.gradle.VisualStudioCodeCppConfiguration;
import io.lacasse.vscode.gradle.internal.tasks.GenerateCompileCommandsFileTask;
import org.gradle.api.Transformer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.ToolType;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultVisualStudioCodeCppConfiguration implements VisualStudioCodeCppConfiguration {
    private final String name;
    private final ConfigurableFileCollection includes;
    private final ProjectLayout projectLayout;
    private final ListProperty<String> defines;
    private final RegularFileProperty compileCommandsLocation;
    private final ProviderFactory providerFactory;

    @Inject
    public DefaultVisualStudioCodeCppConfiguration(String name, ProjectLayout projectLayout, ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.name = name;
        this.includes = projectLayout.configurableFiles();
        this.projectLayout = projectLayout;
        this.defines = objectFactory.listProperty(String.class);
        this.compileCommandsLocation = objectFactory.fileProperty();
        this.providerFactory = providerFactory;
    }

    @Input
    @Override
    public String getDisplayName() {
        return name;
    }

    @InputFiles
    @Override
    public ConfigurableFileCollection getIncludes() {
        return includes;
    }

    @Input
    @Override
    public ListProperty<String> getDefines() {
        return defines;
    }

    @InputFile
    @Override
    public RegularFileProperty getCompileCommandsLocation() {
        return compileCommandsLocation;
    }

    @Override
    public void configureFromBinary(CppBinary binary) {
        Provider<? extends CppCompile> compileTask = binary.getCompileTask();

        getIncludes().from(compileTask.map((Transformer<Object, CppCompile>) cppCompile -> cppCompile.getIncludes().plus(cppCompile.getSystemIncludes())));
        getDefines().set(compileTask.map((Transformer<Iterable<? extends String>, CppCompile>) cppCompile -> {
            Map<String, String> macros = new LinkedHashMap<>(cppCompile.getMacros());
            for (String arg : cppCompile.getCompilerArgs().get()) {
                if (arg.startsWith("-D")) {
                    // TODO: Good approximation
                    macros.put(arg.substring(2), null);
                }
            }

            List<String> result = new ArrayList<>();
            for (Map.Entry<String, String> e : macros.entrySet()) {
                String macro = e.getKey();
                if (e.getValue() != null) {
                    macro += "=" + e.getValue();
                }
                result.add(macro);
            }
            return result;
        }));

        getCompileCommandsLocation()
                .set(providerFactory
                        .provider(() -> projectLayout.getProjectDirectory().file("dummy"))
                        .flatMap(new Transformer<Provider<? extends RegularFile>, RegularFile>() {
                            private boolean alreadyCalled = false;
                            @Override
                            public Provider<? extends RegularFile> transform(RegularFile regularFile) {
                                TaskContainer tasks = compileTask.get().getProject().getTasks();
                                if (!alreadyCalled) {
                                    alreadyCalled = true;
                                    return generateCompileCommandsFileFor(tasks, binary);
                                }
                                return tasks.named(GenerateCompileCommandsFileTask.taskName(binary), GenerateCompileCommandsFileTask.class).get().getCompileCommandsFileLocation();
                            }
                        }));
    }

    private static Provider<RegularFile> generateCompileCommandsFileFor(TaskContainer tasks, CppBinary binary) {
        return tasks.register(GenerateCompileCommandsFileTask.taskName(binary), GenerateCompileCommandsFileTask.class, it -> {
            ProviderFactory providerFactory = it.getProject().getProviders();
            ProjectLayout projectLayout = it.getProject().getLayout();

            it.setDescription("Generate compile_commands.json for '" + binary + "'");
            it.dependsOn(binary.getCompileTask());
            it.getCompiler().set(providerFactory.provider(() -> {
                CppCompile compileTask = binary.getCompileTask().get();
                RegularFileProperty f = projectLayout.fileProperty();
                f.set(((NativeToolChainInternal)compileTask.getToolChain().get()).select((NativePlatformInternal) compileTask.getTargetPlatform().get()).locateTool(ToolType.CPP_COMPILER).getTool());
                return f.get();
            }));

            it.getOptionsFile().set(providerFactory.provider(() -> {
                CppCompile compileTask = binary.getCompileTask().get();
                RegularFileProperty f = projectLayout.fileProperty();
                f.set(new File(compileTask.getTemporaryDir(), "options.txt"));
                return f.get();
            }));
            it.getSources().from(binary.getCompileTask().map((Transformer<FileCollection, CppCompile>) cppCompile -> cppCompile.getSource()));
            it.setOutputFile(projectLayout.getBuildDirectory().file("cpp-support/" + binary.getName() + "/compile_commands.json").get().getAsFile());
        }).flatMap(it -> it.getCompileCommandsFileLocation());
    }
}
