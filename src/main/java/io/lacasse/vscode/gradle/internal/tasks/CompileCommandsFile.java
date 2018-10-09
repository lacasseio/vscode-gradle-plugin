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

import io.lacasse.vscode.gradle.internal.generator.JsonPersistableConfigurationObject;
import io.lacasse.vscode.internal.schemas.CompileCommand;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompileCommandsFile extends JsonPersistableConfigurationObject<List<CompileCommand>> {
    private final List<CompileCommand> compileCommands = new ArrayList<>();

    public CompileCommandsFile() {
        super(new TypeToken<List<CompileCommand>>() {}.getType());
    }

    @Override
    protected List<CompileCommand> newRootObject() {
        return new ArrayList<>();
    }

    @Override
    protected void store(List<CompileCommand> rootObject) {
        rootObject.addAll(compileCommands);
    }

    @Override
    protected void load(List<CompileCommand> rootObject) {

    }

    @Override
    protected String getDefaultResourceName() {
        return "compile_commands.json";
    }

    public void source(File directory, String command, File sourceFile) {
        CompileCommand compileCommand = new CompileCommand();
        compileCommand.setCommand(command);
        compileCommand.setDirectory(directory.getAbsolutePath());
        compileCommand.setFile(sourceFile.getAbsolutePath());
        compileCommands.add(compileCommand);
    }
}
