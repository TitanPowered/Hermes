plugins {
    id("java-library")
    alias(libs.plugins.shadow)
}

group = "me.moros"
version = "2.3.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(libs.jspecify)
    implementation(libs.cloud.paper)
    implementation(libs.cloud.signed)
    implementation(libs.cloud.signed.paper)
    implementation(libs.cloud.minecraft)
    implementation(libs.configurate.hocon)
    compileOnly(libs.paper)
    compileOnly(libs.mini.placeholders)
}

tasks {
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    shadowJar {
        archiveClassifier = ""
        archiveBaseName = project.name
        val licenseName = "LICENSE_${rootProject.name.uppercase()}"
        from("$rootDir/LICENSE") {
            into("META-INF")
            rename { licenseName }
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
    named<Copy>("processResources") {
        inputs.property("version", project.version)
        inputs.property("mcVersion", libs.versions.minecraft.get())
        filesMatching("paper-plugin.yml") {
            expand(inputs.properties)
        }
    }
}
