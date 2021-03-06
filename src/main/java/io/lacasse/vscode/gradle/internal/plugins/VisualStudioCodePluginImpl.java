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

import io.lacasse.vscode.gradle.VisualStudioCodeExtension;
import io.lacasse.vscode.gradle.VisualStudioCodeRootExtension;
import io.lacasse.vscode.gradle.internal.DefaultVisualStudioCodeExtension;
import io.lacasse.vscode.gradle.internal.DefaultVisualStudioCodeRootExtension;
import io.lacasse.vscode.gradle.internal.tasks.GenerateCppPropertiesFileTask;
import io.lacasse.vscode.gradle.internal.tasks.GenerateLaunchFileTask;
import io.lacasse.vscode.gradle.internal.tasks.GenerateTasksFileTask;
import io.lacasse.vscode.gradle.internal.tasks.GenerateWorkspaceFileTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.CppComponent;
import org.gradle.language.cpp.CppExecutable;
import org.gradle.language.cpp.CppLibrary;
import org.gradle.language.cpp.CppSharedLibrary;
import org.gradle.language.cpp.CppStaticLibrary;
import org.gradle.plugins.ide.internal.IdeArtifactRegistry;
import org.gradle.plugins.ide.internal.IdePlugin;
import org.gradle.plugins.ide.internal.IdeProjectMetadata;
import org.gradle.util.GradleVersion;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class VisualStudioCodePluginImpl extends IdePlugin {
    private final IdeArtifactRegistry artifactRegistry;

    @Inject
    public VisualStudioCodePluginImpl(IdeArtifactRegistry artifactRegistry) {
        this.artifactRegistry = artifactRegistry;
    }

    @Override
    protected String getLifecycleTaskName() {
        return "vscode";
    }

    @Override
    protected void onApply(final Project project) {
        DefaultVisualStudioCodeExtension visualStudioCode = createVisualStudioCodeExtension(project.getExtensions(), project.getObjects());

        configureLifecycleTask(getLifecycleTask());
        configureCleanTask((TaskProvider<? extends Delete>) getCleanTask(), visualStudioCode);
        configureVisualStudioCodeExtension(visualStudioCode);

        TaskProvider<GenerateTasksFileTask> generateTasksFileTask = createTaskFileTask(project.getTasks(), project.getProviders(), visualStudioCode);
        TaskProvider<GenerateLaunchFileTask> generateLaunchFileTask = createLaunchFileTask(project.getTasks(), project.getProviders(), visualStudioCode);
        TaskProvider<GenerateCppPropertiesFileTask> generateCppPropertiesFileTask = createCppPropertiesFileTask(project.getTasks(), visualStudioCode);

        TaskProvider<Task> generateProjectTask = createProjectTask(project.getTasks(), generateTasksFileTask, generateCppPropertiesFileTask, generateLaunchFileTask);
        addWorker(generateProjectTask, generateProjectTask.getName());

        artifactRegistry.registerIdeProject(new VisualStudioCodeProjectMetadata(project.getProjectDir(), generateProjectTask));

        if (isRoot()) {
            TaskProvider<GenerateWorkspaceFileTask> generateWorkspaceFileTask = createWorkspaceFileTask(project.getTasks(), artifactRegistry, (DefaultVisualStudioCodeRootExtension)visualStudioCode);
            addWorker(generateWorkspaceFileTask, generateWorkspaceFileTask.getName());
        }

        configureNativePlugin(visualStudioCode);
    }

    private void configureVisualStudioCodeExtension(DefaultVisualStudioCodeExtension visualStudioCode) {
        if (isRoot()) {
            ((DefaultVisualStudioCodeRootExtension)visualStudioCode).getWorkspace().getLocation().set(project.file(project.getName() + ".code-workspace"));
        }
        visualStudioCode.getProject().getLocation().set(project.file(".vscode"));
    }

    private static TaskProvider<Task> createProjectTask(TaskContainer tasks, TaskProvider<? extends Task>... generateProjectTasks) {
        return tasks.register("vscodeProject", task -> {
            task.dependsOn(Arrays.asList(generateProjectTasks));
        });
    }

    private static void configureLifecycleTask(TaskProvider<? extends Task> lifecycleTask) {
        lifecycleTask.configure(withDescription("Generates Visual Studio Code project files"));
    }

    private static void configureCleanTask(TaskProvider<? extends Delete> cleanTask, DefaultVisualStudioCodeExtension visualStudioCode) {
        cleanTask.configure((it) -> it.delete(visualStudioCode.getProject().getLocation()));
    }

    private static TaskProvider<GenerateLaunchFileTask> createLaunchFileTask(TaskContainer tasks, ProviderFactory providerFactory, DefaultVisualStudioCodeExtension visualStudioCode) {
        return tasks.register("vscodeLaunch", GenerateLaunchFileTask.class, task -> {
            task.getLaunchFileLocation().set(visualStudioCode.getProject().getLocation().file("launch.json"));
            task.getLaunches().set(providerFactory.provider(() -> visualStudioCode.getProject().getLaunches()));
        });
    }

    private DefaultVisualStudioCodeExtension createVisualStudioCodeExtension(ExtensionContainer extensions, ObjectFactory objectFactory) {
        if (isRoot()) {
            DefaultVisualStudioCodeRootExtension visualStudioCode = objectFactory.newInstance(DefaultVisualStudioCodeRootExtension.class);
            extensions.add(VisualStudioCodeRootExtension.class, "visualStudioCode", visualStudioCode);
            return visualStudioCode;
        }

        DefaultVisualStudioCodeExtension visualStudioCode = objectFactory.newInstance(DefaultVisualStudioCodeExtension.class);
        extensions.add(VisualStudioCodeExtension.class, "visualStudioCode", visualStudioCode);
        return visualStudioCode;
    }

    private static TaskProvider<GenerateCppPropertiesFileTask> createCppPropertiesFileTask(TaskContainer tasks, DefaultVisualStudioCodeExtension visualStudioCode) {
        return tasks.register("generateCppProperties", GenerateCppPropertiesFileTask.class, it -> {
            it.getConfigurations().set(visualStudioCode.getProject().getConfigurations());
            it.getCppPropertiesFileLocation().set(visualStudioCode.getProject().getLocation().file("c_cpp_properties.json"));
        });
    }

    private void configureNativePlugin(VisualStudioCodeExtension visualStudioCode) {
        project.getPluginManager().withPlugin("cpp-application", appliedPlugin -> configureNativeComponent(visualStudioCode, (CppComponent) project.getExtensions().getByName("application")));

        project.getPluginManager().withPlugin("cpp-library", appliedPlugin -> configureNativeComponent(visualStudioCode, (CppComponent) project.getExtensions().getByName("library")));
    }

    private void configureNativeComponent(VisualStudioCodeExtension visualStudioCode, CppComponent component) {
        boolean hasStaticAndSharedLinkage = false;

        component.getBinaries().whenElementKnown(binary -> {
            visualStudioCode.getProject().cppConfiguration(binary.getName(), (it) -> it.configureFromBinary(binary));
            if (binary instanceof CppSharedLibrary) {
                visualStudioCode.getProject().buildTask("Build " + binary.getName(), ((CppSharedLibrary) binary).getLinkTask(), isDefaultBuildTask((CppLibrary) component, binary));
            } else if (binary instanceof CppStaticLibrary) {
                visualStudioCode.getProject().buildTask("Build " + binary.getName(), ((CppStaticLibrary) binary).getCreateTask(), isDefaultBuildTask((CppLibrary) component, binary));
            } else if (binary instanceof CppExecutable) {
                // TODO: Implementation knowledge on what is the development binary
                visualStudioCode.getProject().buildTask("Build " + binary.getName(), ((CppExecutable) binary).getLinkTask(), !binary.isOptimized());
            } else {
                throw new IllegalArgumentException();
            }
        });
    }

    private boolean isDefaultBuildTask(CppLibrary library, CppBinary binary) {
        if (library.getLinkage().get().size() == 2) {
            if (binary instanceof CppSharedLibrary && !binary.isOptimized()) {
                return true;
            }
            return false;
        }

        // TODO: Implementation knowledge on what is the development binary
        return !binary.isOptimized();
    }

    private static TaskProvider<GenerateWorkspaceFileTask> createWorkspaceFileTask(TaskContainer tasks, IdeArtifactRegistry artifactRegistry, DefaultVisualStudioCodeRootExtension visualStudioCode) {
        return tasks.register("vscodeWorkspace", GenerateWorkspaceFileTask.class, task -> {
            task.getWorkspaceLocation().set(visualStudioCode.getWorkspace().getLocation());
            task.getProjectLocations().from(artifactRegistry.getIdeProjectFiles(VisualStudioCodeProjectMetadata.class));
        });
    }

    private static TaskProvider<GenerateTasksFileTask> createTaskFileTask(TaskContainer tasks, ProviderFactory providerFactory, DefaultVisualStudioCodeExtension visualStudioCode) {
        return tasks.register("vscodeTasks", GenerateTasksFileTask.class, task -> {
            task.getTaskFileLocation().set(visualStudioCode.getProject().getLocation().file("tasks.json"));
            task.getTasks().set(providerFactory.provider(() -> visualStudioCode.getProject().getGradleTasks()));
        });
    }

    private static class VisualStudioCodeProjectMetadata implements IdeProjectMetadata {
        private final File projectDir;
        private final TaskProvider<? extends Task> lifecycleTask;

        VisualStudioCodeProjectMetadata(File projectDir, TaskProvider<? extends Task> lifecycleTask) {
            this.projectDir = projectDir;
            this.lifecycleTask = lifecycleTask;
        }

        @Override
        public File getFile() {
            return projectDir;
        }

        @Override
        public Set<? extends Task> getGeneratorTasks() {
            return Collections.singleton(lifecycleTask.get());
        }
    }
}
