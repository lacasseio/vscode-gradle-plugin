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

import com.google.common.collect.ImmutableList;
import io.lacasse.vscode.gradle.VisualStudioCodeCppConfiguration;
import io.lacasse.vscode.gradle.VisualStudioCodeGradleTask;
import io.lacasse.vscode.gradle.VisualStudioCodeGdbLaunch;
import io.lacasse.vscode.gradle.VisualStudioCodeProject;
import io.lacasse.vscode.gradle.internal.tasks.GenerateCompileCommandsFileTask;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
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

public class DefaultVisualStudioCodeProject implements VisualStudioCodeProject {
    private final List<DefaultVisualStudioCodeGradleTask> gradleTasks = new ArrayList<>();
    private final List<DefaultVisualStudioCodeCppConfiguration> configurations = new ArrayList<>();
    private final List<DefaultVisualStudioCodeGdbLaunch> launches = new ArrayList<>();
    private final ObjectFactory objectFactory;
    private final DirectoryProperty location;

    @Inject
    public DefaultVisualStudioCodeProject(ObjectFactory objectFactory, ProjectLayout projectLayout) {
        this.objectFactory = objectFactory;
        this.location = projectLayout.directoryProperty();
    }

    @Override
    public DirectoryProperty getLocation() {
        return location;
    }

    @Override
    public VisualStudioCodeGradleTask task(String name, TaskProvider<? extends Task> task) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, false, false, false, null);
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task) {
        return testTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, true, isDefault, false, null);
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task) {
        return buildTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, true, false, isDefault, false, "$gcc");
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask backgroundTask(String name, TaskProvider<? extends Task> task) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, false, false, true, null);
        gradleTasks.add(result);
        return result;
    }

    public List<DefaultVisualStudioCodeGradleTask> getGradleTasks() {
        return ImmutableList.copyOf(gradleTasks);
    }

    @Override
    public void cppConfiguration(String name, Action<? super VisualStudioCodeCppConfiguration> action) {
        DefaultVisualStudioCodeCppConfiguration result = objectFactory.newInstance(DefaultVisualStudioCodeCppConfiguration.class, name);
        configurations.add(result);
        action.execute(result);
    }

    // Kind of public... from Groovy's perspective.
    public Action<? super VisualStudioCodeCppConfiguration> fromBinary(Project project, CppBinary binary) {
        return (Action<VisualStudioCodeCppConfiguration>) it -> {
            Provider<? extends CppCompile> compileTask = binary.getCompileTask();

            it.getIncludes().from(compileTask.map((Transformer<Object, CppCompile>) cppCompile -> cppCompile.getIncludes().plus(cppCompile.getSystemIncludes())));
            it.getDefines().set(compileTask.map((Transformer<Iterable<? extends String>, CppCompile>) cppCompile -> {
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

            it.getCompileCommandsLocation().set(generateCompileCommandsFileFor(project.getTasks(), binary));
        };
    }

    private static Provider<RegularFile> generateCompileCommandsFileFor(TaskContainer tasks, CppBinary binary) {
        return tasks.register(GenerateCompileCommandsFileTask.taskName(binary), GenerateCompileCommandsFileTask.class, it -> {
            ProviderFactory providerFactory = it.getProject().getProviders();
            ProjectLayout projectLayout = it.getProject().getLayout();

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
        }).map(it -> it.getCompileCommandsFileLocation().get());
    }

    public List<DefaultVisualStudioCodeCppConfiguration> getConfigurations() {
        return ImmutableList.copyOf(configurations);
    }

    @Override
    public void gdbLaunch(String name, Action<? super VisualStudioCodeGdbLaunch> action) {
        DefaultVisualStudioCodeGdbLaunch result = objectFactory.newInstance(DefaultVisualStudioCodeGdbLaunch.class, name);
        launches.add(result);
        action.execute(result);
    }

    public List<DefaultVisualStudioCodeGdbLaunch> getLaunches() {
        return ImmutableList.copyOf(launches);
    }
}
