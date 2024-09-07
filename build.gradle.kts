plugins {
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.ben-manes.versions") version "0.51.0"
	id("io.ktor.plugin") version "2.3.12"
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.spring") version "2.0.20"
	kotlin("plugin.jpa") version "2.0.20"
	kotlin("plugin.serialization") version "2.0.20"
}

group = "com.inovex"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_22
project.setProperty("mainClassName", "com.inovex.inoventory.InoventoryApplication")

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-oauth2-resource-server")
	implementation("org.springframework.security:spring-security-oauth2-jose")


	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.ktor:ktor-server-core")
	implementation("io.ktor:ktor-client-cio")
	implementation("io.ktor:ktor-serialization-kotlinx-json")
	implementation("io.ktor:ktor-client-content-negotiation")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
	testImplementation("io.ktor:ktor-client-mock")
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
	useJUnitPlatform()
}