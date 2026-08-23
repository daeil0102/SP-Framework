plugins {
    id("java")
    alias(libs.plugins.shadow)
}

base {
    archivesName.set("${project.rootProject.name}")
    version = "${project.version}-JPALib"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.mariadb.java.client)
    compileOnly(libs.hibernate.core)
    compileOnly(libs.slf4j)
    compileOnly(project(":lang"))
}

tasks.test {
    useJUnitPlatform()
}