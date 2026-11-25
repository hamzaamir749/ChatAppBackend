plugins {
    id("java-library")
    id("chirp.kotlin-common")
}
group = "com.backend"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jackson.module.kotlin)
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}