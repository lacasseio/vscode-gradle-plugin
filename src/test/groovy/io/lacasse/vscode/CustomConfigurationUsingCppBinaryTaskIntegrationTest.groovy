package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleMultiProject
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import org.junit.Rule
import spock.lang.Specification

class CustomConfigurationUsingCppBinaryTaskIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject()

    def setup() {
        settingsFile << """
            include "cpp" 
        """

        buildFile << """
            plugins {
                id "io.lacasse.vscode"
            }

            project(":cpp") {
                apply plugin: "cpp-application"
            }
        """
    }

    // TODO: Add C++ source
    def "can configure vscode using CppBinary task"() {
        buildFile << '''
            project(":cpp").application.binaries.whenElementKnown { binary ->
                visualStudioCode.project {
                    cppConfiguration(binary.name) { configureFromBinary(binary) }
                }
            }
        '''

        expect:
        succeed "vscode"

        assertTasksExecuted([vscodeTasks(), ":cpp:generateCompileCommandsFormainDebug", ":cpp:generateCompileCommandsFormainRelease"].flatten())
        assertTasksNotSkipped([vscodeTasks(), ":cpp:generateCompileCommandsFormainDebug", ":cpp:generateCompileCommandsFormainRelease"].flatten())
    }

    // can generate configuration when no source files (options.txt is not generated)
}
