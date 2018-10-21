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

import io.lacasse.vscode.internal.schemas.CompileCommand;
import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.apache.commons.io.IOUtils;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.NativeBinarySpec;
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.ToolType;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.text.WordUtils.capitalize;

public class GenerateCompileCommandsFileTask extends JsonGeneratorTask<CompileCommandsFile> {
    private final RegularFileProperty compileCommandsLocation = getProject().getObjects().fileProperty();
    private final RegularFileProperty compiler = getProject().getObjects().fileProperty();
    private final ListProperty<CompileCommandsConfiguration> compileCommands = getProject().getObjects().listProperty(CompileCommandsConfiguration.class);

    public GenerateCompileCommandsFileTask() {
        compileCommands.set(Collections.emptyList());
    }

    @Override
    protected void configure(CompileCommandsFile object) {
        for (CompileCommandsConfiguration configuration : compileCommands.get()) {
            String command = null;
            if (configuration.getOptionsFile().isPresent() && configuration.getOptionsFile().getAsFile().get().exists()) {
                String compilerFlags = getCompilerFlags(configuration.getOptionsFile().get().getAsFile());
                String compilerPath = getCompiler().get().getAsFile().getAbsolutePath();
                command = compilerPath + " " + compilerFlags;
            }

            for (File sourceFile : configuration.getSources()) {
                object.source(getProject().getProjectDir(), command, sourceFile);
            }
        }
    }

    private String getCompilerFlags(File optionsFile) {
        try (InputStream inStream = new FileInputStream(optionsFile)) {
            return IOUtils.readLines(inStream, Charset.defaultCharset()).stream().collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected CompileCommandsFile create() {
        return new CompileCommandsFile();
    }

    @OutputFile
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

    public void compileCommands(Action<CompileCommandsConfiguration> action) {
        CompileCommandsConfiguration configuration = getProject().getObjects().newInstance(CompileCommandsConfiguration.class);
        action.execute(configuration);
        compileCommands.add(configuration);
    }

    @Nested
    public ListProperty<CompileCommandsConfiguration> getCompileCommands() {
        return compileCommands;
    }

    @InputFile
    public RegularFileProperty getCompiler() {
        return compiler;
    }

    public static String taskName(CppBinary binary) {
        return taskName(binary.getName());
    }

    public static String taskName(NativeBinarySpec binary) {
        return taskName(binary.getName());
    }

    private static String taskName(String name) {
        return "generate" + capitalize(name) + "CompileCommands";
    }

    public static class CompileCommandsConfiguration {
        private final ConfigurableFileCollection sources;
        private final RegularFileProperty optionsFile;

        @Inject
        public CompileCommandsConfiguration(ObjectFactory objectFactory, ProjectLayout projectLayout) {
            this.sources = projectLayout.configurableFiles();
            this.optionsFile = objectFactory.fileProperty();
        }

        @InputFiles
        public ConfigurableFileCollection getSources() {
            return sources;
        }

        @InputFiles
        @Optional
        public RegularFileProperty getOptionsFile() {
            return optionsFile;
        }
    }
}
