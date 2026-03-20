# Web Interactions

## Rule: Handle waits, screenshots, and browser state through page objects and hooks

### Convention
- Use explicit waits, never `time.sleep()`
- Capture screenshots on failure in `after_scenario` hook
- Initialize and tear down the browser driver in hooks
- Pass the driver through `context`

### Examples

#### Browser setup in hooks
```python
# environment.py

def before_scenario(context, scenario):
    if "web" in scenario.effective_tags:
        # TODO: Initialize browser driver
        # context.driver = webdriver.Chrome()
        # context.driver.maximize_window()
        pass


def after_scenario(context, scenario):
    if hasattr(context, "driver"):
        if scenario.status == "failed":
            # TODO: Capture screenshot
            # context.driver.save_screenshot(f"allure-results/{scenario.name}.png")
            pass
        # TODO: Close browser
        # context.driver.quit()
```

#### Explicit waits in page objects
```python
# pageobjects/base_page.py


class BasePage:
    """Base page object with common wait utilities."""

    def __init__(self, driver, timeout=10):
        self.driver = driver
        self.timeout = timeout

    def wait_for_element(self, locator):
        """Wait for an element to be visible."""
        # TODO: Use WebDriverWait
        # from selenium.webdriver.support.ui import WebDriverWait
        # from selenium.webdriver.support import expected_conditions as EC
        # return WebDriverWait(self.driver, self.timeout).until(
        #     EC.visibility_of_element_located(locator)
        # )
        pass

    def wait_for_element_clickable(self, locator):
        """Wait for an element to be clickable."""
        # TODO: Use WebDriverWait with element_to_be_clickable
        pass
```