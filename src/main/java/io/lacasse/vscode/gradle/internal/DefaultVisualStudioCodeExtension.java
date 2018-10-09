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

import io.lacasse.vscode.gradle.VisualStudioCodeExtension;
import io.lacasse.vscode.gradle.VisualStudioCodeProject;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class DefaultVisualStudioCodeExtension implements VisualStudioCodeExtension {
    private final List<DefaultVisualStudioCodeCppConfiguration> configuration = new ArrayList<>();
    private final List<DefaultVisualStudioCodeGdbLaunch> launches = new ArrayList<>();
    private final DefaultVisualStudioCodeProject project;

    @Inject
    public DefaultVisualStudioCodeExtension(ObjectFactory objectFactory) {
        this.project = objectFactory.newInstance(DefaultVisualStudioCodeProject.class);
    }

    @Override
    public DefaultVisualStudioCodeProject getProject() {
        return project;
    }

    @Override
    public void project(Action<? super VisualStudioCodeProject> action) {
        action.execute(project);
    }
}
