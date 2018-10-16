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
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class SingleCppLibraryWithBothLinkageProjectIntergrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    @UsesSample("cpp-library-with-both-linkage")
    def "can create vscode IDE files for C++ library with both linkage"() {
        expect:
        succeed "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 4
        project.cppPropertiesFile.content.configurations[0].name == "mainDebugStatic"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/public").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/mainDebugStatic/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "mainDebugShared"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/mainDebugShared/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[2].name == "mainReleaseStatic"
        project.cppPropertiesFile.content.configurations[2].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[2].defines.empty
        project.cppPropertiesFile.content.configurations[2].compileCommands == rootProject.file("build/cpp-support/mainReleaseStatic/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[3].name == "mainReleaseShared"
        project.cppPropertiesFile.content.configurations[3].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[3].defines.empty
        project.cppPropertiesFile.content.configurations[3].compileCommands == rootProject.file("build/cpp-support/mainReleaseShared/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 4
        project.tasksFile.content.tasks[0].label == "Build mainDebugStatic"
        project.tasksFile.content.tasks[0].group == "build"

        project.tasksFile.content.tasks[1].label == "Build mainDebugShared"
        project.tasksFile.content.tasks[1].group.kind == "build"
        project.tasksFile.content.tasks[1].group.isDefault == true

        project.tasksFile.content.tasks[2].label == "Build mainReleaseStatic"
        project.tasksFile.content.tasks[2].group == "build"

        project.tasksFile.content.tasks[3].label == "Build mainReleaseShared"
        project.tasksFile.content.tasks[3].group == "build"
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
