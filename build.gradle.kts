plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.checker)
}

group = "me.moros"
version = "2.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(libs.cloud.paper)
    implementation(libs.cloud.minecraft) { isTransitive = false }
    implementation(libs.configurate.hocon)
    compileOnly(libs.paper)
    compileOnly(libs.papi)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(project.name)
        dependencies {
            relocate("cloud.commandframework", "me.moros.hermes.internal.cf")
            relocate("com.typesafe", "me.moros.hermes.internal.typesafe")
            relocate("io.leangen", "me.moros.hermes.internal.leangen")
            relocate("org.spongepowered.configurate", "me.moros.hermes.internal.configurate")
        }
        minimize()
    }
    build {
        dependsOn(shadowJar)
    }
    withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        options.encoding = "UTF-8"
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
