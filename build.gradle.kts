plugins {
    `maven-publish`

    alias(libs.plugins.leaf.loom)
}

loom {
    mods {
        create(project.name) {
            sourceSet(sourceSets.main.get())
        }
    }
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    mavenLocal()
}

dependencies {
    // To change the versions, see gradle/libs.versions.toml
    zomboid(libs.zomboid)
    implementation(libs.leaf.loader)
    // implementation(libs.leaf.api)
}

base {
    archivesName = project.name
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks {
    processResources {
        val projectVersion: String = project.version.toString()
        val loaderVersion: String = libs.versions.leaf.loader.get().replace(".local", "")
        val zomboidVersion: String = libs.versions.zomboid.get()

        inputs.property("version", projectVersion)
        inputs.property("loader_version", loaderVersion)
        inputs.property("zomboid_version", zomboidVersion)

        filesMatching("leaf.mod.json") {
            expand(
                "version" to projectVersion,
                "loader_version" to loaderVersion,
                "zomboid_version" to zomboidVersion
            )
        }
    }

    jar {
        from("LICENSE") {
            rename {
                "${it}_${inputs.properties["archivesName"]}"
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components.getByName("java"))
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}
