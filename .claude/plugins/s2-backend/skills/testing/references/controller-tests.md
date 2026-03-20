# Controller Tests (MockMvc)

Controller tests verify the HTTP layer in isolation: status codes, response body, and Use Case invocation.

## Conventions

- Use `@WebMvcTest(XxxController.class)` — not full `@SpringBootTest`
- Mock the Use Case with `@MockBean`
- Verify: HTTP status, response body, that the Use Case was called with correct args

## Example

```java
@WebMvcTest(CreateUserController.class)
class CreateUserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CreateUserUseCase useCase;

    @Test
    void shouldCreateUserWhenValidRequest() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "name": "John Doe", "email": "john@example.com" }
                """))
            .andExpect(status().isCreated());

        verify(useCase).execute(any(), eq("John Doe"), eq("john@example.com"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "name": "", "email": "john@example.com" }
                """))
            .andExpect(status().isBadRequest());
    }
}
```
