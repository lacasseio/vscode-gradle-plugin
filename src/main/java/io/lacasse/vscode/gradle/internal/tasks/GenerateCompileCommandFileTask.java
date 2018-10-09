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

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.apache.commons.io.IOUtils;
import org.gradle.language.cpp.CppBinary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class GenerateCompileCommandFileTask extends JsonGeneratorTask<CompileCommandsFile> {
    private final RegularFileProperty compileCommandsLocation = newOutputFile();
    private final ConfigurableFileCollection sources = getProject().files();
    private final RegularFileProperty optionsFile = newInputFile();
    private final RegularFileProperty compiler = newInputFile();

    @Override
    protected void configure(CompileCommandsFile object) {
        String compilerFlags = getCompilerFlags();
        String compilerPath = getCompiler().get().getAsFile().getAbsolutePath();
        String command = compilerPath + " " + compilerFlags;

        for (File sourceFile : sources) {
            object.source(getProject().getProjectDir(), command, sourceFile);
        }
    }

    private String getCompilerFlags() {
        try (InputStream inStream = new FileInputStream(optionsFile.getAsFile().get())) {
            return IOUtils.readLines(inStream, Charset.defaultCharset()).stream().collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected CompileCommandsFile create() {
        return new CompileCommandsFile();
    }

    @Internal
    public RegularFileProperty getCompileCommandsFileLocation() {
        return compileCommandsLocation;
    }

    @Override
    public File getOutputFile() {
        return compileCommandsLocation.getAsFile().get();
    }

    @Override
    public void setOutputFile(File outputFile) {
        compileCommandsLocation.set(outputFile);
    }

    @Override
    public File getInputFile() {
        return null;
    }

    @InputFiles
    public ConfigurableFileCollection getSources() {
        return sources;
    }

    @InputFile
    public RegularFileProperty getOptionsFile() {
        return optionsFile;
    }

    @InputFile
    public RegularFileProperty getCompiler() {
        return compiler;
    }

    public static String taskName(CppBinary binary) {
        return "generateCompileCommandsFor" + binary.getName();
    }
}
