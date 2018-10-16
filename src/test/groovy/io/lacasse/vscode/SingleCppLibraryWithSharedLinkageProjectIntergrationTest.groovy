package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleMultiProject
import io.lacasse.integtest.TestFile
import io.lacasse.vscode.fixtures.VisualStudioCodeProjectFixture
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import io.lacasse.vscode.fixtures.VisualStudioCodeWorkspaceFixture
import org.junit.Rule
import spock.lang.Specification

class SingleCppLibraryWithSharedLinkageProjectIntergrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject()

    def setup() {
        buildFile << """
            plugins {
                id "io.lacasse.vscode"
                id "cpp-library"
            }
            
            library.linkage = [Linkage.SHARED]
        """

        rootProject.file("src/main/public/common.h") << """
            #define EXIT_CODE 0
            
            int get_foo();
        """
        rootProject.file("src/main/cpp/main.cpp") << """
            #include "common.h"

            int get_foo() {
                return EXIT_CODE;
            }
        """
    }

    def "can create vscode IDE files for C++ shared library"() {
        expect:
        succeed "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 2
        project.cppPropertiesFile.content.configurations[0].name == "mainDebug"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/public").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/mainDebug/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "mainRelease"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/mainRelease/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 2
        project.tasksFile.content.tasks[0].label == "Build mainDebug"
        project.tasksFile.content.tasks[0].group.kind == "build"
        project.tasksFile.content.tasks[0].group.isDefault == true

        project.tasksFile.content.tasks[1].label == "Build mainRelease"
        project.tasksFile.content.tasks[1].group == "build"
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
