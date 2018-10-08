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

import io.lacasse.vscode.gradle.VisualStudioCodeLaunch;
import io.lacasse.vscode.gradle.internal.DefaultVisualStudioCodeLaunch;
import io.lacasse.vscode.schemas.launch.LaunchDescription;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.SkipWhenEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GenerateLaunchFileTask extends JsonGeneratorTask<VisualStudioCodeLaunchFile> {
    private final RegularFileProperty launchFileLocation = newOutputFile();
    private final ListProperty<VisualStudioCodeLaunch> lauches = getProject().getObjects().listProperty(VisualStudioCodeLaunch.class);

    @Override
    protected void configure(VisualStudioCodeLaunchFile object) {
        List<LaunchDescription> l = new ArrayList<>();

        for (VisualStudioCodeLaunch tool : lauches.get()) {
            LaunchDescription launch = new LaunchDescription();
            launch.setName(tool.getName());
            launch.setProgram(tool.getProgramLocation().get().getAsFile().getAbsolutePath());
            launch.setPreLaunchTask(tool.getPreLaunchGradleTask().isPresent() ? tool.getPreLaunchGradleTask().get().getName() : null);
            launch.setMIMode("gdb");
            launch.setMiDebuggerServerAddress("localhost:4242");
            launch.setMiDebuggerPath(tool.getDebuggerLocation().get().getAsFile().getAbsolutePath());
            launch.setMiDebuggerArgs("\"--init-eval-command=file " + tool.getProgramLocation().get().getAsFile().getAbsolutePath() + "\" \"--init-eval-command=load\" \"--init-eval-command=break main\"");

            l.add(launch);
        }

        object.setLaunches(l);
    }

    @Override
    protected VisualStudioCodeLaunchFile create() {
        return new VisualStudioCodeLaunchFile();
    }

    public ListProperty<VisualStudioCodeLaunch> getLaunches() {
        return lauches;
    }

    @Internal
    public RegularFileProperty getLaunchFileLocation() {
        return launchFileLocation;
    }

    @Override
    public File getOutputFile() {
        return launchFileLocation.getAsFile().get();
    }

    @Override
    public void setOutputFile(File outputFile) {
        launchFileLocation.set(outputFile);
    }

    @Override
    public File getInputFile() {
        return null;
    }
}
