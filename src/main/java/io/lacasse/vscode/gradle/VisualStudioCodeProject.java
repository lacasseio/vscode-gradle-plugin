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

    /**
     * Creates a task reference to be used in Visual Studio Code.
     *
     * @param displayName the task name as seen in Visual Studio Code
     * @param task the Gradle task to reference
     * @return a Gradle task alias for Visual Studio Code
     */
    VisualStudioCodeGradleTask task(String displayName, Provider<? extends Task> task);

    /**
     * Creates a test task reference to be used in Visual Studio Code. The task can optionally be mark as the default test task.
     *
     * @param displayName the task name as seen in Visual Studio Code
     * @param task the Gradle task to reference
     * @return a Gradle task alias for Visual Studio Code
     */
    VisualStudioCodeGradleTask testTask(String displayName, Provider<? extends Task> task);
    VisualStudioCodeGradleTask testTask(String displayName, Provider<? extends Task> task, boolean isDefault);

    /**
     * Creates a build task reference to be used in Visual Studio Code. The task can optionally be mark as the default build task.
     *
     * @param displayName the task name as seen in Visual Studio Code
     * @param task the Gradle task to reference
     * @return a Gradle task alias for Visual Studio Code
     */
    VisualStudioCodeGradleTask buildTask(String displayName, Provider<? extends Task> task);
    VisualStudioCodeGradleTask buildTask(String displayName, Provider<? extends Task> task, boolean isDefault);

    /**
     * Creates a background task reference to be used in Visual Studio Code.
     *
     * @param displayName the task name as seen in Visual Studio Code
     * @param task the Gradle task to reference
     * @return a Gradle task alias for Visual Studio Code
     */
    VisualStudioCodeGradleTask backgroundTask(String displayName, Provider<? extends Task> task);

    /**
     * Creates a C++ configuration for Visual Studio Code intellisense.
     *
     * @param displayName the configuration name as seen in Visual Studio Code
     * @param action the configuration action
     */
    void cppConfiguration(String displayName, Action<? super VisualStudioCodeCppConfiguration> action);

    /**
     * Creates a GDB launch configuration for Visual Studio Code debugger.
     *
     * @param displayName the launch configuration name as seen in Visual Studio Code
     * @param action the configuration action
     */
    void gdbLaunch(String displayName, Action<? super VisualStudioCodeGdbLaunch> action);
}
