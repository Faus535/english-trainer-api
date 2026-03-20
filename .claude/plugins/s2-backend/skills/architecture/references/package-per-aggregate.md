# Package Per Aggregate Structure

## Rule: Use package-per-aggregate, NOT package-by-layer

### Convention
Group all code related to a specific aggregate root in the same package. Each aggregate has `application/`, `domain/`, and `infrastructure/` subpackages.

### Benefits
- **Modularity**: Each aggregate is self-contained and independent
- **Navigation**: Easy to find all related code in one place
- **Low coupling**: Reduces dependencies between aggregates
- **Scalability**: Modules can evolve independently

### Examples

#### Good: Package per aggregate
```
com.s2grupo.application
└── user/
    ├── application/
    │   ├── CreateUserUseCase.java
    │   ├── GetUserByIdUseCase.java
    │   └── DeleteUserUseCase.java
    ├── domain/
    │   ├── User.java
    │   ├── UserRepository.java
    │   └── error/
    │       └── UserNotFoundException.java
    └── infrastructure/
        ├── PostgreSqlUserRepository.java
        └── rest/
            ├── CreateUserPostController.java
            └── UserControllerAdvice.java
```

#### Bad: Package by layer
```
com.s2grupo.application
├── controllers/
│   └── UserController.java
├── models/
│   └── User.java
└── services/
    ├── UserService.java
    └── UserServiceImpl.java
```
