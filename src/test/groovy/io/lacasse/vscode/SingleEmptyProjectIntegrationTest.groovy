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

class SingleEmptyProjectIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    @UsesSample("empty-project")
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
        workspace.content.folders*.path == [rootProject.projectDir.absolutePath]
    }

    VisualStudioCodeProjectFixture vscodeProject(TestFile projectDir = file(".vscode")) {
        new VisualStudioCodeProjectFixture(projectDir)
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
