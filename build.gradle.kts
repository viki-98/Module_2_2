plugins {
    id("java")
}

group = "org.post_hub"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object Versions {
    const val postgresql = "42.7.2"
    const val liquibase = "4.33.0"
    const val mockito = "5.21.0"
    const val mockitoJupiter = "5.20.0"
    const val junitBom = "5.10.0"
}

dependencies {
    implementation("org.postgresql:postgresql:${Versions.postgresql}")
    implementation("org.liquibase:liquibase-core:${Versions.liquibase}")
    testImplementation("org.mockito:mockito-core:${Versions.mockito}")
    testImplementation("org.mockito:mockito-junit-jupiter:${Versions.mockitoJupiter}")

    testImplementation(platform("org.junit:junit-bom:${Versions.junitBom}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}