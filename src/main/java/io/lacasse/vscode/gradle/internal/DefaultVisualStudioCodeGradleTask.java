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
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskProvider;

public class DefaultVisualStudioCodeGradleTask implements VisualStudioCodeGradleTask {
    private final String name;
    private final TaskProvider<?> task;
    private final boolean build;
    private final boolean test;
    private final boolean isDefault;
    private final boolean isBackground;
    private final String problemMatcher;

    public DefaultVisualStudioCodeGradleTask(String name, TaskProvider<?> task, boolean build, boolean test, boolean isDefault, boolean isBackground, String problemMatcher) {
        this.name = name;
        this.task = task;
        this.build = build;
        this.test = test;
        this.isDefault = isDefault;
        this.isBackground = isBackground;
        this.problemMatcher = problemMatcher;
    }

    @Input
    public boolean isBuild() {
        return build;
    }

    @Input
    public boolean isTest() {
        return test;
    }

    @Input
    public boolean isDefault() {
        return isDefault;
    }

    @Input
    public boolean isBackground() {
        return isBackground;
    }

    @Input
    @Optional
    public String getProblemMatcher() {
        return problemMatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Input
    public String getTaskPath() {
        return task.get().getPath();
    }

    @Internal
    @Override
    public TaskProvider<?> getTask() {
        return task;
    }
}
