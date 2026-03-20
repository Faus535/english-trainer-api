---
name: s2-init
description: Initializes a Python BDD project with BDDKit library, Behave framework, and Allure reporting
skill: s2-quality-assurance:init-project
---

# Initialize QA Project

Initializes a new Quality Assurance project based on the BDDKit library with Behave framework support for API and Web testing.

## Usage

```bash
/s2-init <project-name>
```

**Example:**
```bash
/s2-init iris
```

## Behavior

1. **Ask** for the project name if not provided as argument.
2. **Validate** the project name follows kebab-case convention (e.g., `my-project`, not `myProject` or `My_Project`).
3. **Generate** project structure by following the `s2-quality-assurance:init-project` skill.
4. **Display** a summary of the generated files and next steps.

## What it generates

### Project structure
- Defined in the init-project skill

### Dependencies

- **BDDKit**: Core testing library (includes Behave). Version defined in `requirements.txt`.
- **Allure-Behave**: Test reporting integration. Requires Java runtime for Allure CLI report generation.

## Next steps

After initializing the project:

1. Review and adjust environment configuration in `conf/properties.cfg`
2. Generate `CLAUDE.md` for the project:
   ```bash
   /s2-setup-claude
   ```
3. Create your first test plan from a YouTrack user story or bug:
   ```bash
   /s2-create-plan
   ```
4. Generate test code:
   ```bash
   /s2-create
   ```
5. Execute the plan:
   ```bash
   /s2-execute-plan
   ```

## References

- Skill: [init-project](../skills/init-project/SKILL.md)