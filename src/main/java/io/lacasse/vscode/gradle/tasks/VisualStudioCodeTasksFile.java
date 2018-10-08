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

package io.lacasse.vscode.gradle.tasks;

import io.lacasse.vscode.schemas.task.TaskDescription;
import io.lacasse.vscode.gradle.generator.JsonPersistableConfigurationObject;
import io.lacasse.vscode.schemas.task.TaskConfiguration;

import java.util.List;

public class VisualStudioCodeTasksFile extends JsonPersistableConfigurationObject<TaskConfiguration> {
    private List<TaskDescription> vsCodeTasks;

    protected VisualStudioCodeTasksFile() {
        super(TaskConfiguration.class);
    }

    @Override
    protected TaskConfiguration newRootObject() {
        return new TaskConfiguration();
    }

    @Override
    protected void store(TaskConfiguration rootObject) {
        rootObject.getTasks().addAll(vsCodeTasks);
    }

    @Override
    protected void load(TaskConfiguration rootObject) {

    }

    @Override
    protected String getDefaultResourceName() {
        return "tasks.json";
    }

    public void setTaskDescriptions(List<TaskDescription> vsCodeTasks) {
        this.vsCodeTasks = vsCodeTasks;
    }
}
