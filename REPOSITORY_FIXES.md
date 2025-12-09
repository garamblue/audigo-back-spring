# Repository Query Method Fixes

## Issue
Spring Data JPA cannot auto-generate query methods for fields that follow the pattern of lowercase letter followed by uppercase letter (e.g., `mIdx`, `gpIdx`). This causes errors like:
```
Could not resolve attribute 'MIdx' of 'RewardBalanceEntity'
```

## Root Cause
When Spring Data JPA parses method names like `findByMIdx`, it tries to find an attribute named `MIdx` (with capital M) instead of `mIdx` (lowercase m). This is a known limitation with JPA's property naming conventions.

## Solution
Add explicit `@Query` annotations to all repository methods that use fields with this naming pattern.

## Files Modified

### 1. RewardBalanceRepository.java
```java
// Before
Optional<RewardBalanceEntity> findByMIdx(BigInteger mIdx);
boolean existsByMIdx(BigInteger mIdx);

// After
@Query("SELECT rb FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
Optional<RewardBalanceEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

@Query("SELECT CASE WHEN COUNT(rb) > 0 THEN true ELSE false END FROM RewardBalanceEntity rb WHERE rb.mIdx = :mIdx")
boolean existsByMIdx(@Param("mIdx") BigInteger mIdx);
```

### 2. RewardTopupRepository.java
```java
// Added @Query annotations to:
- findByMIdxOrderByTranDtDesc(BigInteger mIdx)
- findByMIdxOrderByTranDtDesc(BigInteger mIdx, Pageable pageable)
- findByMIdxAndCd(BigInteger mIdx, String cd)
```

### 3. RewardExchangeRepository.java
```java
// Added @Query annotations to:
- findByMIdxOrderByTranDtDesc(BigInteger mIdx)
- findByMIdxOrderByTranDtDesc(BigInteger mIdx, Pageable pageable)
```

### 4. RewardAdjustHistoryRepository.java
```java
// Added @Query annotation to:
- findByMIdxOrderByCdtDesc(BigInteger mIdx, Pageable pageable)
```

### 5. GiftExchangeHistoryRepository.java
```java
// Added @Query annotations to:
- findByMIdxOrderByCdtDesc(BigInteger mIdx, Pageable pageable)
- findByMIdxAndStatus(BigInteger mIdx, String status)
```

### 6. GiftProductRepository.java
```java
// Added @Query annotations to:
- findByVisibleAndUseYnOrderByGpIdxDesc(String visible, String useYn)
- findByVisibleAndUseYnOrderByGpIdxDesc(String visible, String useYn, Pageable pageable)
```

## Test Results
After applying these fixes:
- ✅ Build successful
- ✅ Application starts without errors
- ✅ All 22 JPA repositories loaded successfully
- ✅ Tomcat started on port 8081
- ✅ PostgreSQL connection successful

## Lesson Learned
When using Spring Data JPA with fields that have unconventional naming patterns (like `mIdx`), always use explicit `@Query` annotations instead of relying on Spring Data's method name query generation.
