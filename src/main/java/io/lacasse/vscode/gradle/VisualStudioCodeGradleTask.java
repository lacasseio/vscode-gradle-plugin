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

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

/**
 * A Gradle task reference to be called from Visual Studio Code.
 *
 * @since 1.0
 */
public interface VisualStudioCodeGradleTask {
    /**
     * Returns the display name to use in Visual Studio Code for the referenced Gradle task.
     */
    String getDisplayName();

    TaskProvider<? extends Task> getTask();
}
