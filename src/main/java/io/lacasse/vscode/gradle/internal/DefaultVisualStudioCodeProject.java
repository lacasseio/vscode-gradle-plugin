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
import io.lacasse.vscode.gradle.VisualStudioCodeConfiguration;
import io.lacasse.vscode.gradle.VisualStudioCodeGradleTask;
import io.lacasse.vscode.gradle.VisualStudioCodeLaunch;
import io.lacasse.vscode.gradle.VisualStudioCodeProject;
import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.tasks.CppCompile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultVisualStudioCodeProject implements VisualStudioCodeProject {
    private final List<DefaultVisualStudioCodeGradleTask> tasks = new ArrayList<>();
    private final List<DefaultVisualStudioCodeConfiguration> configurations = new ArrayList<>();
    private final List<DefaultVisualStudioCodeLaunch> launches = new ArrayList<>();
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
        tasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task) {
        return testTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, true, isDefault, false, null);
        tasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task) {
        return buildTask(name, task, false);
    }

    @Override
    public VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task, boolean isDefault) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, true, false, isDefault, false, "$gcc");
        tasks.add(result);
        return result;
    }

    @Override
    public VisualStudioCodeGradleTask backgroundTask(String name, TaskProvider<? extends Task> task) {
        DefaultVisualStudioCodeGradleTask result = new DefaultVisualStudioCodeGradleTask(name, task, false, false, false, true, null);
        tasks.add(result);
        return result;
    }

    public List<DefaultVisualStudioCodeGradleTask> getGradleTasks() {
        return ImmutableList.copyOf(tasks);
    }

    @Override
    public void configuration(String name, Action<? super VisualStudioCodeConfiguration> action) {
        DefaultVisualStudioCodeConfiguration result = objectFactory.newInstance(DefaultVisualStudioCodeConfiguration.class, name);
        configurations.add(result);
        action.execute(result);
    }

    public static Action<? super VisualStudioCodeConfiguration> fromCompileTask(Provider<? extends CppCompile> compileTask) {
        return (Action<VisualStudioCodeConfiguration>) it -> {
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
        };
    }

    public List<DefaultVisualStudioCodeConfiguration> getConfigurations() {
        return ImmutableList.copyOf(configurations);
    }

    @Override
    public void gdbLaunch(String name, Action<? super VisualStudioCodeLaunch> action) {
        DefaultVisualStudioCodeLaunch result = objectFactory.newInstance(DefaultVisualStudioCodeLaunch.class, name);
        launches.add(result);
        action.execute(result);
    }

    public List<DefaultVisualStudioCodeLaunch> getLaunches() {
        return ImmutableList.copyOf(launches);
    }
}
