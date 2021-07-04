val ktorVersion = "1.6.1"

plugins {
    kotlin("plugin.serialization") version "1.5.20"
}

dependencies {
    implementation(kotlin("stdlib"))
    api(project(":nexus-api"))
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
}
