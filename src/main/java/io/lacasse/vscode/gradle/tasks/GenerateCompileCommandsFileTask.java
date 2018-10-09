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

package io.lacasse.vscode.gradle.tasks;

import io.lacasse.vscode.gradle.internal.tasks.CompileCommandsFile;
import io.lacasse.vscode.gradle.internal.tasks.JsonGeneratorTask;
import org.gradle.api.Transformer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.apache.commons.io.IOUtils;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.language.cpp.CppBinary;
import org.gradle.language.cpp.tasks.CppCompile;
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal;
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal;
import org.gradle.nativeplatform.toolchain.internal.ToolType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class GenerateCompileCommandsFileTask extends JsonGeneratorTask<CompileCommandsFile> {
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

    public static TaskProvider<GenerateCompileCommandsFileTask> create(TaskContainer tasks, CppBinary binary) {
        return tasks.register(GenerateCompileCommandsFileTask.taskName(binary), GenerateCompileCommandsFileTask.class, it -> {
            ProviderFactory providerFactory = it.getProject().getProviders();
            ProjectLayout projectLayout = it.getProject().getLayout();

            it.setGroup("C++ Support");
            it.setDescription("Generate compile_commands.json for '" + binary + "'");
            it.dependsOn(binary.getCompileTask());
            it.getCompiler().set(providerFactory.provider(() -> {
                CppCompile compileTask = binary.getCompileTask().get();
                RegularFileProperty f = projectLayout.fileProperty();
                f.set(((NativeToolChainInternal)compileTask.getToolChain().get()).select((NativePlatformInternal) compileTask.getTargetPlatform().get()).locateTool(ToolType.CPP_COMPILER).getTool());
                return f.get();
            }));

            it.getOptionsFile().set(providerFactory.provider(() -> {
                CppCompile compileTask = binary.getCompileTask().get();
                RegularFileProperty f = projectLayout.fileProperty();
                f.set(new File(compileTask.getTemporaryDir(), "options.txt"));
                return f.get();
            }));
            it.getSources().from(binary.getCompileTask().map((Transformer<FileCollection, CppCompile>) cppCompile -> cppCompile.getSource()));
            it.setOutputFile(projectLayout.getBuildDirectory().file("cpp-support/" + binary.getName() + "/compile_commands.json").get().getAsFile());
        });
    }
}
