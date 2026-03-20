---
name: s2-setup-claude
description: Generates CLAUDE.md with skill references based on the chosen architectural style
model: haiku
---

# Setup CLAUDE.md

Generates the `CLAUDE.md` file in the project root with references to s2-backend skills based on the architectural style.

## Usage

```bash
/s2-setup-claude
```

## Behavior

1. **Detect project name** from `settings.gradle` or `pom.xml`
2. **Detect base package** from `Application.java`
3. **Detect infrastructure dependencies** from `build.gradle` or `pom.xml`. Look for:
   - **Relational**: `spring-boot-starter-data-jdbc`, `spring-boot-starter-data-jpa`
   - **Search**: `spring-boot-starter-data-elasticsearch`
   - **Cache/NoSQL**: `spring-boot-starter-data-redis`, `spring-boot-starter-data-mongodb`
   - **Messaging**: `spring-kafka`, `spring-boot-starter-amqp`
   - **Build tool**: Gradle or Maven
   - Any other `spring-data-*` or infrastructure dependency
4. **Detect security** presence (`spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`)
5. **Ask** the user for the architectural style:
   - **Modulith** (Use Cases) - default
   - **CQRS** (coming soon)
   - **Event-Driven** (coming soon)
6. **Show detected configuration** to the user for confirmation:
   ```
   Detected:
   - Name: {project-name}
   - Package: {base-package}
   - Build: Gradle
   - Persistence: Spring Data JDBC, Elasticsearch
   - Security: Yes (OAuth2 Resource Server)

   Is this correct?
   ```
7. **Consult** the skill `s2-backend:setup-claude` for the generic CLAUDE.md template
8. **Generate** `CLAUDE.md` replacing placeholders with detected values
9. If it already exists, ask whether to overwrite
10. **Generate `.claudeignore`** if it does not exist, with at minimum:
    ```
    .env
    .env.*
    *credentials*
    *secret*
    application-local.yml
    application-local.properties
    *.pem
    *.key
    *.p12
    *.jks
    ```
    If `.claudeignore` already exists, show its contents and suggest adding any missing entries from the list above.

## Placeholder Resolution

The template uses these placeholders that must be replaced with detected values:

| Placeholder | Source | Example |
|---|---|---|
| `{project-name}` | `settings.gradle` / `pom.xml` | `my-project` |
| `{base-package}` | `Application.java` package | `com.s2grupo.myproject` |
| `{stack}` | Detected dependencies | `Spring Boot, Spring Data JDBC, Elasticsearch, Java, Gradle` |
| `{persistence-line}` | Detected persistence techs | `Spring Data JDBC (Persistable<UUID>), Elasticsearch` |
| `{repo-naming-rows}` | One row per persistence tech | `UserJdbcRepository`, `UserElasticRepository` |
| `{security-skills}` | If security detected | Include security + security-audit skills |
