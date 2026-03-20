# Environment Hooks

## Rule: Use BDDKit's environment.py as the base, extend with project-specific logic

BDDKit provides a complete `environment.py` implementation that handles driver lifecycle, configuration, storage, and YouTrack integration. Projects should import and extend it, not rewrite it.

Source: `bddkit/behave/environment.py`

### BDDKit environment.py - What It Does

The built-in `before_all` hook performs these initializations:

1. **Environment config** - Reads `BDDKIT_CONFIG_ENVIRONMENT` from `-D` parameter
2. **Config files** - Loads `conf/*-properties.cfg` via `ConfigFiles` and `DriverWrappersPool`
3. **Driver wrapper** - Creates and configures the default driver wrapper
4. **Storage** - Initializes `context.run_storage` and `context.storage`
5. **Dataset** - Connects `bddkit.utils.dataset` to the behave context and config
6. **Dynamic environment** - Sets up `DynamicEnvironment` for feature-description-based steps
7. **POM manager** - Registers `context.use_pageobject()` for dynamic page object loading
8. **YouTrack** - Initializes YouTrack client and optionally creates a test run

### Using BDDKit's Environment Directly

The simplest approach: import BDDKit's environment directly.

```python
# environment.py (project root)
from bddkit.behave.environment import *
```

This gives full BDDKit lifecycle management with zero custom code.

### Extending BDDKit's Environment

To add project-specific setup while keeping BDDKit's lifecycle:

```python
# environment.py (project root)
from bddkit.behave import environment as bddkit_env
from bddkit.utils import dataset


def before_all(context):
    bddkit_env.before_all(context)
    # Project-specific initialization
    dataset.language = context.bddkit_config.get_optional('TestExecution', 'language', 'es')
    dataset.country = context.bddkit_config.get_optional('TestExecution', 'country', 'ES')


def before_feature(context, feature):
    bddkit_env.before_feature(context, feature)
    # Project-specific feature setup


def before_scenario(context, scenario):
    bddkit_env.before_scenario(context, scenario)
    # Project-specific scenario setup
    context.logger.info(f"Starting scenario: {scenario.name}")


def after_scenario(context, scenario):
    bddkit_env.after_scenario(context, scenario)
    # Project-specific cleanup
    if scenario.status == 'failed' and 'web' in scenario.feature.tags:
        _capture_screenshot(context, scenario.name)


def after_feature(context, feature):
    bddkit_env.after_feature(context, feature)


def after_all(context):
    bddkit_env.after_all(context)


def _capture_screenshot(context, name):
    if hasattr(context, 'driver') and context.driver:
        context.driver.save_screenshot(f'output/screenshots/{name}.png')
```

### Key Context Attributes Set by BDDKit

After `before_all` completes, these attributes are available:

| Attribute | Type | Description |
|-----------|------|-------------|
| `context.driver_wrapper` | `DriverWrapper` | Default driver wrapper |
| `context.bddkit_config` | `ExtendedConfigParser` | BDDKit configuration |
| `context.logger` | `Logger` | Logging instance |
| `context.run_storage` | `dict` | Run-level storage (persists across features) |
| `context.feature_storage` | `dict` | Feature-level storage (cleared per feature) |
| `context.storage` | `ChainMap` | Cascading storage (scenario -> feature -> run) |
| `context.use_pageobject` | `function` | Dynamic page object loader |
| `context.store_key_in_storage` | `function` | Helper to store values in proper storage level |
| `context.dyn_env` | `DynamicEnvironment` | Dynamic environment for feature-description steps |
| `context.driver` | `WebDriver` | Browser driver (after `before_scenario` for web tests) |
| `context.utils` | `Utils` | Driver utility methods |

### Driver Lifecycle Tags

| Tag | Applied To | Effect |
|-----|-----------|--------|
| `@no_driver` | Feature or Scenario | Skip browser driver initialization |
| `@reuse_driver` | Feature | Reuse driver across all scenarios in the feature |
| `@reset_driver` | Scenario | Force driver restart before this scenario |

### DynamicEnvironment - Feature Description Steps

BDDKit supports defining setup/teardown steps in the feature description:

```gherkin
Feature: User management
  actions before the feature
    Given I start mock server on "http://localhost:9090"
  actions before each scenario
    Given I reset the api request
  actions after each scenario
    Given I clear mock server
  actions after the feature
    Given I stop mock server

  Scenario: List users
    Given I set request url to "http://localhost:9090"
    ...
```

Supported action labels:
- `actions before the feature`
- `actions before each scenario`
- `actions after each scenario`
- `actions after the feature`

Steps in these sections are parsed by `DynamicEnvironment.get_steps_from_feature_description()` and executed at the appropriate lifecycle phase.

### YouTrack Integration in Hooks

When YouTrack is enabled in config (`[YouTrack] enabled = true`):
- `before_all`: Creates a test run if `new_run` is configured
- `after_scenario`: Reports scenario pass/fail status to YouTrack using the first tag as test case ID (format: `@PROJECT-123`)
- `after_all`: Generates and attaches Allure report to the YouTrack run