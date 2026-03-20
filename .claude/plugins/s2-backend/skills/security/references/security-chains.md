# Security Chains

Multiple `@SecurityFilterChain` beans with `@Order` allow different policies per path group. Lower order = higher priority.

## Chain layout (Carmen reference)

| Order | Matcher | Auth | Session |
|---|---|---|---|
| 0 | `/actuator/**`, `/v3/api-docs/**`, `/swagger-ui/**`, `/error` | `permitAll()` | `IF_REQUIRED` |
| 1 | `/integrator/**` | `permitAll()` | `IF_REQUIRED` |
| 2 | `/sse/**` | JWT required | `IF_REQUIRED` |
| 3 | `/**` | JWT + API key | `IF_REQUIRED` |

> **Note on integrator chain**: `/integrator/**` uses `permitAll()` — auth is handled by the endpoint logic itself (API key validation, etc.), not by Spring Security.

## Example

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class WebSecurityConfiguration {

    @Bean
    @Order(0)
    SecurityFilterChain publicChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/error")
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(reg -> reg.anyRequest().permitAll())
            .build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain integratorChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher("/integrator/**")
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(reg -> reg.anyRequest().permitAll())
            .build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain sseChain(
        HttpSecurity http,
        UserServiceEnrichedJwtAuthConverter jwtConverter,
        TenantContextFilter tenantContextFilter,
        JwtDecoder jwtDecoder
    ) throws Exception {
        return http
            .securityMatcher("/sse/**")
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                jwt.decoder(jwtDecoder);
                jwt.jwtAuthenticationConverter(jwtConverter);
            }))
            .addFilterAfter(tenantContextFilter, BearerTokenAuthenticationFilter.class)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain defaultChain(
        HttpSecurity http,
        UserServiceEnrichedJwtAuthConverter jwtConverter,
        JwtDecoder jwtDecoder,
        PostAuthProvisioningFilter postAuthFilter,
        ApiKeyAuthenticationFilter apiKeyFilter,
        TenantContextFilter tenantContextFilter
    ) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
                jwt.decoder(jwtDecoder);
                jwt.jwtAuthenticationConverter(jwtConverter);
            }))
            .addFilterBefore(apiKeyFilter, BearerTokenAuthenticationFilter.class)
            .addFilterAfter(postAuthFilter, BearerTokenAuthenticationFilter.class)
            .addFilterAfter(tenantContextFilter, PostAuthProvisioningFilter.class)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .build();
    }
}
```

## Rules

- Always define a catch-all chain at the highest order number
- CSRF disabled for token-based APIs
- `TenantContextFilter` always **after** `PostAuthProvisioningFilter` in the same chain
- `ApiKeyAuthenticationFilter` always **before** `BearerTokenAuthenticationFilter`
