[![Build Status](https://travis-ci.org/lacasseio/vscode-gradle-plugin.svg?branch=master)](https://travis-ci.org/lacasseio/vscode-gradle-plugin)

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

## Public API

Anything that is located inside an internal package is considered implementation specific.
You shouldn't rely on this code as it may be removed or change without notice.
The behavior will stay the same without prior deprecation or major version change.
If you need to use any internal API, please discuss it in a new issue with clear use case.

## Contribution

Please open issues or provide PRs for any features or bugs this plugin may have.
Keep in mind that all features or bugs needs to have the proper test coverage.