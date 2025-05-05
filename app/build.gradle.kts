plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.spring") version "2.0.10"
}

group = "com.sample"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("software.amazon.kinesis:amazon-kinesis-client:3.0.1")
	implementation("software.amazon.awssdk:kinesis:2.30.26")
	implementation("software.amazon.awssdk:dynamodb:2.30.26")
	implementation("software.amazon.awssdk:cloudwatch:2.30.26")
	implementation("software.amazon.awssdk:auth:2.30.26")
	implementation("software.amazon.awssdk:core:2.30.26")
	implementation("software.amazon.awssdk:regions:2.30.26")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("com.sample.kcl.ApplicationKt")
}
