package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleMultiProject
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import org.gradle.samples.test.rule.Sample
import org.gradle.samples.test.rule.UsesSample
import org.junit.Rule
import spock.lang.Specification

class CustomConfigurationUsingCppBinaryTaskIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    // TODO: Add C++ source
    @UsesSample("custom-configuration")
    def "can configure vscode using CppBinary task"() {
        expect:
        succeed "vscode"

        assertTasksExecuted([vscodeTasks(), ":cpp:generateMainDebugCompileCommands", ":cpp:generateMainReleaseCompileCommands"].flatten())
        assertTasksNotSkipped([vscodeTasks(), ":cpp:generateMainDebugCompileCommands", ":cpp:generateMainReleaseCompileCommands"].flatten())
    }

    // can generate configuration when no source files (options.txt is not generated)
}
