plugins {
    kotlin("plugin.serialization") version "1.5.21"
}

dependencies {
    implementation(kotlin("stdlib"))
//    api(project(":nexus-api"))
    implementation("io.ktor:ktor-client-serialization:${rootProject.extra["ktorVersion"]}")
}
