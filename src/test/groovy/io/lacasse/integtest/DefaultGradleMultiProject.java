package io.lacasse.integtest;

import org.gradle.samples.test.rule.Sample;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class DefaultGradleMultiProject extends ExternalResource implements GradleMultiProject {
    public final TemporaryFolder testProjectDir;
    private final Sample sample;
    private File buildFile;
    private File settingsFile;

    public DefaultGradleMultiProject() {
        this(new TemporaryFolder());
    }

    public DefaultGradleMultiProject(TemporaryFolder testProjectDir) {
        this.testProjectDir = testProjectDir;
        this.sample = null;
    }

    public DefaultGradleMultiProject(Sample sample) {
        this.testProjectDir = null;
        this.sample = sample;
    }

    public File getBuildFile() {
        try {
            if (buildFile == null) {
                buildFile = new File(getProjectDir(), "build.gradle");
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
            if (sample == null) {
                return testProjectDir.getRoot().getCanonicalFile();
            }
            return sample.getDir().getCanonicalFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public File getSettingsFile() {
        try {
            if (settingsFile == null) {
                settingsFile = new File(getProjectDir(), "settings.gradle");
                settingsFile.createNewFile();
            }
            return settingsFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public TestFile file(String path) {
        TestFile result = new TestFile(new File(getProjectDir(), path));
        result.getParentFile().mkdirs();
        return result;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        if (testProjectDir != null) {
            testProjectDir.create();
        }
    }
}
