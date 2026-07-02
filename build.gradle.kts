plugins {
    val jvmVersion = libs.versions.fabric.kotlin.get()
        .split("+kotlin.")[1]
        .split("+")[0]

    kotlin("jvm").version(jvmVersion)
    kotlin("plugin.serialization").version(jvmVersion)
    alias(libs.plugins.fabric.loom)
    `maven-publish`
    java
}

version = project.version

repositories {
    mavenLocal()
    maven("https://maven.supersanta.me/snapshots")
    maven("https://maven.maxhenkel.de/repository/public")
    maven("https://maven.parchmentmc.org/")
    maven("https://jitpack.io")
    maven("https://maven.nucleoid.xyz")
    maven("https://api.modrinth.com/maven")
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)

    //TODO allow disabling such a fat jar
    includeImplementation(libs.fabric.kotlin)
    includeImplementation(libs.arcade)
    //Transitive of arcade..., should this be explicit includeImplementation?
    implementation(libs.polymer)

    implementation(libs.fabric.api)

}

java {
    withSourcesJar()
}

loom {
    runConfigs.configureEach {
        vmArgs("-Dmixin.debug.export=true -DCOMMAND_STACK_TRACES=true -DMC_DEBUG_ENABLED=true -DMC_DEBUG_COMMAND_STACK_TRACES=true -DMC_DEBUG_VERBOSE_COMMAND_ERRORS=true")
    }
    accessWidenerPath.set(file("src/main/resources/twists.accesswidener"))
}

tasks {
    processResources {
        inputs.property("version", version)
        filesMatching("fabric.mod.json") {
            expand(mutableMapOf(
                "version" to version,
                "minecraft_version" to libs.versions.minecraft.get().replaceAfterLast('.', "x"),
                "fabric_version" to libs.versions.fabric.api.get(),
                "arcade_version" to libs.versions.arcade.get(),
                "fabric_language_kotlin_version" to libs.versions.fabric.kotlin.get(),
            ))
        }
    }

    jar {
        from("LICENSE")
    }
}





private fun DependencyHandler.includeImplementation(dependencyNotation: Any) {
    include(dependencyNotation)
    implementation(dependencyNotation)
}