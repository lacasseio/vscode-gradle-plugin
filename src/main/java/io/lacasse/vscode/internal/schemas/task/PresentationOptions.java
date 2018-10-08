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

public class PresentationOptions {
    private final Reveal reveal = Reveal.SILENT;
    private final boolean echo = true;
    private final boolean focus = false;
    private final Panel panel = Panel.SHARED;

    public Reveal getReveal() {
        return reveal;
    }

    public boolean isEcho() {
        return echo;
    }

    public boolean isFocus() {
        return focus;
    }

    public Panel getPanel() {
        return panel;
    }

    public enum Reveal {
        @SerializedName("silent")
        SILENT
    }

    public enum Panel {
        @SerializedName("shared")
        SHARED
    }
}
