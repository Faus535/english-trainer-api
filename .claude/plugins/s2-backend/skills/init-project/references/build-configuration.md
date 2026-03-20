# Build Configuration Template

## Rule: Configure Gradle with DDD-oriented dependencies

### Convention
- Use Java toolchain for LTS version
- Spring Boot plugin for dependency management
- Minimal dependency set focused on DDD backend

### build.gradle Template

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '{latest-stable}'
    id 'io.spring.dependency-management' version '{latest-stable}'
}

group = '{base-package-group}'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of({latest-lts})
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Database
    runtimeOnly 'org.postgresql:postgresql'
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.modulith:spring-modulith-starter-test'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'net.datafaker:datafaker:{latest-version}'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### settings.gradle Template

```groovy
rootProject.name = '{project-name}'
```

### Notes
- Always use the latest stable versions for Spring Boot and plugins
- Manage version catalog with `libs.versions.toml` for better maintainability
- Use Context7 to resolve exact latest versions before generating
- DataFaker replaces the deprecated JavaFaker
