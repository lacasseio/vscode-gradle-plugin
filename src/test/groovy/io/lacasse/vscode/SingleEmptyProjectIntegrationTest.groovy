package io.lacasse.vscode

import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.TestFile
import io.lacasse.vscode.fixtures.VisualStudioCodeProjectFixture
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import io.lacasse.vscode.fixtures.VisualStudioCodeWorkspaceFixture
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class SingleEmptyProjectIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()

    @Override
    File getRootDir() {
        return testProjectDir.root.getCanonicalFile()
    }

    @Override
    File createBuildFile() {
        return testProjectDir.newFile("build.gradle")
    }

    @Override
    File createSettingsFile() {
        return testProjectDir.newFile("settings.gradle")
    }

    def setup() {
        buildFile << """
            plugins {
                id "io.lacasse.vscode"
            }
        """
        settingsFile << """
            rootProject.name = "root"
        """
    }

    def "can generate vscode project and workspace for empty project"() {
        when:
        succeed"vscode"

        then:
        assertTasksExecuted vscodeTasks()
        assertTasksNotSkipped vscodeTasks()

        def project = vscodeProject()

        project.launchFile.location.assertIsFile()
        project.launchFile.content.version == "0.2.0"
        project.launchFile.content.configurations.empty

        project.tasksFile.location.assertIsFile()
        project.tasksFile.content.version == "2.0.0"
        project.tasksFile.content.tasks.empty
        
        project.cppPropertiesFile.location.assertIsFile()
        project.cppPropertiesFile.content.version == 4
        project.cppPropertiesFile.content.configurations.empty

        def workspace = vscodeWorkspace()
        workspace.location.assertIsFile()
        workspace.content.folders*.path == [rootDir.absolutePath]
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
