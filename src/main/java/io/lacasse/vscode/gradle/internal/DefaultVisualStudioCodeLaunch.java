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

import io.lacasse.vscode.gradle.VisualStudioCodeGradleTask;
import io.lacasse.vscode.gradle.VisualStudioCodeLaunch;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;

public class DefaultVisualStudioCodeLaunch implements VisualStudioCodeLaunch {
    private final String name;
    private final RegularFileProperty programLocation;
    private final RegularFileProperty debuggerLocation;
    private final Property<VisualStudioCodeGradleTask> preLaunchGradleTask;

    @Inject
    public DefaultVisualStudioCodeLaunch(String name, ProjectLayout projectLayout, ObjectFactory objectFactory) {
        this.name = name;
        this.programLocation = projectLayout.fileProperty();
        this.debuggerLocation = projectLayout.fileProperty();
        this.preLaunchGradleTask = objectFactory.property(VisualStudioCodeGradleTask.class);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RegularFileProperty getProgramLocation() {
        return programLocation;
    }

    @Override
    public RegularFileProperty getDebuggerLocation() {
        return debuggerLocation;
    }

    @Nested
    @Override
    public Property<VisualStudioCodeGradleTask> getPreLaunchGradleTask() {
        return preLaunchGradleTask;
    }
}
