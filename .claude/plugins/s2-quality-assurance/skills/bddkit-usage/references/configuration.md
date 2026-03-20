# Configuration

## Rule: Centralize configuration in conf/ directory following BDDKit conventions

Source: `bddkit/config_files.py`, `bddkit/config_parser.py`, `bddkit/config_driver.py`

### Configuration Files Structure

```
conf/
├── properties.cfg            # Default/shared properties
├── local-properties.cfg      # Local overrides (gitignored)
├── dev-properties.cfg        # Dev environment properties
├── staging-properties.cfg    # Staging environment properties
├── production-properties.cfg # Production environment properties
└── logging.conf              # Python logging configuration
```

### properties.cfg Format (INI)

```ini
[TestExecution]
language = es
country = ES

[Driver]
type = chrome
# Options: chrome, firefox, api, no_driver
reuse_driver = false
save_web_element = false
implicitly_wait = 0
explicitly_wait = 10

[Server]
api_url = https://api.staging.example.com
base_url = https://staging.example.com

[YouTrack]
enabled = false
url = https://youtrack.example.com
token = ${YOUTRACK_TOKEN}
project = MY-PROJECT
# run_id = EXISTING-RUN-ID
# new_run = Run Summary Text
# release = v1.0.0
```

### Environment Selection at Runtime

```bash
# Select environment via -D parameter
bddkit behave-exec -D BDDKIT_CONFIG_ENVIRONMENT=staging

# This loads: conf/staging-properties.cfg (overrides conf/properties.cfg)
```

The environment name maps to `conf/<env>-properties.cfg`. Properties in the environment-specific file override the default `conf/properties.cfg`.

### Accessing Configuration from Steps

```python
from bddkit.utils.dataset import map_param

# From properties.cfg sections
api_url = map_param('[BDDKIT:Server_api_url]')
driver_type = map_param('[BDDKIT:Driver_type]')

# From environment variables
token = map_param('[ENV:API_TOKEN]')

# From project config JSON
value = map_param('[CONF:key.nested_key]')
```

### Configuration via Feature Tags

| Tag | Effect |
|-----|--------|
| `@no_driver` | Override driver type to skip browser initialization |
| `@reuse_driver` | Override `Driver.reuse_driver` to true for the feature |
| `@reset_driver` | Force driver restart before the scenario |

### Test Execution Commands

```bash
# Standard execution
bddkit behave-exec -D BDDKIT_CONFIG_ENVIRONMENT=staging -t "@exe" -t "~@todo" -t "~@wip"

# With specific tags
bddkit behave-exec -D BDDKIT_CONFIG_ENVIRONMENT=dev -t "@smoke"

# API-only tests
bddkit behave-exec -D BDDKIT_CONFIG_ENVIRONMENT=staging -t "@api"

# With YouTrack run tracking
bddkit behave-exec -D BDDKIT_CONFIG_ENVIRONMENT=staging -D BDDKIT_RUN_SCOPE=regression -D BDDKIT_RUN_ENV=staging -D BDDKIT_RUN_LAYER=api

# Sync features to YouTrack
bddkit feat2youtrack -f features/
```

### Local Overrides

Create `conf/local-properties.cfg` (gitignored) for developer-specific settings:

```ini
[Driver]
type = chrome
# headless = true

[Server]
api_url = http://localhost:8080
base_url = http://localhost:3000
```