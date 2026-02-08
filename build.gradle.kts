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

version = "0.3.0"

repositories {

    mavenLocal()
    maven("https://maven.supersanta.me/snapshots")
    maven("https://maven.maxhenkel.de/repository/public")
    maven("https://maven.parchmentmc.org/")
    maven("https://jitpack.io")
    maven("https://maven.nucleoid.xyz")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.andante.dev/releases/")
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.get()}@zip")
    })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)

    modImplementation(libs.arcade)
    modImplementation(libs.polymer)

}

java {
    withSourcesJar()
}

loom {
    runs {
        create("datagenClient") {
            client()
            programArgs("--arcade-datagen")
            runDir = "run-datagen"
        }
    }
    runConfigs.configureEach {
        //TODO: how to do in kotlin
//        ideConfigGenerated = true
//        vmArgs '-Dmixin.debug.export=true -DCOMMAND_STACK_TRACES=true'
    }
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





private fun DependencyHandler.includeModImplementation(dependencyNotation: Any) {
    include(dependencyNotation)
    modImplementation(dependencyNotation)
}

private fun DependencyHandler.includeImplementation(dependencyNotation: Any) {
    include(dependencyNotation)
    implementation(dependencyNotation)
}