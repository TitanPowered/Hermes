plugins {
    java
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "me.moros"
version = "1.1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    if (!isSnapshot()) {
        withJavadocJar()
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("cloud.commandframework","cloud-paper", "1.6.2")
    implementation("cloud.commandframework","cloud-minecraft-extras", "1.6.2") {
        exclude(group = "net.kyori")
    }
    implementation("org.spongepowered", "configurate-hocon", "4.1.2")
    compileOnly("org.checkerframework", "checker-qual", "3.21.3")
    compileOnly("io.papermc.paper", "paper-api", "1.18.2-R0.1-SNAPSHOT")
    compileOnly("me.clip", "placeholderapi", "2.11.1")
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
        options.encoding = "UTF-8"
    }
    named<Copy>("processResources") {
        filesMatching("plugin.yml") {
            expand("pluginVersion" to project.version)
        }
        from("LICENSE") {
            rename { "${project.name.toUpperCase()}_${it}"}
        }
    }
}

fun isSnapshot() = project.version.toString().endsWith("-SNAPSHOT")
