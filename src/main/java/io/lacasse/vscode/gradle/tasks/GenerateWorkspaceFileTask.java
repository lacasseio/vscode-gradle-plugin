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

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.io.File;

public class GenerateWorkspaceFileTask extends JsonGeneratorTask<VisualStudioCodeWorkspaceFile> {
    private final RegularFileProperty workspaceLocation = newInputFile();
    private final ConfigurableFileCollection projectLocations = getProject().files();

    @Override
    protected void configure(VisualStudioCodeWorkspaceFile object) {
        object.setProjectLocations(projectLocations.getFiles());
    }

    @Override
    protected VisualStudioCodeWorkspaceFile create() {
        return new VisualStudioCodeWorkspaceFile();
    }

    @Input
    public ConfigurableFileCollection getProjectLocations() {
        return projectLocations;
    }

    @Internal
    public RegularFileProperty getWorkspaceLocation() {
        return workspaceLocation;
    }

    @Override
    public File getOutputFile() {
        return workspaceLocation.getAsFile().get();
    }

    @Override
    public void setOutputFile(File outputFile) {
        workspaceLocation.set(outputFile);
    }

    @Override
    public File getInputFile() {
        return null;
    }
}
