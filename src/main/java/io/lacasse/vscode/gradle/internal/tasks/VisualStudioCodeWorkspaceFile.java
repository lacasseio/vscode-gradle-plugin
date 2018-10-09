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

package io.lacasse.vscode.gradle.internal.tasks;

import io.lacasse.vscode.internal.schemas.workspace.Workspace;
import io.lacasse.vscode.internal.schemas.workspace.WorkspaceFolder;
import io.lacasse.vscode.gradle.internal.generator.JsonPersistableConfigurationObject;

import java.io.File;
import java.util.Set;

public class VisualStudioCodeWorkspaceFile extends JsonPersistableConfigurationObject<Workspace> {
    private Set<File> projectLocations;

    public VisualStudioCodeWorkspaceFile() {
        super(Workspace.class);
    }

    @Override
    protected Workspace newRootObject() {
        return new Workspace();
    }

    @Override
    protected void store(Workspace rootObject) {
        for (File projectLocation : projectLocations) {
            WorkspaceFolder workspaceFolder = new WorkspaceFolder();
            workspaceFolder.setPath(projectLocation.getAbsolutePath());
            rootObject.getFolders().add(workspaceFolder);
        }
    }

    @Override
    protected void load(Workspace rootObject) {

    }

    @Override
    protected String getDefaultResourceName() {
        return "default.code-workspace";
    }

    public void setProjectLocations(Set<File> projectLocations) {
        this.projectLocations = projectLocations;
    }
}
