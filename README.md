# Gradle plugin for Visual Studio Code IDE

## Using the plugin

### Source dependencies

settings.gradle
```
sourceControl {
    gitRepository("https://github.com/gradle/native-samples-cpp-library.git") {
        producesModule("org.gradle.cpp-samples:utilities")
    }
}
```

build.gradle
```
buildscript {
    dependencies {
        classpath "io.lacasse.vscode:vscode-gradle-plugin:latest.integration"
    }
}
```

## Contribution

Please open issues or provide PRs for any features or bugs this plugin may have.
Keep in mind that all features or bugs needs to have the proper test coverage.