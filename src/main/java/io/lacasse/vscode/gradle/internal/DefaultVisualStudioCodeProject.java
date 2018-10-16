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
import java.util.concurrent.Callable;

public class DefaultVisualStudioCodeProject implements VisualStudioCodeProject {
    private final List<DefaultVisualStudioCodeGradleTask> gradleTasks = new ArrayList<>();
    private final List<DefaultVisualStudioCodeCppConfiguration> configurations = new ArrayList<>();
    private final List<DefaultVisualStudioCodeGdbLaunch> launches = new ArrayList<>();
    private final ObjectFactory objectFactory;
    private final DirectoryProperty location;
    private final ProjectLayout projectLayout;
    private final ProviderFactory providerFactory;

    @Inject
    public DefaultVisualStudioCodeProject(ObjectFactory objectFactory, ProjectLayout projectLayout, ProviderFactory providerFactory) {
        this.objectFactory = objectFactory;
        this.location = projectLayout.directoryProperty();
        this.projectLayout = projectLayout;
        this.providerFactory = providerFactory;
    }

    @Override
    public DirectoryProperty getLocation() {
        return location;
    }

    @Override
    public VisualStudioCodeGradleTask task(String name, Provider<? extends Task> task) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, false, false, false, null);
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, Provider<? extends Task> task) {
        return testTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, Provider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, true, isDefault, false, null);
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, Provider<? extends Task> task) {
        return buildTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, Provider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, true, false, isDefault, false, "$gcc");
        gradleTasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask backgroundTask(String name, Provider<? extends Task> task) {
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
