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

import io.lacasse.vscode.gradle.generator.JsonPersistableConfigurationObject;
import io.lacasse.vscode.schemas.VisualStudioCodeCppProperties;
import org.gradle.api.file.FileCollection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisualStudioCodeCppPropertiesFile extends JsonPersistableConfigurationObject<VisualStudioCodeCppProperties> {
    private final List<VisualStudioCodeCppProperties.Configuration> configurations = new ArrayList<>();
    protected VisualStudioCodeCppPropertiesFile() {
        super(VisualStudioCodeCppProperties.class);
    }

    @Override
    protected VisualStudioCodeCppProperties newRootObject() {
        return new VisualStudioCodeCppProperties();
    }

    @Override
    protected void store(VisualStudioCodeCppProperties rootObject) {
        rootObject.getConfigurations().addAll(configurations);
    }

    @Override
    protected void load(VisualStudioCodeCppProperties rootObject) {

    }

    @Override
    protected String getDefaultResourceName() {
        return "c_cpp_properties.json";
    }

    public void configuration(String name, File regularFile, FileCollection includes, List<String> macros) {
        VisualStudioCodeCppProperties.Configuration conf = new VisualStudioCodeCppProperties.Configuration();
        conf.setCompileCommands(regularFile.getAbsolutePath());
        conf.setName(name);
        for (File f : includes) {
            conf.getIncludePath().add(f.getAbsolutePath());
        }
        conf.getDefines().addAll(macros);

        configurations.add(conf);
    }
}
