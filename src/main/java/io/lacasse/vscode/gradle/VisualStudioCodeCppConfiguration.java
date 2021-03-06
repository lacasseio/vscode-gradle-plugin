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

package io.lacasse.vscode.gradle;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.nativeplatform.NativeBinarySpec;

/**
 * A C++ configuration used for intellisense by the {@code ms-vscode.cpptools} Visual Studio Code extension.
 *
 * @since 1.0
 */
public interface VisualStudioCodeCppConfiguration {
    /**
     * Returns the display name of the configuration used for Visual Studio Code intellisense.
     */
    String getDisplayName();

    /**
     * Returns the include paths for header resolving.
     */
    ConfigurableFileCollection getIncludes();

    /**
     * Returns the list of defines in the format of {@code DEFINE} or {@code DEFINE=VALUE}.
     */
    ListProperty<String> getDefines();

    /**
     * Returns the location of the compile_commands.json file.
     */
    RegularFileProperty getCompileCommandsLocation();

    /**
     * Configure C++ configuration from a {@code CppBinary}.
     */
    void configureFromBinary(CppBinary binary);

    /**
     * Configure C++ configuration from a {@code NativeBinarySpec} (software model).
     */
    void configureFromBinary(NativeBinarySpec binary);
}
