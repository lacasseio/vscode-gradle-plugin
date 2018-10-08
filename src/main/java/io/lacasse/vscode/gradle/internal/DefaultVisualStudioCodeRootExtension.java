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

import io.lacasse.vscode.gradle.VisualStudioCodeRootExtension;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class DefaultVisualStudioCodeRootExtension extends DefaultVisualStudioCodeExtension implements VisualStudioCodeRootExtension {
    private final DefaultVisualStudioCodeWorkspace workspace;

    @Inject
    public DefaultVisualStudioCodeRootExtension(ObjectFactory objectFactory) {
        super(objectFactory);
        workspace = objectFactory.newInstance(DefaultVisualStudioCodeWorkspace.class);
    }

    @Override
    public DefaultVisualStudioCodeWorkspace getWorkspace() {
        return workspace;
    }
}
