package io.lacasse.integtest;

import org.junit.rules.TestRule;

import java.io.File;

public interface GradleProject extends TestRule {
    File getBuildFile();
    File getProjectDir();

    TestFile file(String path);
}
