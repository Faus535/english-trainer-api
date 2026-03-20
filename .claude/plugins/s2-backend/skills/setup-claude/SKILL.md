---
name: setup-claude
description: This skill should be used when the user asks to "generate CLAUDE.md", "setup claude", "configure claude for project", or runs "/s2-setup-claude". Generates CLAUDE.md with architecture conventions and skill references based on the chosen architectural style.
---

# Skill: Setup Claude

Generates the `CLAUDE.md` file in the project root with instructions for Claude Code to understand the project's
architecture and patterns.

## Command

```bash
/s2-setup-claude
```

## Behavior

1. Detect project name from `settings.gradle` or `pom.xml`
2. Detect base package from `Application.java`
3. Detect infrastructure dependencies from `build.gradle` or `pom.xml` (persistence, search, cache, messaging, security)
4. Ask the user for the architectural style:
    - **Modulith** (Use Cases) - default
    - **CQRS** (coming soon)
    - **Event-Driven** (coming soon)
5. Show detected configuration and ask for confirmation
6. Generate `CLAUDE.md` with the corresponding template
7. If it already exists, ask whether to overwrite

## Template

The CLAUDE.md template is in [references/template.md](references/template.md). It is a generic template with
placeholders for the architectural style, persistence technologies, and security. See the template file for the full
list of placeholders, conditional sections, and style-specific values.

## Target size

The generated CLAUDE.md should be **under 100 lines**. Keep it concise — skills contain the details.
