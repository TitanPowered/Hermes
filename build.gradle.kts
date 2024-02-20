plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.checker)
}

group = "me.moros"
version = "2.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.cloud.paper)
    implementation(libs.cloud.minecraft)
    implementation(libs.configurate.hocon)
    compileOnly(libs.paper)
    compileOnly(libs.mini.placeholders)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        dependencies {
            relocate("org.incendo.cloud", "hermes.libraries.cloud")
            relocate("com.typesafe", "hermes.libraries.typesafe")
            relocate("org.spongepowered.configurate", "hermes.libraries.configurate")
            exclude { it.moduleName.contains("geantyref") }
        }
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        options.encoding = "UTF-8"
    }
    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    named<Copy>("processResources") {
        filesMatching("*plugin.yml") {
            expand("pluginVersion" to project.version)
        }
        from("LICENSE") {
            rename { "${project.name.uppercase()}_${it}"}
        }
    }
}
