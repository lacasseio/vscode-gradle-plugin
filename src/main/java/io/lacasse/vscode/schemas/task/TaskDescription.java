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

package io.lacasse.vscode.schemas.task;

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName;

public class TaskDescription {
    private String label;
    private final Type type = Type.SHELL;
    private String command;
    private Object group;
    private Object problemMatcher;
    private boolean isBackground;
    private final PresentationOptions presentation = new PresentationOptions();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Type getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getGroup() {
        return group;
    }

    public void setGroup(Object group) {
        this.group = group;
    }

    public Object getProblemMatcher() {
        return problemMatcher;
    }

    public void setProblemMatcher(Object problemMatcher) {
        this.problemMatcher = problemMatcher;
    }

    public boolean isBackground() {
        return isBackground;
    }

    public void setBackground(boolean isBackground) {
        this.isBackground = isBackground;
    }

    public PresentationOptions getPresentation() {
        return presentation;
    }

    public enum Type {
        @SerializedName("shell")
        SHELL
    }

    public static class Group {
        public enum Type {
            @SerializedName("build")
            BUILD,

            @SerializedName("test")
            TEST
        }
        private Type kind;
        private Boolean isDefault;

        public Type getKind() {
            return kind;
        }

        public void setKind(Type kind) {
            this.kind = kind;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean isDefault) {
            this.isDefault = isDefault;
        }
    }
}
