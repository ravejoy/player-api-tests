# DEFECT LOG

## Index

| ID      | Title                                         | Type                  | Status |
| ------- | --------------------------------------------- | --------------------- | ------ |
| RBAC-01 | Admin cannot create USER                      | Business Logic Defect | Open   |
| API-01  | Null fields in create response                | API Contract Defect   | Open   |
| VAL-01  | Password not validated                        | Validation Defect     | Open   |
| VAL-02  | Invalid gender accepted                       | Validation Defect     | Open   |
| DES-01  | GET creates resource (REST violation)         | Design Defect         | Open   |
| API-02  | Wrong codes for invalid/not-found playerId    | API Contract Defect   | Open   |
| DES-02  | `/player/get` uses POST instead of GET        | Design Defect         | Open   |
| DES-03  | Missing cache headers for GET resource        | Design Defect         | Open   |
| VAL-03  | Duplicate login overwrites existing user      | Data Integrity Defect | Open   |
| RBAC-02 | User can delete players (should be forbidden) | Business Logic Defect | Open   |
| API-03  | Duplicate login overwrites existing user      | Data Integrity Defect | Open   |
| RBAC-03 | Admin cannot update USER                      | Business Logic Defect | Open   |
| VAL-04  | Update accepts invalid ages                   | Validation Defect     | Open   |

---

### RBAC-01 Admin creates USER returns 403 instead of 200

**Type:** Business Logic Defect  
**Severity:** Critical  
**Status:** Open

**Summary:** Admin should be able to create users with role `user`. API incorrectly returns `403 Forbidden`.

**Expected:** 200 OK  
**Observed:** 403 Forbidden

**Test:** `CreatePlayerTests.adminCreatesUser()`

---

### API-01 Create returns null fields instead of populated contract values

**Type:** API Contract Defect  
**Severity:** Major  
**Status:** Open

**Summary:** Response DTO contains `null` fields despite contract defining them as non-null.

**Test:** `CreatePlayerContractTests.supervisorCreatesUserContractStrict`

---

### VAL-01 Password is required but request succeeds without one

**Type:** Validation Defect  
**Severity:** High  
**Status:** Open

**Summary:** Missing/empty password is accepted.

**Test:**

- `CreatePlayerContractTests.supervisorCreatesUserWithEmptyPassword`
- `CreatePlayerValidationTests.createPlayerValidationEmptyPassword`

---

### VAL-02 Gender accepts invalid values (no validation)

**Type:** Validation Defect  
**Severity:** Medium  
**Status:** Open

**Summary:** Arbitrary gender values are accepted instead of validated.

**Test:** `CreatePlayerValidationTests.createPlayerValidationInvalidGender`

---

### DES-01 GET /player/create changes state (REST violation)

**Type:** Design Defect  
**Severity:** Major  
**Status:** Open

**Summary:** GET endpoint creates a resource; should be POST.

**Test:** `CreatePlayerRestDesignTests.getCreateViolatesSafety`

---

### API-02 Wrong status codes for invalid/not‑found playerId

**Type:** API Contract Defect  
**Severity:** Major  
**Status:** Open

**Summary:** `POST /player/get` returns 200 instead of 400/404 for invalid playerId.

**Tests:**

- `GetPlayerByIdTests.getByIdBadRequest`
- `GetPlayerByIdTests.getByIdNotFound`

---

### DES-02 `/player/get` uses POST instead of GET (REST violation)

**Type:** Design Defect
**Severity:** Major
**Status:** Open

**Summary:**
`/player/get` is implemented as `POST`, but retrieving a resource must use `GET`.

**Expected:**
`GET /player/{id}` or `GET /player/get?playerId=` should return the player.

**Observed:**

- `POST /player/get` returns data
- `GET /player/get?playerId=` is not supported (or returns unexpected success)

**Impact:**

- Violates REST semantics
- Prevents caching, browser support, proxies, CDNs
- Harder client implementation

**Test:**
`GetPlayerDesignTests.getByIdDesignSemantics`

---

### DES-03 No cache headers for read-only GET resource

**Type:** Design Defect
**Severity:** Minor
**Status:** Open

**Summary:**
`/player/get` responses do not return caching directives.
Read-only idempotent resource retrieval should include `Cache-Control`.

**Expected:**
Response should include:

```
Cache-Control: public, max-age=...
```

(or similar caching policy)

**Observed:**
No `Cache-Control` header on successful fetch.

**Impact:**

- Cannot leverage browser/network caching
- Increased latency and server load

**Test:**
`GetPlayerDesignTests.getByIdDesignSemantics`

---

### VAL-03 Duplicate login overwrites existing user (data loss)

**Type:** Data Integrity / Validation Defect
**Severity:** Critical
**Status:** Open

**Summary:**
`/player/create/{editor}` accepts a duplicate `login` and **overwrites** the existing user instead of rejecting the request.

**Expected:**

- Reject duplicate login
- Status: `400 Bad Request` or `409 Conflict`
- Existing user remains unchanged

**Observed:**

- Second request with same login succeeds
- Existing user is silently overwritten
- Data loss occurs

**Impact:**

- Violates uniqueness rule
- Causes account takeover / identity corruption
- Breaks business logic and security model

**Test:**
`CreatePlayerValidationTests.duplicateLoginShouldBeRejected`

---

### RBAC-02 User is able to delete players (should be forbidden)

**Type:** Business Logic Defect
**Severity:** Critical
**Status:** Open

**Summary:**
Requests with `editor=user` can delete players (both `role=user` and `role=admin`). According to the role model, a **user must not be allowed to delete**.

**Expected:** `403 Forbidden`
**Observed:** `204 No Content` (entity actually removed)

**Reproduction (examples):**

- Create a player (any role), then call
  `DELETE /player/delete/user` with body `{ "playerId": <createdId> }` → **204**.
- Same result for target roles `user` and `admin`.

**Impact:**

- Regular users can remove other accounts → data loss, privilege escalation.

**Tests:**
`DeletePlayerRbacTest.deleteIsForbiddenFor(USER, USER)`
`DeletePlayerRbacTest.deleteIsForbiddenFor(USER, ADMIN)`

**Recommendation:**
Enforce RBAC check for delete: deny when `editor=user` (regardless of target role).

---

### API-03 Wrong status codes on DELETE

**Type:** API Contract Defect
**Severity:** Major
**Status:** Open

**Summary:** `/player/delete/{editor}` returns 200/empty for invalid (0, -1) or unknown ids instead of 400/404.

**Expected:**

- 400 Bad Request for invalid ids (0, negative)
- 404 Not Found for unknown ids

**Observed:** 200 OK with empty body.

**Tests:**

- DeletePlayerContractTests.deleteBadRequest
- DeletePlayerContractTests.deleteUnknownId

---

### RBAC-03 Admin cannot update USER (403 instead of 200)

**Type:** Business Logic Defect  
**Severity:** Critical  
**Status:** Open

**Summary:**  
Admin should be able to update users with role `user`, but API incorrectly returns `403 Forbidden`.

**Expected behavior:**  
Per role model rules, `admin` can manage users with roles:

- `user`
- `admin` (if it's himself)

Updating a `user` should return `200 OK` (or `204 No Content`).

**Actual behavior:**  
Calling `PATCH /player/update/admin/{id}` to update a regular user returns:

- `403 Forbidden`

**Impact:**

- Admin cannot modify subordinate users
- Violates documented role hierarchy
- Blocks normal admin operations and UI flows

**Reproduction:**

1. Create a USER as supervisor
2. Attempt to update them as admin

Example request:

```

PATCH /player/update/admin/{userId}
Content-Type: application/json

{
"login": "upd_xxx",
"screenName": "upd_xxx",
"age": 25,
"gender": "male",
"password": "qwerty12"
}

```

**Expected:** `200 OK`  
**Actual:** `403 Forbidden`

**Test reference:**  
`UpdatePlayerRbacTest.adminShouldUpdateUser_butForbidden`

**Group:** `known-issues`

**Recommendation:**  
Fix RBAC logic to allow admin → user updates.

---

---

### VAL-04 Update accepts invalid ages (returns 200 instead of 400)

**Type:** Validation Defect  
**Severity:** High  
**Status:** Open

**Summary:**  
`PATCH /player/update/{editor}/{id}` does not validate `age` boundaries. Requests with age values such as 15, 60, 0, or negative numbers are accepted and return 200 OK.

**Expected behavior:**  
Per baseline rules (16–59 inclusive), invalid ages must be rejected with `400 Bad Request`.

**Observed behavior:**  
Requests with invalid ages return `200 OK` and apply changes.

**Impact:**

- Violates age business rules established for create/update
- Leads to inconsistent user data

**Reproduction:**

1. Create a USER
2. `PATCH /player/update/supervisor/{id}` with body `{ "age": 15 }` (or 60, 0, -1)

**Expected:** 400 Bad Request  
**Actual:** 200 OK

**Test reference:**  
`UpdatePlayerValidationTest.invalidAgesAreRejected`

**Group:** `known-issues`

**Recommendation:**  
Add server-side validation for `age` range (e.g., 16 ≤ age ≤ 59) consistent with create.
