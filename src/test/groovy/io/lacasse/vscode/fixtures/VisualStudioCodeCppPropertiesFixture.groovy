package io.lacasse.vscode.fixtures

import groovy.json.JsonSlurper
import io.lacasse.integtest.TestFile

class VisualStudioCodeCppPropertiesFixture {
    final TestFile location
    final def content

    VisualStudioCodeCppPropertiesFixture(TestFile file) {
        this.location = file
        this.content = new JsonSlurper().parse(file)
    }
}
