plugins {
    id("java")
}

base {
    archivesName.set("${project.rootProject.name}")
    version = "${project.version}-Lang"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.slf4j)
}

tasks.test {
    useJUnitPlatform()
}