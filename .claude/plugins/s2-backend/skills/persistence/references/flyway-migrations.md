# Flyway Migrations

## Rule: Use semantic versioning for migration files

### Convention
- **Location**: `src/main/resources/db/migration/`
- **Format**: `V{MAJOR}.{MINOR}.{PATCH}__{module}_{description}.sql`
- Use `IF NOT EXISTS` / `IF EXISTS` for idempotency
- One migration per feature/change

### Benefits
- **Traceability**: Versions align with product releases
- **Safety**: Idempotent migrations can be re-run
- **Organization**: Module prefix shows bounded context
- **Automation**: Flyway runs migrations on startup

### Examples

#### Initial migration
```sql
-- V1.0.0__task_init.sql
CREATE TABLE IF NOT EXISTS tasks (
    id UUID,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    status VARCHAR NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_tasks_created_at ON tasks (created_at);
```

#### Add column migration
```sql
-- V1.1.0__task_add_priority.sql
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'MEDIUM';
CREATE INDEX IF NOT EXISTS idx_tasks_priority ON tasks (priority);
```

#### Naming examples
- `V1.0.0__task_init.sql` - Initial task module
- `V2.0.0__product_init.sql` - New product module
- `V2.1.0__product_add_price.sql` - Add price column
- `V2.1.1__product_fix_index.sql` - Fix index
