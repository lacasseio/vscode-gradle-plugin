package io.lacasse.vscode.fixtures

import groovy.json.JsonSlurper
import io.lacasse.integtest.TestFile

class VisualStudioCodeTasksFixture {
    final TestFile location
    final def content

    VisualStudioCodeTasksFixture(TestFile location) {
        this.location = location
        this.content = new JsonSlurper().parse(location)
    }
}
