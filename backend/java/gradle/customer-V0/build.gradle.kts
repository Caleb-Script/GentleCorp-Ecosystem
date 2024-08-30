
val javaLanguageVersion = project.properties["javaLanguageVersion"] as String? ?: JavaVersion.VERSION_22.majorVersion
val javaVersion = project.properties["javaVersion"] ?: libs.versions.javaVersion.get()

val enablePreview = if (project.properties["enablePreview"] == false) null else "--enable-preview"
val imagePath = project.properties["imagePath"] ?: "gentlecorp"

val alternativeBuildpack = project.properties["buildpack"]

plugins {
  java
  id("org.springframework.boot") version "3.3.3"
  id("io.spring.dependency-management") version "1.1.6"
}

group = "com.gentle.bank"
version = "21.08.2024"
val imageTag = project.properties["imageTag"] ?: project.version.toString()

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

  /**--------------------------------------------------------------------------------------------------------------------
   * SECURITY
   * --------------------------------------------------------------------------------------------------------------------*/
  runtimeOnly("org.bouncycastle:bcpkix-jdk18on:${libs.versions.bouncycastle.get()}") // Argon2
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("com.c4-soft.springaddons:spring-addons-starter-oidc:${libs.versions.springAddonsStarterOidc.get()}")


  /**------------------------------------------------------------------------------------------------------------------------
   * SWAGGER
   * --------------------------------------------------------------------------------------------------------------------*/
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
  implementation(platform("org.springdoc:springdoc-openapi:${libs.versions.springdocOpenapi.get()}"))

  /**--------------------------------------------------------------------------------------------------------------------
   * für MAPPER
   * --------------------------------------------------------------------------------------------------------------------*/
  annotationProcessor("org.mapstruct:mapstruct-processor:${libs.versions.mapstruct.get()}")
  annotationProcessor("org.projectlombok:lombok-mapstruct-binding:${libs.versions.lombokMapstructBinding.get()}")
  implementation("org.mapstruct:mapstruct:${libs.versions.mapstruct.get()}")

  /**------------------------------------------------------------------------------------------------------------------------
   * TEST
   * --------------------------------------------------------------------------------------------------------------------*/
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.springframework.graphql:spring-graphql-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.springframework.boot:spring-boot-starter-test")

  /**----------------------------------------------------------------
   * SPRING BOOT STARTER
   **-------------------------------------------------------------*/
  implementation("org.springframework.boot:spring-boot-starter-actuator")//bei SecurityConfig
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-graphql")
  implementation("org.springframework.boot:spring-boot-starter-hateoas")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  /**--------------------------------------------------------------------------------------------------------------------
   * DATENBANK
   * --------------------------------------------------------------------------------------------------------------------*/
  runtimeOnly("org.postgresql:postgresql")
  runtimeOnly("com.mysql:mysql-connector-j")
  implementation("org.flywaydb:flyway-core")
  implementation("org.flywaydb:flyway-mysql")

  /**------------------------------------------------------------------------------------------------------------------------
   * WICHTIGE EXTRAS
   * --------------------------------------------------------------------------------------------------------------------*/
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.projectlombok:lombok:${libs.versions.lombok.get()}")
  implementation("org.springframework.boot:spring-boot-starter-web")
  annotationProcessor("org.hibernate:hibernate-jpamodelgen:${libs.versions.hibernateJpamodelgen.get()}")

  /**------------------------------------------------------------------------------------------------------------------------
   * WEITERE EXTRAS
   * --------------------------------------------------------------------------------------------------------------------*/
  implementation("com.google.guava:guava:30.1-jre") //für Splitt-operation in FlightRepository
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
  runtimeOnly("io.micrometer:micrometer-registry-prometheus")
}

tasks.withType<Test> {
  useJUnitPlatform()
}



tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.named<JavaExec>("bootRun") {
  if (enablePreview != null) {
    jvmArgs(enablePreview)
  }
  systemProperty("spring.profiles.active", "dev")
}

tasks.named<JavaCompile>("compileJava") {
  with(options) {
    isDeprecation = true
    with(compilerArgs) {
      add("-Xlint:unchecked")

      add("--add-opens")
      add("--add-exports")

      if (enablePreview != null) {
        add(enablePreview)
        //add("-Xlint:preview")
      }
    }
  }
}

tasks.named("bootBuildImage", org.springframework.boot.gradle.tasks.bundling.BootBuildImage::class.java) {
  // statt "created xx years ago": https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db
  createdDate = "now"

  // default:   imageName = "docker.io/${project.name}:${project.version}"
  imageName = "$imagePath/${project.name}:$imageTag"

  @Suppress("StringLiteralDuplication")
  environment = mapOf(
    "BP_JVM_VERSION" to javaLanguageVersion, // default: 17
    "BPL_JVM_THREAD_COUNT" to "20", // default: 250 (reactive: 50)
    "BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
    "BPE_APPEND_JAVA_TOOL_OPTIONS" to enablePreview,
  )
      imageName = imageName.get()
      println("")
      println("Buildpacks: JVM durch   B e l l s o f t   L i b e r i c a   (default)")
      println("")
    }
