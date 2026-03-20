---
name: s2-youtrack-integration
description: Syncs test definitions with YouTrack
---

# YouTrack Integration

Syncs test definitions from `.feature` files with YouTrack by running the BDDKit integration command.

## Usage

```bash
/s2-youtrack-integration                    # Sync all modified feature files
/s2-youtrack-integration <path>             # Sync specific feature file or directory
```

## Behavior

1. **Detect** which `.feature` files have been modified (using `git diff` or user-specified path).
2. **Run** the BDDKit sync command against the modified files:
   ```bash
   bddkit feat2youtrack -f <feature-files>
   ```
3. **Display** the sync results (created/updated test cases in YouTrack).

## Prerequisites

- `conf/properties.cfg` must have `YouTrack_project` configured with the correct project key
- BDDKit must be installed and configured for YouTrack access

## Example output

```
$ /s2-youtrack-integration

Detecting modified feature files...

Modified features:
  - features/web/user_login.feature
  - features/api/users.feature

Running: bddkit feat2youtrack -f features/web/user_login.feature features/api/users.feature

Results:
  ✓ Created: QA-101 — Successful login with valid credentials
  ✓ Created: QA-102 — Failed login with invalid credentials
  ✓ Updated: QA-85 — Create a new user via API

---
3 test cases synced to YouTrack (2 created, 1 updated)
```

## References

- Skill: [bddkit-usage](../skills/bddkit-usage/SKILL.md)