import java.util.Locale
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.component.external.model.ProjectTestFixtures
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.gradle.plugins.ide.idea.model.internal.GeneratedIdeaScope
import org.gradle.plugins.ide.idea.model.internal.IdeaDependenciesProvider

plugins {
  val kotlinPluginVersion = "2.1.10"
  kotlin("jvm") version kotlinPluginVersion
  kotlin("plugin.spring") version kotlinPluginVersion
  kotlin("plugin.jpa") version kotlinPluginVersion

  id("com.ncorti.ktfmt.gradle") version "0.21.0"

  id("org.springframework.boot") version "3.4.3"
  id("io.spring.dependency-management") version "1.1.7"

  id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.10"

  `java-test-fixtures`
}

group = "com.barbzdev"

version = "1.0.0"

repositories { mavenCentral() }

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-security")

  implementation("io.micrometer:micrometer-registry-prometheus:1.14.4")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation("org.flywaydb:flyway-core")
  implementation("org.flywaydb:flyway-database-postgresql")

  implementation("org.jetbrains.kotlin:kotlin-reflect")

  runtimeOnly("org.postgresql:postgresql")

  // OpenAPI
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
  implementation("org.springdoc:springdoc-openapi-starter-common:2.6.0")

  // Testing
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(module = "mockito-core")
  }
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:4.2.0")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

  testImplementation("org.testcontainers:postgresql")
  testImplementation("org.testcontainers:junit-jupiter")
  testFixturesImplementation("org.testcontainers:postgresql")
  testFixturesImplementation("org.testcontainers:junit-jupiter")

  testImplementation("io.mockk:mockk:1.13.17")
  testImplementation("com.ninja-squad:springmockk:4.0.2")

  testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
  testImplementation("org.assertj:assertj-core:3.27.3")

  testImplementation("org.awaitility:awaitility:4.3.0")

  testImplementation("io.rest-assured:rest-assured:5.5.1")

  testImplementation("com.tngtech.archunit:archunit-junit5:1.4.0")

  testFixturesImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

addTestSet("integrationTest")
addTestSet("acceptanceTest")
addTestSet("architectureTest")

kotlin {
  compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") }
  jvmToolchain { languageVersion = JavaLanguageVersion.of(21) }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

tasks.withType<Test> { useJUnitPlatform() }

tasks.named("check") {
  dependsOn("jacocoIntegrationTestReport", "jacocoAcceptanceTestReport", "jacocoArchitectureTestReport")
}

tasks.withType(Test::class) {
  testLogging {
    events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    exceptionFormat = TestExceptionFormat.FULL
  }

  useJUnitPlatform()
  // Fix encoding issues on GHA Runners
  systemProperty("file.encoding", "UTF-8")
}

tasks.named<Test>("integrationTest") {
  mustRunAfter(tasks.named<Test>("test"))

  outputs.upToDateWhen { false }
  outputs.cacheIf { false }
}

tasks.named<Test>("acceptanceTest") {
  mustRunAfter(tasks.named<Test>("integrationTest"))

  outputs.upToDateWhen { false }
  outputs.cacheIf { false }
}

ktfmt {
  googleStyle()
  maxWidth.set(120)
  manageTrailingCommas.set(false)
}

private fun Project.addTestSet(name: String) {
  sourceSets {
    create(name) {
      compileClasspath += sourceSets["main"].output
      runtimeClasspath += sourceSets["main"].output
    }
  }

  with(configurations) {
    // TODO("not working properly need to fix)
    named("${name}Implementation") {
      extendsFrom(configurations["testImplementation"])
    }
    named("${name}RuntimeOnly") {
      extendsFrom(configurations["testRuntimeOnly"])
    }
  }

  plugins.withType<JavaTestFixturesPlugin> {
    val testDependency = dependencies.add("${name}Implementation", dependencies.create(project)) as ProjectDependency
    testDependency.capabilities(ProjectTestFixtures(project))
  }

  plugins.withType<IdeaPlugin> {
    with(the<IdeaModel>()) {
      module {
        testSources.from(testSources, sourceSets[name].allJava.srcDirs)
        testResources.from(testResources, sourceSets[name].resources.srcDirs)

        val test = scopes[GeneratedIdeaScope.TEST.name]!![IdeaDependenciesProvider.SCOPE_PLUS]
        test!!.add(configurations["${name}CompileClasspath"])
        test.add(configurations["${name}RuntimeClasspath"])
      }
    }
  }

  val testTask = tasks.register<Test>(name) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Runs the ${name.removeSuffix("Test")} tests."

    testClassesDirs = sourceSets[name].output.classesDirs
    classpath = sourceSets[name].runtimeClasspath
  }

  tasks.register<JacocoReport>("jacoco${name.replaceFirstChar { it.titlecase(Locale.ROOT) }}Report") {
    dependsOn(testTask)
    executionData(testTask.get())
    sourceSets(sourceSets["main"])
  }
}
