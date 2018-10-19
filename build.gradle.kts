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
    implementation("org.apache.commons:commons-text:1.5")
    testImplementation(gradleTestKit())
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.5") {
        exclude("groovy-all")
    }
    testImplementation("org.gradle:sample-check:latest.integration") {
        version {
            branch = "lacasseio/support-kotlin-1.3-rc"
        }
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
    website = "https://vscode.lacasse.io/"
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