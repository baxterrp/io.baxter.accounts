import org.gradle.kotlin.dsl.implementation

plugins {
	java
    jacoco
    id("org.sonarqube") version "5.1.0.4882"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "io.baxter.accounts"
version = "0.0.1-SNAPSHOT"
description = "Account Service"

repositories {
	mavenCentral()
}

dependencies {
    // lombok for DI
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // spring
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.7.0")

    // Reactive database stack
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.asyncer:r2dbc-mysql:1.3.0")

    // JWT support
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // unit testing
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


jacoco {
    toolVersion = "0.8.13"
}

sonarqube {
    properties {
        property("sonar.projectKey", "baxterrp_io-baxter-accounts")
        property("sonar.organization", "baxterrp")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.projectName", "io.baxter.accounts")
        property("sonar.sources", "src/main/java")
        property("sonar.tests", "src/test/java")
        property("sonar.language", "java")
        property("sonar.java.binaries", "build/classes/java")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.check{
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    mainClass.set("io.baxter.accounts.Application")
}