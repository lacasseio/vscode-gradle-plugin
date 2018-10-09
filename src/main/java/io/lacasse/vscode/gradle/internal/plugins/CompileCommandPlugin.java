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

package io.lacasse.vscode.gradle.internal.plugins;

import io.lacasse.vscode.gradle.tasks.GenerateCompileCommandsFileTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.ToolType;

import javax.inject.Inject;
import java.io.File;

public class CompileCommandPlugin implements Plugin<Project> {
    private final ProviderFactory providerFactory;
    private final ProjectLayout projectLayout;

    @Inject
    public CompileCommandPlugin(ProviderFactory providerFactory, ProjectLayout projectLayout) {
        this.providerFactory = providerFactory;
        this.projectLayout = projectLayout;
    }

    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("cpp-library", appliedPlugin -> {
            createCompileCommandTask(project.getTasks(), (CppComponent) project.getExtensions().getByName("library"));
        });

        project.getPluginManager().withPlugin("cpp-application", appliedPlugin -> {
            createCompileCommandTask(project.getTasks(), (CppComponent) project.getExtensions().getByName("application"));
        });
    }

    private void createCompileCommandTask(TaskContainer tasks, CppComponent component) {
        component.getBinaries().whenElementKnown((Action<CppBinary>) binary -> {
            // TODO: Capitalize binary name
            tasks.register(GenerateCompileCommandsFileTask.taskName(binary), GenerateCompileCommandsFileTask.class, it -> {

                it.setGroup("C++ Support");
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
            });
        });
    }
}
