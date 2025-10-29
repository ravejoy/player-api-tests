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
Call `POST /player/create/admin` with valid body (`age > 16`, valid password, unique login/screenName), role = `user`.

**Test reference:**  
`CreatePlayerTests.adminCreatesUser()`

**Group:** `known-issues`

**Recommendation:**  
Fix RBAC logic: allow admin → user creation.
