import org.gradle.kotlin.dsl.dependencies

// Configure Kotlin and Spring Boot plugins
plugins {
    id("chirp.kotlin-common")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// Define application dependencies
dependencies {
    // Kotlin reflection and standard library
    "implementation"(libraries.findLibrary("kotlin-reflect").get())
    "implementation"(libraries.findLibrary("kotlin-stdlib").get())

    // Spring Boot web support for building REST APIs
    "implementation"(libraries.findLibrary("spring-boot-starter-web").get())

    // Testing dependencies
    "testImplementation"(libraries.findLibrary("spring-boot-starter-test").get())
    "testImplementation"(libraries.findLibrary("kotlin-test-junit5").get())
    "testRuntimeOnly"(libraries.findLibrary("junit-platform-launcher").get())
}