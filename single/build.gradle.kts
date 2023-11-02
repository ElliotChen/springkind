import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.*
import org.apache.tools.ant.filters.*

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.0"
	id("com.gorylenko.gradle-git-properties") version "2.4.1"
	kotlin("jvm") version "1.8.21"
	kotlin("plugin.spring") version "1.8.21"
	kotlin("plugin.jpa") version "1.8.21"
}

group = "tw.elliot"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val activeProfile=project.properties["activeProfile"] ?: "local"
val imageVersion=project.properties["imageVersion"] ?: "latest"

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
	//implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.postgresql:postgresql")
	testImplementation("org.projectlombok:lombok:1.18.22")
	compileOnly("org.projectlombok:lombok")
	implementation("com.mysql:mysql-connector-j:8.0.31")
	implementation("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.ajoberstar.grgit:grgit-core:4.1.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.getByName<BootBuildImage>("bootBuildImage") {
	imageName.set("elliot/single/$activeProfile:"+imageVersion)
	verboseLogging.set(true)
	buildpacks.addAll(
		"urn:cnb:builder:paketo-buildpacks/java",
		"gcr.io/paketo-buildpacks/opentelemetry")
	environment.put("BP_OPENTELEMETRY_ENABLED","true")
}

tasks.withType<ProcessResources> {
	println("Got active profile [$activeProfile]")
	val filterTokens = mapOf("activeProfile" to activeProfile)
	filter<ReplaceTokens>("tokens" to filterTokens)
}

tasks.withType<BootJar> {
	println("current profile is [$activeProfile]")
	if (activeProfile == "local") {
		exclude("application-docker.yml")
		exclude("application-k8s.yml")
	} else {
		exclude("application-*.yml")
	}
}

tasks.register<Copy>("copyDependenciesToLib") {
	into("$buildDir/libs")
	from(project.configurations.runtimeClasspath)
	doLast {
		println("copyDependenciesToLib:\n  ${project.configurations.runtimeClasspath.get().files.joinToString("\n  ") { it.absolutePath }}\n  ->\n  $buildDir/libs")
	}
}

