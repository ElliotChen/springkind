import org.apache.tools.ant.filters.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.*

plugins {
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.gorylenko.gradle-git-properties") version "2.4.2"
	kotlin("jvm") version "2.0.0"
	kotlin("plugin.spring") version "2.0.0"
	kotlin("plugin.jpa") version "2.0.0"
}

group = "tw.elliot"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

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
	testImplementation("org.projectlombok:lombok:1.18.34")
	compileOnly("org.projectlombok:lombok")
	implementation("com.mysql:mysql-connector-j:9.0.0")
	implementation("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.ajoberstar.grgit:grgit-core:5.2.2")

	implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter")
}

dependencyManagement {
	imports {
		mavenBom("io.opentelemetry:opentelemetry-bom:1.40.0")
		mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-boma:2.5.0")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
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

tasks.withType<JavaCompile>(){
	options.compilerArgs.add("-parameters")
}
