plugins {
  java
  id("org.springframework.boot") version "3.3.2"
  id("io.spring.dependency-management") version "1.1.6"
}

group = "com.gentle.bank"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(22)
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-hateoas")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("io.micrometer:micrometer-tracing-bridge-brave")
  implementation("io.zipkin.reporter2:zipkin-reporter-brave")
  implementation("org.flywaydb:flyway-core")
  implementation("org.flywaydb:flyway-mysql")
  compileOnly("org.projectlombok:lombok")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.mysql:mysql-connector-j")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
  useJUnitPlatform()
}
