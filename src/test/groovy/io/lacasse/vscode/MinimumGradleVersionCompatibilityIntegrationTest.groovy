package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleProject
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class MinimumGradleVersionCompatibilityIntegrationTest extends Specification implements FunctionalTest {
    @Rule GradleProject rootProject = new DefaultGradleMultiProject()

    @Unroll
    def "cannot apply plugin on Gradle version lower than 5.0"() {
        rootProject.buildFile << """
            plugins {
                id "io.lacasse.vscode"
            }
        """

        expect:
        runner.withGradleVersion(gradleVersion)
        def result = fails "help"

        result.output.contains("'io.lacasse.vscode' plugin is compatible with Gradle 5.0 or later")

        where:
        gradleVersion << ["4.10.2", "4.9", "4.8", "4.7", "4.0", "3.0"]
    }

    def "can apply plugin on Gradle version 5.0 and later"() {
        rootProject.buildFile << """
            plugins {
                id "io.lacasse.vscode"
            }
        """

        expect:
        succeeds "help"
    }
}
