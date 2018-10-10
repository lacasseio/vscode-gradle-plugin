plugins {
    `groovy`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish").version("0.10.0")
}

group = "io.lacasse.vscode"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation("com.google.guava:guava:26.0-jre")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("commons-io:commons-io:2.6")
    testImplementation(gradleTestKit())
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.4") {
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
            implementationClass = "io.lacasse.vscode.gradle.internal.plugins.VisualStudioCodePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/lacasseio/vscode-gradle-plugin"
    vcsUrl = "https://github.com/lacasseio/vscode-gradle-plugin"
    description = "Gradle plugin for generating Visual Studio Code IDE files."
    tags = listOf("visual-studio-code", "vscode", "ide")

    plugins {
        val vscode by existing {
            // id is captured from java-gradle-plugin configuration
            displayName = "Visual Studio Code Gradle Plugin"
        }
    }
}

tasks.register("continuousIntegration") {
    dependsOn(tasks.named("check"))
}