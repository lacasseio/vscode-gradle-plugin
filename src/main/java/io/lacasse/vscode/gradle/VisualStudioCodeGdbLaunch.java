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

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;

/**
 * A GDB launch configuration for Visual Studio Code.
 *
 * @since 1.0
 */
public interface VisualStudioCodeGdbLaunch {
    /**
     * Returns the display name to use in Visual Studio Code for this GDB launch.
     */
    String getDisplayName();

    /**
     * Returns the program executable to be launched by GDB.
     */
    RegularFileProperty getProgramLocation();

    /**
     * Returns the GDB executable location.
     */
    RegularFileProperty getGdbLocation();

    /**
     * Returns the preparatory launch Gradle task.
     */
    Property<VisualStudioCodeGradleTask> getPreLaunchGradleTask();
}
