package io.lacasse.vscode

import io.lacasse.integtest.DefaultGradleMultiProject
import io.lacasse.integtest.FunctionalTest
import io.lacasse.integtest.GradleMultiProject
import io.lacasse.integtest.TestFile
import io.lacasse.vscode.fixtures.VisualStudioCodeTaskNames
import io.lacasse.vscode.fixtures.VisualStudioCodeWorkspaceFixture
import org.gradle.samples.test.rule.Sample
import org.gradle.samples.test.rule.UsesSample
import org.junit.Rule
import spock.lang.Specification

class MultiProjectIntegrationTest extends Specification implements FunctionalTest, VisualStudioCodeTaskNames {
    @Rule Sample sample = Sample.from("src/test/samples").intoTemporaryFolder()
    @Rule GradleMultiProject rootProject = new DefaultGradleMultiProject(sample)

    @UsesSample("multi-empty-project")
    def "can generate vscode project and workspace for multiple projects"() {
        when:
        succeeds "vscode"
        
        then:
        assertTasksExecuted([vscodeTasks(), vscodeTasks(":project1"), vscodeTasks(":project2"), vscodeTasks(":project3")].flatten())
        assertTasksNotSkipped([vscodeTasks(), vscodeTasks(":project1"), vscodeTasks(":project2"), vscodeTasks(":project3")].flatten())

        def workspace = vscodeWorkspace()
        workspace.location.assertIsFile()
        workspace.content.folders*.path as Set == [rootProject.projectDir, file("project1"), file("project2"), file("project3")]*.absolutePath as Set
    }

    VisualStudioCodeWorkspaceFixture vscodeWorkspace(TestFile workspaceFile = file("root.code-workspace")) {
        new VisualStudioCodeWorkspaceFixture(workspaceFile)
    }
}
