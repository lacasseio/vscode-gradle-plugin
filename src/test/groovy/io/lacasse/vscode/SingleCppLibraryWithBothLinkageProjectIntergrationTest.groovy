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

class SingleCppLibraryWithBothLinkageProjectIntergrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    @UsesSample("cpp-library-with-both-linkage")
    def "can create vscode IDE files for C++ library with both linkage"() {
        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 4
        project.cppPropertiesFile.content.configurations[0].name == "mainDebugStatic"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/public").absolutePath)
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/mainDebugStatic/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "mainDebugShared"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/public").absolutePath)
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/mainDebugShared/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[2].name == "mainReleaseStatic"
        project.cppPropertiesFile.content.configurations[2].includePath.contains(rootProject.file("src/main/public").absolutePath)
        project.cppPropertiesFile.content.configurations[2].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[2].defines.empty
        project.cppPropertiesFile.content.configurations[2].compileCommands == rootProject.file("build/cpp-support/mainReleaseStatic/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[3].name == "mainReleaseShared"
        project.cppPropertiesFile.content.configurations[3].includePath.contains(rootProject.file("src/main/public").absolutePath)
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

    @UsesSample("cpp-library-software-model")
    def "can create vscode IDE files for C++ library from the software model"() {
        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 2
        project.cppPropertiesFile.content.configurations[0].name == "sharedLibrary"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/sharedLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "staticLibrary"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/staticLibrary/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 2
        project.tasksFile.content.tasks[0].label == "Build sharedLibrary"
        project.tasksFile.content.tasks[0].group.kind == "build"
        project.tasksFile.content.tasks[0].group.isDefault == true

        project.tasksFile.content.tasks[1].label == "Build staticLibrary"
        project.tasksFile.content.tasks[1].group == "build"
    }

    @UsesSample("cpp-library-software-model")
    def "can create vscode IDE files for C++ library from the software model with well known build type"() {
        buildFile << """
            model {
                buildTypes {
                    debug
                    debugOptimized
                    release
                }
            }
        """

        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 6
        project.cppPropertiesFile.content.configurations[0].name == "debugOptimizedSharedLibrary"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/debugOptimizedSharedLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "debugOptimizedStaticLibrary"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/debugOptimizedStaticLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[2].name == "debugSharedLibrary"
        project.cppPropertiesFile.content.configurations[2].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[2].defines.empty
        project.cppPropertiesFile.content.configurations[2].compileCommands == rootProject.file("build/cpp-support/debugSharedLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[3].name == "debugStaticLibrary"
        project.cppPropertiesFile.content.configurations[3].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[3].defines.empty
        project.cppPropertiesFile.content.configurations[3].compileCommands == rootProject.file("build/cpp-support/debugStaticLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[4].name == "releaseSharedLibrary"
        project.cppPropertiesFile.content.configurations[4].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[4].defines.empty
        project.cppPropertiesFile.content.configurations[4].compileCommands == rootProject.file("build/cpp-support/releaseSharedLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[5].name == "releaseStaticLibrary"
        project.cppPropertiesFile.content.configurations[5].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[5].defines.empty
        project.cppPropertiesFile.content.configurations[5].compileCommands == rootProject.file("build/cpp-support/releaseStaticLibrary/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 6
        project.tasksFile.content.tasks[0].label == "Build debugOptimizedSharedLibrary"
        project.tasksFile.content.tasks[0].group == "build"

        project.tasksFile.content.tasks[1].label == "Build debugOptimizedStaticLibrary"
        project.tasksFile.content.tasks[1].group == "build"

        project.tasksFile.content.tasks[2].label == "Build debugSharedLibrary"
        project.tasksFile.content.tasks[2].group.kind == "build"
        project.tasksFile.content.tasks[2].group.isDefault == true

        project.tasksFile.content.tasks[3].label == "Build debugStaticLibrary"
        project.tasksFile.content.tasks[3].group == "build"

        project.tasksFile.content.tasks[4].label == "Build releaseSharedLibrary"
        project.tasksFile.content.tasks[4].group == "build"

        project.tasksFile.content.tasks[5].label == "Build releaseStaticLibrary"
        project.tasksFile.content.tasks[5].group == "build"
    }

    @UsesSample("cpp-library-software-model")
    def "can create vscode IDE files for C++ library from the software model with non-debug build type"() {
        buildFile << """
            model {
                buildTypes {
                    release
                }
            }
        """

        expect:
        succeeds "vscode"

        assertTasksExecuted(vscodeTasks())
        assertTasksNotSkipped(vscodeTasks())

        def project = vscodeProject()
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.configurations.size() == 2
        project.cppPropertiesFile.content.configurations[0].name == "sharedLibrary"
        project.cppPropertiesFile.content.configurations[0].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[0].defines.empty
        project.cppPropertiesFile.content.configurations[0].compileCommands == rootProject.file("build/cpp-support/sharedLibrary/compile_commands.json").absolutePath

        project.cppPropertiesFile.content.configurations[1].name == "staticLibrary"
        project.cppPropertiesFile.content.configurations[1].includePath.contains(rootProject.file("src/main/headers").absolutePath)
        project.cppPropertiesFile.content.configurations[1].defines.empty
        project.cppPropertiesFile.content.configurations[1].compileCommands == rootProject.file("build/cpp-support/staticLibrary/compile_commands.json").absolutePath

        // TODO: support debugging
        project.launchFile.location.assertIsFile()
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.tasks.size() == 2
        project.tasksFile.content.tasks[0].label == "Build sharedLibrary"
        project.tasksFile.content.tasks[0].group.kind == "build"
        project.tasksFile.content.tasks[0].group.isDefault == true

        project.tasksFile.content.tasks[1].label == "Build staticLibrary"
        project.tasksFile.content.tasks[1].group == "build"
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
