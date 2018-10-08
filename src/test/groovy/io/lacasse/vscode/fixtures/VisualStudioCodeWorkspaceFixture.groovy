package io.lacasse.vscode.fixtures

import groovy.json.JsonSlurper
import io.lacasse.integtest.TestFile

class VisualStudioCodeWorkspaceFixture {
    final TestFile location
    final def content

    VisualStudioCodeWorkspaceFixture(TestFile location) {
        this.location = location
        this.content = new JsonSlurper().parse(location)
    }
}
