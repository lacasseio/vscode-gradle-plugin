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

package io.lacasse.vscode.internal.schemas.launch;

import com.google.gson.annotations.SerializedName;

public class LaunchDescription {
    private String name;
    private final String type = "cppdbg";
    private final Request request = Request.LAUNCH;
    private String preLaunchTask;
    private String program;
    private final String cwd = "${workspaceFolder}";
    private final boolean externalConsole = false;
    private String MIMode;
    private String miDebuggerServerAddress;
    private String miDebuggerPath;
    private String miDebuggerArgs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public Request getRequest() {
        return request;
    }

    public String getPreLaunchTask() {
        return preLaunchTask;
    }

    public void setPreLaunchTask(String preLaunchTask) {
        this.preLaunchTask = preLaunchTask;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getCwd() {
        return cwd;
    }

    public boolean isExternalConsole() {
        return externalConsole;
    }

    public String getMIMode() {
        return MIMode;
    }

    public void setMIMode(String MIMode) {
        this.MIMode = MIMode;
    }

    public String getMiDebuggerServerAddress() {
        return miDebuggerServerAddress;
    }

    public void setMiDebuggerServerAddress(String miDebuggerServerAddress) {
        this.miDebuggerServerAddress = miDebuggerServerAddress;
    }

    public String getMiDebuggerPath() {
        return miDebuggerPath;
    }

    public void setMiDebuggerPath(String miDebuggerPath) {
        this.miDebuggerPath = miDebuggerPath;
    }

    public String getMiDebuggerArgs() {
        return miDebuggerArgs;
    }

    public void setMiDebuggerArgs(String miDebuggerArgs) {
        this.miDebuggerArgs = miDebuggerArgs;
    }

    public enum Request {
        @SerializedName("launch")
        LAUNCH
    }
}
