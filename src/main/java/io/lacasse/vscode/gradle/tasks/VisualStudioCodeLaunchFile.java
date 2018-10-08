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
import io.lacasse.vscode.schemas.launch.LaunchConfiguration;
import io.lacasse.vscode.schemas.launch.LaunchDescription;

import java.util.List;

public class VisualStudioCodeLaunchFile extends JsonPersistableConfigurationObject<LaunchConfiguration> {
    private List<LaunchDescription> launches;

    protected VisualStudioCodeLaunchFile() {
        super(LaunchConfiguration.class);
    }

    @Override
    protected LaunchConfiguration newRootObject() {
        return new LaunchConfiguration();
    }

    @Override
    protected void store(LaunchConfiguration rootObject) {
        rootObject.getConfigurations().addAll(launches);
    }

    @Override
    protected void load(LaunchConfiguration rootObject) {

    }

    @Override
    protected String getDefaultResourceName() {
        return "launch.json";
    }

    public void setLaunches(List<LaunchDescription> launches) {
        this.launches = launches;
    }
}
