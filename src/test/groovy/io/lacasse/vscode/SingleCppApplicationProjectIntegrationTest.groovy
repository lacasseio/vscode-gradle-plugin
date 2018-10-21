package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleMultiProject
import io.lacasse.integtest.TestFile
import io.lacasse.vscode.fixtures.VisualStudioCodeProjectFixture
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import io.lacasse.vscode.fixtures.VisualStudioCodeWorkspaceFixture
import org.gradle.samples.test.rule.Sample
import org.gradle.samples.test.rule.UsesSample
import org.junit.Rule
import spock.lang.Specification

class SingleCppApplicationProjectIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    @UsesSample("cpp-application")
    def "can create vscode IDE files for C++ application"() {
        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 2
        project.cppPropertiesFile.content.configurations[0].name == "mainDebug"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
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

    @UsesSample("cpp-application-software-model")
    def "can create vscode IDE files for C++ application from the software model"() {
        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 1
        project.cppPropertiesFile.content.configurations[0].name == "executable"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/executable/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 1
        project.tasksFile.content.tasks[0].label == "Build executable"
        project.tasksFile.content.tasks[0].group.kind == "build"
        project.tasksFile.content.tasks[0].group.isDefault == true
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
