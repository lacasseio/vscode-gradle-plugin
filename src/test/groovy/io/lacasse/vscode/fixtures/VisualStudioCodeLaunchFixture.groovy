package io.lacasse.vscode.fixtures

import groovy.json.JsonSlurper
import io.lacasse.integtest.TestFile

class VisualStudioCodeLaunchFixture {
    final TestFile location
    final def content

    VisualStudioCodeLaunchFixture(TestFile file) {
        this.location = file
        this.content = new JsonSlurper().parse(file)
    }
}
