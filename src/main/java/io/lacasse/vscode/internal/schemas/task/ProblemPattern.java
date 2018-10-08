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

package io.lacasse.vscode.internal.schemas.task;

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName;

public class ProblemPattern {
    private String regexp;
    private Integer file;
    private Integer message;
    private Boolean loop;
    private final Kind kind = Kind.FILE;

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Integer getFile() {
        return file;
    }

    public void setFile(Integer file) {
        this.file = file;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    public Kind getKind() {
        return kind;
    }

    public enum Kind {
        @SerializedName("file")
        FILE
    }
}
