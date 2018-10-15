package io.lacasse.integtest;

import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class DefaultGradleMultiProject extends ExternalResource implements GradleMultiProject {
    public TemporaryFolder testProjectDir = new TemporaryFolder();
    private File buildFile;
    private File settingsFile;

    public File getBuildFile() {
        try {
            if (buildFile == null) {
                buildFile = new File(testProjectDir.getRoot(), "build.gradle");
                buildFile.createNewFile();
            }
            return buildFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public File getProjectDir() {
        try {
            return testProjectDir.getRoot().getCanonicalFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File getSettingsFile() {
        try {
            if (settingsFile == null) {
                settingsFile = new File(testProjectDir.getRoot(), "settings.gradle");
                settingsFile.createNewFile();
            }
            return settingsFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        testProjectDir.create();
    }
}
