package io.lacasse.vscode.fixtures

import io.lacasse.integtest.TestFile

class VisualStudioCodeProjectFixture {
    final VisualStudioCodeLaunchFixture launchFile
    final VisualStudioCodeTasksFixture tasksFile
    final VisualStudioCodeCppPropertiesFixture cppPropertiesFile

    VisualStudioCodeProjectFixture(TestFile projectDir) {
        this.launchFile = new VisualStudioCodeLaunchFixture(projectDir.file("launch.json"))
        this.tasksFile = new VisualStudioCodeTasksFixture(projectDir.file("tasks.json"))
        this.cppPropertiesFile = new VisualStudioCodeCppPropertiesFixture(projectDir.file("c_cpp_properties.json"))
    }
}
