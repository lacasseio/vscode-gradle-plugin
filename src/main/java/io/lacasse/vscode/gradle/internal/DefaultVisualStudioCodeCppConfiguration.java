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

import io.lacasse.vscode.gradle.VisualStudioCodeCppConfiguration;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;

import javax.inject.Inject;

public class DefaultVisualStudioCodeCppConfiguration implements VisualStudioCodeCppConfiguration {
    private final String name;
    private final ConfigurableFileCollection includes;
    private final ListProperty<String> defines;
    private final RegularFileProperty compileCommandsLocation;

    @Inject
    public DefaultVisualStudioCodeCppConfiguration(String name, ProjectLayout projectLayout, ObjectFactory objectFactory) {
        this.name = name;
        this.includes = projectLayout.configurableFiles();
        this.defines = objectFactory.listProperty(String.class);
        this.compileCommandsLocation = projectLayout.fileProperty();
    }

    @Input
    @Override
    public String getDisplayName() {
        return name;
    }

    @InputFiles
    @Override
    public ConfigurableFileCollection getIncludes() {
        return includes;
    }

    @Input
    @Override
    public ListProperty<String> getDefines() {
        return defines;
    }

    @InputFile
    @Override
    public RegularFileProperty getCompileCommandsLocation() {
        return compileCommandsLocation;
    }
}
