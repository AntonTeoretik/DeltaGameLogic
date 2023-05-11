plugins {
    application
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
}

group = "com.delta"
version = "1.0-SNAPSHOT"

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
    repositories {
        maven {
            url = uri("${System.getProperty("user.home")}/.m2/repository")
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.delta"
            artifactId = "game-logic"
            version = "1.0.0"
        }
    }
    println("Published to file://${System.getProperty("user.home")}/.m2/repository")
}



