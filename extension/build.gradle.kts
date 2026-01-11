plugins {
    /* Kotlin */
    kotlin("jvm") version "2.2.10"
    /* Typewriter */
    id("com.typewritermc.module-plugin") version "2.1.0"
}

group = "fr.legendsofxania"
version = "0.0.1"

repositories {
    mavenLocal()
}
dependencies {
    compileOnly("fr.legendsofxania:StructureExtension:0.0.1")
}

typewriter {
    namespace = "legendsofxania"

    extension {
        name = "Dungeon"
        shortDescription = "Create dungeons in Typewriter."
        description = """
            Creating dungeons has never been easier with Typewriter a powerful,
            intuitive tool that helps you design immersive rooms and 
            adventures quickly and creatively, saving you time and effort.
        """.trimIndent()
        engineVersion = "0.9.0-beta-170"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.BETA

        dependencies {
            dependency("legendsofxania", "Structure")
        }
        paper()
    }
}

kotlin {
    jvmToolchain(21)
}