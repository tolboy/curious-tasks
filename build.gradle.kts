plugins {
    id("java")
}

group = "ru.tolboy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("org.tinylog:tinylog-api:2.6.1")
    implementation("org.tinylog:tinylog-impl:2.6.1")
}

tasks.test {
    useJUnitPlatform()
}