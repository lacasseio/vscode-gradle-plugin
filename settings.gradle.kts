rootProject.name = "vscode-gradle-plugin"

sourceControl {
    gitRepository(uri("https://github.com/gradle/exemplar")) {
        producesModule("org.gradle:sample-check")
    }
}