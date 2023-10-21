plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.sonarqube") version "4.2.1.3168"
    //id("org.sonarqube") version "4.3.1.3277"
    jacoco
}

group = "de.cofinpro"
version = "0.0.1-SNAPSHOT"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "expenses-splitter")
        property("sonar.projectName", "expenses-splitter")
        property("sonar.jacoco.reportPaths", "build/reports/jacoco")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.token", "sqp_d436210e94100d7ef46cf7a5c9ffabcafdd12657")
        property("sonar.host.url", "http://localhost:9000")
    }
}

tasks.sonar {
    dependsOn(tasks.jacocoTestReport)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:5.2.0")
}

tasks.named("compileJava") {
    inputs.files(tasks.named("processResources"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
