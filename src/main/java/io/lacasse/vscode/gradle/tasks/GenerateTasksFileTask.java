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

import io.lacasse.vscode.gradle.VisualStudioCodeGradleTask;
import io.lacasse.vscode.gradle.internal.DefaultVisualStudioCodeGradleTask;
import io.lacasse.vscode.internal.schemas.task.ProblemMatcher;
import io.lacasse.vscode.internal.schemas.task.ProblemPattern;
import io.lacasse.vscode.internal.schemas.task.TaskDescription;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Nested;
import org.gradle.plugins.ide.internal.IdePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GenerateTasksFileTask extends JsonGeneratorTask<VisualStudioCodeTasksFile> {
    private final RegularFileProperty taskFileLocation = newOutputFile();
    private final ListProperty<VisualStudioCodeGradleTask> tasks = getProject().getObjects().listProperty(VisualStudioCodeGradleTask.class);

    @Override
    protected void configure(VisualStudioCodeTasksFile object) {
        List<TaskDescription> result = new ArrayList<>();

        for (VisualStudioCodeGradleTask t : tasks.get()) {
            DefaultVisualStudioCodeGradleTask tool = (DefaultVisualStudioCodeGradleTask) t;
            TaskDescription task = new TaskDescription();
            task.setCommand(IdePlugin.toGradleCommand(getProject()) + " --project-dir " + getProject().getRootDir().getAbsolutePath() + " " + tool.getTask().get().getPath());
            task.setLabel(tool.getName());
            if (tool.getProblemMatcher() != null) {
                task.setProblemMatcher(tool.getProblemMatcher());
            } else {
                ProblemMatcher matcher = new ProblemMatcher();
                ProblemPattern pattern1 = new ProblemPattern();
                pattern1.setRegexp("^\\* What went wrong:$");
                matcher.getPattern().add(pattern1);

                ProblemPattern pattern2 = new ProblemPattern();
                pattern2.setRegexp("^Execution failed for task '(.+)'.$");
                pattern2.setFile(1);
                matcher.getPattern().add(pattern2);

                ProblemPattern pattern3 = new ProblemPattern();
                pattern3.setRegexp("^\\s*>\\s+(.+)$");
                pattern3.setMessage(1);
                pattern3.setLoop(true);
                matcher.getPattern().add(pattern3);

                task.setProblemMatcher(matcher);
            }
            task.setBackground(tool.isBackground());

            TaskDescription.Group.Type groupType = toGroupType(tool);
            if (groupType != null) {
                if (tool.isDefault()) {
                    TaskDescription.Group g = new TaskDescription.Group();
                    g.setKind(groupType);
                    g.setDefault(tool.isDefault());
                    task.setGroup(g);
                } else {
                    task.setGroup(groupType);
                }
            }


            result.add(task);
        }

        object.setTaskDescriptions(result);
    }

    private TaskDescription.Group.Type toGroupType(DefaultVisualStudioCodeGradleTask tool) {
        if (tool.isBuild()) {
            return TaskDescription.Group.Type.BUILD;
        } else if (tool.isTest()) {
            return TaskDescription.Group.Type.TEST;
        }
        return null;
    }

    @Override
    protected VisualStudioCodeTasksFile create() {
        return new VisualStudioCodeTasksFile();
    }

    @Nested
    public ListProperty<VisualStudioCodeGradleTask> getTasks() {
        return tasks;
    }

    @Internal
    public RegularFileProperty getTaskFileLocation() {
        return taskFileLocation;
    }

    @Override
    public File getOutputFile() {
        return taskFileLocation.getAsFile().get();
    }

    @Override
    public void setOutputFile(File outputFile) {
        taskFileLocation.set(outputFile);
    }

    @Override
    public File getInputFile() {
        return null;
    }
}
