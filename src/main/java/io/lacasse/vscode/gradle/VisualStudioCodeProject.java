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

package io.lacasse.vscode.gradle;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

/**
 * A visual studio code project, created from one or more project variant.
 *
 * @since 1.0
 */
public interface VisualStudioCodeProject {
    /**
     * Returns a {@code Provider} of the project location.
     */
    Provider<Directory> getLocation();

    VisualStudioCodeGradleTask task(String name, TaskProvider<? extends Task> task);

    VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task);
    VisualStudioCodeGradleTask testTask(String name, TaskProvider<? extends Task> task, boolean isDefault);

    VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task);
    VisualStudioCodeGradleTask buildTask(String name, TaskProvider<? extends Task> task, boolean isDefault);

    VisualStudioCodeGradleTask backgroundTask(String name, TaskProvider<? extends Task> task);

    void cppConfiguration(String name, Action<? super VisualStudioCodeCppConfiguration> action);

    void gdbLaunch(String name, Action<? super VisualStudioCodeGdbLaunch> action);
}
