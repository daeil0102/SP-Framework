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
    implementation(libs.mariadb.java.client)
    implementation(libs.hibernate.core)
    implementation(libs.slf4j)
    implementation(project(":lang"))
}

tasks.test {
    useJUnitPlatform()
}