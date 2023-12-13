plugins {
    kotlin("jvm")
}

val ktorVersion: String by project


group = "ru.kyamshanov.local"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}