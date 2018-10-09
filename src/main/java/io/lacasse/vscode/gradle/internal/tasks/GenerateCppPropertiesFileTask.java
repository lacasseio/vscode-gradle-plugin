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

import io.lacasse.vscode.gradle.VisualStudioCodeConfiguration;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;

import java.io.File;

public class GenerateCppPropertiesFileTask extends JsonGeneratorTask<VisualStudioCodeCppPropertiesFile> {
    private final RegularFileProperty cppPropertiesFileLocation = newOutputFile();
    private final ListProperty<VisualStudioCodeConfiguration> configurations = getProject().getObjects().listProperty(VisualStudioCodeConfiguration.class);

    public GenerateCppPropertiesFileTask() {
//        dependsOn((Callable<List<TaskProvider<?>>>) () -> configurations.get().stream().map(DefaultVisualStudioCodeConfiguration::getTask).collect(Collectors.toList()));
    }

    @Override
    protected void configure(VisualStudioCodeCppPropertiesFile object) {
        for (VisualStudioCodeConfiguration configuration : configurations.get()) {
            object.configuration(configuration.getName(), configuration.getCompileCommandsLocation().get().getAsFile(), configuration.getIncludes(), configuration.getDefines().get());
        }
    }

    @Override
    protected VisualStudioCodeCppPropertiesFile create() {
        return new VisualStudioCodeCppPropertiesFile();
    }

    @Override
    public File getInputFile() {
        return null;
    }

    @Internal
    public RegularFileProperty getCppPropertiesFileLocation() {
        return cppPropertiesFileLocation;
    }

    @Override
    public File getOutputFile() {
        return cppPropertiesFileLocation.getAsFile().get();
    }

    @Override
    public void setOutputFile(File outputFile) {
        cppPropertiesFileLocation.set(outputFile);
    }

    @Nested
    public ListProperty<VisualStudioCodeConfiguration> getConfigurations() {
        return configurations;
    }
}
