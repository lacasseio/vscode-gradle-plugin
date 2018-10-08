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

package io.lacasse.vscode.schemas;

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VisualStudioCodeCppProperties {
    private final int version = 4;
    private final List<Configuration> configurations = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public static class Configuration {
        private String name;
        private IntelliSenseMode intelliSenseMode;
        private final List<String> includePath = new ArrayList<>();
        private final List<String> defines = new ArrayList<>();
        private String compilerPath;
        private final CStandard cStandard = CStandard.C11;
        private final CppStandard cppStandard = CppStandard.CPP17;
        private String compileCommands;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public IntelliSenseMode getIntelliSenseMode() {
            return intelliSenseMode;
        }

        public void setIntelliSenseMode(IntelliSenseMode intelliSenseMode) {
            this.intelliSenseMode = intelliSenseMode;
        }

        public List<String> getIncludePath() {
            return includePath;
        }

        public List<String> getDefines() {
            return defines;
        }

        public String getCompilerPath() {
            return compilerPath;
        }

        public void setCompilerPath(String compilerPath) {
            this.compilerPath = compilerPath;
        }

        public CStandard getcStandard() {
            return cStandard;
        }

        public CppStandard getCppStandard() {
            return cppStandard;
        }

        public String getCompileCommands() {
            return compileCommands;
        }

        public void setCompileCommands(String compileCommands) {
            this.compileCommands = compileCommands;
        }

        public enum IntelliSenseMode {
            @SerializedName("clang-x64")
            CLANG,

            @SerializedName("gcc-x64")
            GCC;
        }

        public enum CStandard {
            @SerializedName("c11")
            C11;
        }

        public enum CppStandard {
            @SerializedName("c++17")
            CPP17;
        }
    }
}
