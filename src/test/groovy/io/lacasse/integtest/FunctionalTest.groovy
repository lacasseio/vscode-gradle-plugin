package io.lacasse.integtest

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

trait FunctionalTest {
    private BuildResult lastResult
    GradleRunner newRunner() {
        return GradleRunner.create().withProjectDir(getRootDir()).withPluginClasspath()
    }

    abstract File getRootDir()

    TestFile getBuildFile() {
        File result = new File(getRootDir(), "build.gradle")
        if (!result.exists()) {
            result = createBuildFile()
        }
        return new TestFile(result)
    }
    TestFile getSettingsFile() {
        File result = new File(getRootDir(), "settings.gradle")
        if (!result.exists()) {
            result = createSettingsFile()
        }
        return new TestFile(result)
    }

    abstract File createBuildFile()
    abstract File createSettingsFile()

    BuildResult succeed(String... tasks) {
        lastResult = newRunner().withArguments(tasks).build()
        println lastResult.output
        return lastResult
    }

    void assertTasksExecuted(String... tasks) {
        assertTasksExecuted(Arrays.asList(tasks))
    }

    void assertTasksExecuted(Iterable<String> tasks) {
        for (String task : tasks) {
            assertTaskExecuted(task)
        }
    }

    void assertTasksNotSkipped(String... tasks) {
        assertTasksNotSkipped(Arrays.asList(tasks))
    }

    void assertTasksNotSkipped(Iterable<String> tasks) {
        for (String task : tasks) {
            assertTaskNotSkipped(task)
        }
    }

    void assertTasksSkipped(String... tasks) {
        for (String task : tasks) {
            assert lastResult.task(task).outcome in [TaskOutcome.FROM_CACHE, TaskOutcome.NO_SOURCE, TaskOutcome.SKIPPED, TaskOutcome.UP_TO_DATE]
        }
    }

    void assertTaskNotSkipped(String task) {
        assert lastResult.task(task).outcome in [TaskOutcome.SUCCESS, TaskOutcome.FAILED]
    }

    TestFile file(String path) {
        new TestFile(new File(rootDir, path))
    }

    void assertTaskExecuted(String task) {
        assert lastResult.tasks.any { it.path == task }
    }
}
