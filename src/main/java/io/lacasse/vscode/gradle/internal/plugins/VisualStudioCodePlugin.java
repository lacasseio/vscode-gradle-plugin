package io.lacasse.vscode.gradle.internal.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.util.GradleVersion;

public class VisualStudioCodePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        assertCompatibleGradleVersion();

        project.getPluginManager().apply(VisualStudioCodePluginImpl.class);
    }

    private void assertCompatibleGradleVersion() {
        if (GradleVersion.current().compareTo(GradleVersion.version("5.0")) < 0) {
            throw new IllegalStateException("'io.lacasse.vscode' plugin is compatible with Gradle 5.0 or later");
        }
    }
}
