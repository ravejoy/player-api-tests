Got it. Here’s an updated **DEFECTS.md** block (English only), adding **VAL-02** and aligning test references with the current class/method names and package structure.

````md
# DEFECT LOG

---

### RBAC-01 Admin creates USER returns 403 instead of 200

**Type:** Business Logic Defect  
**Severity:** Critical  
**Status:** Open

**Summary:**  
Admin should be able to create users with role `user`. API incorrectly returns `403 Forbidden`.

**Observed behavior:**  
When sending a valid request:

- editor = `admin`
- role = `user`

API returns: `403 Forbidden`.

**Expected behavior:**  
Per role model — admin can create users with role `user`.  
Should return: `200 OK` (or `201 Created` if aligned with REST).

**Impact:**

- Admin cannot create regular users
- Violates documented role capabilities
- Blocks standard onboarding flow

**Reproduction:**  
Call `GET /player/create/admin` with valid payload (`age > 16`, valid password, unique login/screenName), role = `user`.

**Test reference:**  
`CreatePlayerTests.adminCreatesUser()`

**Group:** `known-issues`

**Recommendation:**  
Fix RBAC logic: allow admin → user creation.

---

### API-01 Create returns null fields instead of populated contract values

**Type:** API Contract Defect  
**Severity:** Major  
**Status:** Open

**Summary:**  
`/player/create/{editor}` returns `null` for multiple response fields despite Swagger contract defining them as typed & non-null (`string`/`int32`).

**Observed behavior:**  
Example response body:

```json
{
  "id": 374393496,
  "login": "L",
  "password": null,
  "screenName": null,
  "gender": null,
  "age": null,
  "role": null
}
```
````

**Expected behavior:**
Per API schema, fields must return meaningful values:

- `id` = int64
- `login` = string
- `screenName` = string
- `role` = string
- `gender` = string
- `age` = int32

**Impact:**

- Client code cannot rely on API response schema
- Breaks typed integration clients
- Blocks contract-based validation and automation

**Reproduction:**
Call `GET /player/create/supervisor` with valid payload.

**Test reference:**
`CreatePlayerContractTests.supervisorCreatesUserContractStrict`

**Group:** `known-issues`

**Recommendation:**
Populate response DTO based on created entity or input data. Avoid nulls — return typed defaults or actual created values.

---

### VAL-01 Password is required but request succeeds without one

**Type:** Validation Defect
**Severity:** High
**Status:** Open

**Summary:**
API accepts requests without `password` despite task specification stating it is required.

**Observed behavior:**
Request with `password=""` succeeds (`200 OK`).

**Expected behavior:**
Missing or empty password should return:

- `400 Bad Request` (validation failure)
  _(or `422 Unprocessable Entity` if adopting strict REST semantics)_

**Impact:**

- Security risk: weak account creation policy
- Inconsistency between documentation, Swagger, and behavior
- Allows invalid accounts to be created

**Reproduction:**

```
GET /player/create/supervisor?login=X&screenName=Y&role=user&gender=male&age=24&password=
```

**Test reference:**
`CreatePlayerContractTests.supervisorCreatesUserWithEmptyPassword`
`CreatePlayerValidationTests.createPlayerValidationEmptyPassword`

**Group:** `known-issues`

**Recommendation:**
Add server-side validation for required field `password`. Reject null/empty values.

---

### VAL-02 Gender accepts invalid values (no server-side validation)

**Type:** Validation Defect
**Severity:** Medium
**Status:** Open

**Summary:**
`gender` is not validated; arbitrary values (e.g., `foo`) are accepted and the user is created.

**Observed behavior:**
`gender=foo` yields `200 OK`.

**Expected behavior:**
`400 Bad Request` with a clear validation error. Allowed values should be restricted (enum/whitelist).

**Impact:**

- Dirty data in the system
- Divergence between UI and API
- Complicates analytics and downstream processing

**Reproduction:**

```
GET /player/create/supervisor?login=X&screenName=Y&role=user&gender=foo&age=24&password=Z
```

**Test reference:**
`CreatePlayerValidationTests.createPlayerValidationInvalidGender`

**Group:** `known-issues`

**Recommendation:**
Validate `gender` against an allowed set; reflect allowed values explicitly in Swagger.

---

### DES-01 GET /player/create changes state (violates REST safety)

**Type:** Design / REST Semantics  
**Severity:** Major  
**Status:** Open

**Summary:**  
Resource creation is exposed via **GET** endpoint `/player/create/{editor}`. GET must be safe (no state change). Current implementation creates a player and returns 200.

**Observed behavior:**  
Calling `GET /player/create/supervisor?login=...&screenName=...&role=user&gender=male&age=24&password=...` increases the number of players.

**Expected behavior:**  
Creation must be performed via `POST /player` (or `/player/create`) returning `201 Created` with `Location` header.

**Impact:**

- Violates HTTP semantics (safe/idempotent methods)
- Breaks caches, proxies, and crawlers safety assumptions
- Makes monitoring/auditing harder

**Reproduction:**  
Compare `/player/get/all` count before vs after a GET create call.

**Test reference:**  
`CreatePlayerRestDesignTests.getCreateViolatesSafety`

**Recommendation:**  
Move creation to `POST` and make current GET endpoint return `405 Method Not Allowed` (or remove).
