package io.lacasse.vscode.fixtures

trait VisualStudioCodeTaskNames {
    def vscodeTasks(def path = "") {
        def result = ["$path:generateCppProperties", "$path:vscodeLaunch", "$path:vscodeTasks", "$path:vscodeProject", "$path:vscode"]
        if (path.empty) {
            result.add(":vscodeWorkspace")
        }
        return result
    }
}
