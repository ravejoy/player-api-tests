# DEFECT LOG

## Index

| ID      | Title                                      | Type                  | Status |
| ------- | ------------------------------------------ | --------------------- | ------ |
| RBAC-01 | Admin cannot create USER                   | Business Logic Defect | Open   |
| API-01  | Null fields in create response             | API Contract Defect   | Open   |
| VAL-01  | Password not validated                     | Validation Defect     | Open   |
| VAL-02  | Invalid gender accepted                    | Validation Defect     | Open   |
| DES-01  | GET creates resource (REST violation)      | Design Defect         | Open   |
| API-02  | Wrong codes for invalid/not-found playerId | API Contract Defect   | Open   |
| DES-02  | `/player/get` uses POST instead of GET     | Design Defect         | Open   |
| DES-03  | Missing cache headers for GET resource     | Design Defect         | Open   |

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
