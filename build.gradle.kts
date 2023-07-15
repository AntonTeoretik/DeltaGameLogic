plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
}

group = "com.delta"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

publishing {
    publications {
        create<MavenPublication>("myLibrary") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "DeltaGameLogic"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }

//    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//            groupId = "com.delta"
//            artifactId = "game-logic"
//            version = "1.0.0"
//        }
//    }
}



