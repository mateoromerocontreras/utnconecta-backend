# Test Structure Documentation

This project contains three types of tests:

## 1. **Unit Tests** (Pure Unit Tests)
**Location**: `src/test/java/com/seminario/pasantias/service/*Test.java`

**Characteristics**:
- ✅ No Spring context loaded
- ✅ All dependencies are mocked using `@Mock`
- ✅ Fast execution
- ✅ Test individual methods in isolation
- ✅ Use `@ExtendWith(MockitoExtension.class)`

**Example**: `CarreraServiceTest.java`

**Purpose**: Test business logic without any external dependencies.

---

## 2. **Integration Tests** (Service Integration Tests)
**Location**: `src/test/java/com/seminario/pasantias/service/integration/*IntegrationTest.java`

**Characteristics**:
- ✅ Full Spring context loaded (`@SpringBootTest`)
- ✅ Real H2 in-memory database
- ✅ Real MyBatis mappers (no mocking)
- ✅ Test service + persistence layer integration
- ✅ Use `@Transactional` for test isolation
- ✅ Use `@ActiveProfiles("test")`

**Example**: `CarreraServiceIntegrationTest.java`

**Purpose**: Test the integration between service layer and persistence layer with real database operations.

---

## 3. **Controller Integration Tests** (HTTP Layer Tests)
**Location**: `src/test/java/com/seminario/pasantias/controller/*ControllerTest.java`

**Characteristics**:
- ✅ Full Spring context loaded (`@SpringBootTest`)
- ✅ Real H2 in-memory database
- ✅ Mock services with `@MockBean`
- ✅ Test HTTP endpoints with MockMvc
- ✅ Test request/response handling
- ✅ Use `@AutoConfigureMockMvc`

**Example**: `AuthControllerTest.java`, `EmpresaControllerTest.java`, etc.

**Purpose**: Test the HTTP layer, request validation, response formatting, and controller logic.

---

## Test Execution

### Run All Tests
```bash
mvn test
```

### Run Only Unit Tests
```bash
mvn test -Dtest="*ServiceTest"
```

### Run Only Integration Tests
```bash
mvn test -Dtest="*IntegrationTest"
```

### Run Only Controller Tests
```bash
mvn test -Dtest="*ControllerTest"
```

### Run Specific Test Class
```bash
mvn test -Dtest=CarreraServiceTest
```

---

## Test Coverage

### Current Coverage:

#### Unit Tests:
- ✅ `CarreraServiceTest` - Unit tests for CarreraService

#### Integration Tests:
- ✅ `CarreraServiceIntegrationTest` - Integration tests for CarreraService

#### Controller Integration Tests:
- ✅ `AuthControllerTest` - 14 tests
- ✅ `EmpresaControllerTest` - 14 tests
- ✅ `CarreraControllerTest` - 13 tests
- ✅ `CvControllerTest` - 9 tests
- ✅ `EstudianteControllerTest` - 14 tests
- ✅ `PasantiaControllerTest` - 12 tests
- ✅ `PostulacionControllerTest` - 14 tests
- ✅ `RolControllerTest` - 15 tests
- ✅ `UsuarioControllerTest` - 19 tests

**Total: 124+ tests**

---

## Test Configuration

### H2 Database Configuration
Tests use H2 in-memory database configured in `src/test/resources/application-test.properties`:
- MySQL compatibility mode
- In-memory database (no file persistence)
- Auto-created schema

### Test Profile
All integration tests use `@ActiveProfiles("test")` to load test-specific configuration.

---

## Best Practices

1. **Unit Tests**: Should be fast, isolated, and test single methods
2. **Integration Tests**: Should test real database operations and transactions
3. **Controller Tests**: Should test HTTP layer, validation, and error handling
4. **Test Isolation**: Use `@Transactional` in integration tests to rollback changes
5. **Test Data**: Clean up test data in `@BeforeEach` or `@AfterEach` methods

---

## Adding New Tests

### To add a Unit Test:
1. Create `*ServiceTest.java` in `src/test/java/com/seminario/pasantias/service/`
2. Use `@ExtendWith(MockitoExtension.class)`
3. Mock all dependencies with `@Mock`
4. Inject service with `@InjectMocks`

### To add an Integration Test:
1. Create `*ServiceIntegrationTest.java` in `src/test/java/com/seminario/pasantias/service/integration/`
2. Use `@SpringBootTest` and `@ActiveProfiles("test")`
3. Use `@Autowired` to inject real services
4. Use `@Transactional` for test isolation

### To add a Controller Test:
1. Create `*ControllerTest.java` in `src/test/java/com/seminario/pasantias/controller/`
2. Use `@SpringBootTest`, `@AutoConfigureMockMvc`, and `@ActiveProfiles("test")`
3. Mock services with `@MockBean`
4. Use `MockMvc` to test HTTP endpoints

