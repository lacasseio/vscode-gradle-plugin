plugins {
    `groovy`
    `java-gradle-plugin`
}

group = "io.lacasse.vscode"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation("com.google.guava:guava:26.0-jre")
    implementation("com.google.code.gson:gson:2.8.5")
    testImplementation(gradleTestKit())
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.5") {
        exclude("groovy-all")
    }
}

repositories {
    mavenCentral()
}

gradlePlugin {
    (plugins) {
        create("vscode") {
            id = "io.lacasse.vscode"
            implementationClass = "io.lacasse.vscode.gradle.plugins.VisualStudioCodePlugin"
        }
        create("compileCommands") {
            id = "io.lacasse.compile-commands"
            implementationClass = "io.lacasse.vscode.gradle.plugins.CompileCommandPlugin"
        }
    }
}
