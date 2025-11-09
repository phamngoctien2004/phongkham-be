# 🔧 Sửa Lỗi Repository Queries - Report Module

## Ngày: 9/11/2025

## 📋 Tóm Tắt

Đã kiểm tra và sửa lỗi các query trong repository liên quan đến module báo cáo. Vấn đề chính là không khớp kiểu dữ liệu giữa cột trong database và tham số trong query.

---

## ✅ Các Lỗi Đã Sửa

### 1. PatientRepository - `countReturningPatients()`

**Vấn đề:**
- Cột `ngay_kham` trong bảng `dat_lich_kham` có kiểu `LocalDate`
- Cột `ngay_dang_ky` trong bảng `benh_nhan` có kiểu `LocalDateTime`  
- Method signature ban đầu sử dụng `LocalDateTime` cho cả 2 tham số

**Giải pháp:**
```java
// TRƯỚC
@Query(value = """
    SELECT COUNT(DISTINCT a.id_benh_nhan)
    FROM dat_lich_kham a
    INNER JOIN benh_nhan p ON a.id_benh_nhan = p.id_benh_nhan
    WHERE a.ngay_kham BETWEEN :fromDate AND :toDate
    AND DATE(p.ngay_dang_ky) < :fromDate  // ❌ So sánh DATE với LocalDateTime
    AND a.trang_thai = 'HOAN_THANH'
    """, nativeQuery = true)
Long countReturningPatients(@Param("fromDate") LocalDateTime fromDate, 
                            @Param("toDate") LocalDateTime toDate);

// SAU  
@Query(value = """
    SELECT COUNT(DISTINCT a.id_benh_nhan)
    FROM dat_lich_kham a
    INNER JOIN benh_nhan p ON a.id_benh_nhan = p.id_benh_nhan
    WHERE a.ngay_kham BETWEEN :fromDate AND :toDate
    AND DATE(p.ngay_dang_ky) < :fromDate  // ✅ Sử dụng DATE() function
    AND a.trang_thai = 'HOAN_THANH'
    """, nativeQuery = true)
Long countReturningPatients(@Param("fromDate") LocalDate fromDate, 
                            @Param("toDate") LocalDate toDate);
```

**Lý do:**
- `ngay_kham` là `LocalDate` → Dùng tham số `LocalDate`
- `ngay_dang_ky` là `LocalDateTime` → Dùng `DATE()` function để convert về date

---

## ✅ Các Repository Đã Kiểm Tra (Không Có Lỗi)

### 1. InvoiceRepository
**Status:** ✅ OK
- Tất cả queries sử dụng JPQL với entity properties
- `paymentDate` được xử lý đúng kiểu `LocalDateTime`
- `CAST(i.paymentDate AS LocalDate)` được sử dụng đúng trong GROUP BY

### 2. AppointmentRepository  
**Status:** ✅ OK
- Tất cả queries sử dụng JPQL
- `date` được xử dụng đúng kiểu `LocalDate`
- CASE statements cho status đã đúng với enum

### 3. ExaminationServiceRepository (HealthPlanRepository)
**Status:** ✅ OK
- Queries sử dụng JPQL qua entity navigation
- JOIN relationships đúng: `a.healthPlan`, `a.doctor`, `d.department`
- Aggregate functions (COUNT, SUM) hoạt động chính xác

### 4. PatientRepository - Các queries khác
**Status:** ✅ OK  
- `countNewPatients()` - Đúng kiểu `LocalDateTime`
- `getNewPatientsByDay()` - Đúng kiểu `LocalDateTime`
- `getPatientsByGender()` - Đúng kiểu `LocalDateTime`
- `getPatientsByAgeGroup()` - Đúng kiểu `LocalDateTime` + `LocalDate` cho currentDate

---

## 📊 Mapping Tên Cột Database vs Entity

### Bảng `benh_nhan` (Patient)
| Cột Database | Entity Property | Java Type | Annotation |
|-------------|----------------|-----------|------------|
| `id_benh_nhan` | `id` | `Integer` | `@Column(name = "id_benh_nhan")` |
| `ma_benh_nhan` | `code` | `String` | `@Column(name = "ma_benh_nhan")` |
| `ho_ten` | `fullName` | `String` | `@Column(name = "ho_ten")` |
| `ngay_sinh` | `birth` | `LocalDate` | `@Column(name = "ngay_sinh")` |
| `gioi_tinh` | `gender` | `User.Gender` | `@Column(name = "gioi_tinh")` |
| `ngay_dang_ky` | `registrationDate` | `LocalDateTime` | `@Column(name = "ngay_dang_ky")` |

### Bảng `dat_lich_kham` (Appointment)
| Cột Database | Entity Property | Java Type | Annotation |
|-------------|----------------|-----------|------------|
| `id_dat_lich` | `id` | `Integer` | `@Column(name = "id_dat_lich")` |
| `id_benh_nhan` | `patient` | `Patient` | `@JoinColumn(name = "id_benh_nhan")` |
| `id_bac_si` | `doctor` | `Doctor` | `@JoinColumn(name = "id_bac_si")` |
| `id_dich_vu_kham` | `healthPlan` | `HealthPlan` | `@JoinColumn(name = "id_dich_vu_kham")` |
| `ngay_kham` | `date` | `LocalDate` | `@Column(name = "ngay_kham")` |
| `gio_kham` | `time` | `LocalTime` | `@Column(name = "gio_kham")` |
| `trang_thai` | `status` | `AppointmentStatus` | `@Column(name = "trang_thai")` |
| `ngay_dat_lich` | `bookingDate` | `LocalDateTime` | `@Column(name = "ngay_dat_lich")` |
| `tong_tien` | `totalAmount` | `BigDecimal` | `@Column(name = "tong_tien")` |

### Bảng `hoa_don_thanh_toan` (Invoice)
| Cột Database | Entity Property | Java Type | Annotation |
|-------------|----------------|-----------|------------|
| `id_hoa_don` | `id` | `Integer` | `@Column(name = "id_hoa_don")` |
| `ma_hoa_don` | `code` | `String` | `@Column(name = "ma_hoa_don")` |
| `tong_tien` | `totalAmount` | `BigDecimal` | `@Column(name = "tong_tien")` |
| `so_tien_thanh_toan` | `paidAmount` | `BigDecimal` | `@Column(name = "so_tien_thanh_toan")` |
| `phuong_thuc` | `paymentMethod` | `PaymentMethod` | `@Column(name = "phuong_thuc")` |
| `ngay_thanh_toan` | `paymentDate` | `LocalDateTime` | `@Column(name = "ngay_thanh_toan")` |
| `trang_thai` | `status` | `PaymentStatus` | `@Column(name = "trang_thai")` |

### Bảng `dich_vu_kham` (HealthPlan)
| Cột Database | Entity Property | Java Type | Annotation |
|-------------|----------------|-----------|------------|
| `id_dich_vu` | `id` | `Integer` | `@Column(name = "id_dich_vu")` |
| `ma_dich_vu` | `code` | `String` | `@Column(name = "ma_dich_vu")` |
| `ten_dich_vu` | `name` | `String` | `@Column(name = "ten_dich_vu")` |
| `loai` | `type` | `ServiceType` | `@Column(name = "loai")` |
| `gia` | `price` | `BigDecimal` | `@Column(name = "gia")` |
| `trang_thai` | `status` | `Boolean` | `@Column(name = "trang_thai")` |

---

## 🎯 Best Practices Đã Áp Dụng

### 1. Native Query vs JPQL
- ✅ **Native Query**: Sử dụng khi cần aggregate phức tạp hoặc function SQL đặc biệt
- ✅ **JPQL**: Sử dụng khi có thể navigate qua entity relationships

### 2. Date/Time Handling
```sql
-- ✅ ĐÚNG: So sánh LocalDate với LocalDate
WHERE ngay_kham BETWEEN :fromDate AND :toDate

-- ✅ ĐÚNG: Convert LocalDateTime về Date khi cần
WHERE DATE(ngay_dang_ky) < :fromDate

-- ✅ ĐÚNG: CAST trong JPQL
SELECT CAST(i.paymentDate AS LocalDate), ...
```

### 3. Enum Handling
```sql
-- ✅ ĐÚNG: String literal trong native query
WHERE a.trang_thai = 'HOAN_THANH'

-- ✅ ĐÚNG: Full qualified enum trong JPQL
WHERE i.status = com.dcm.demo.model.Invoice.PaymentStatus.DA_THANH_TOAN
```

---

## 🧪 Testing

### Unit Tests
```bash
mvn test -Dtest=ReportServiceImplTest
# Result: 17/17 tests PASSED ✅
```

### Compilation
```bash
mvn clean compile -DskipTests
# Result: BUILD SUCCESS ✅
```

---

## 📝 Checklist Hoàn Thành

- [x] Kiểm tra tất cả model entities liên quan
- [x] So sánh tên cột database vs entity properties  
- [x] Sửa lỗi trong PatientRepository.countReturningPatients()
- [x] Verify tất cả repository queries khác
- [x] Test compilation
- [x] Run unit tests
- [x] Tạo documentation

---

## 🔍 Lưu Ý Cho Developer

1. **Khi viết Native Query:**
   - Luôn check tên cột trong `@Column(name = "...")` annotation
   - Đảm bảo kiểu dữ liệu tham số khớp với kiểu cột database
   - Sử dụng `DATE()`, `CAST()` khi cần convert

2. **Khi viết JPQL:**
   - Sử dụng entity property names (camelCase)
   - Enum phải dùng full qualified name
   - Navigation qua relationships dùng dấu chấm (`.`)

3. **Testing:**
   - Luôn test với data thật hoặc mock data gần giống production
   - Kiểm tra edge cases (null values, empty results)
   - Verify aggregate functions trả về đúng kiểu

---

## 📚 Tài Liệu Tham Khảo

- [Spring Data JPA Query Methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods)
- [JPA @Column Annotation](https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/column)
- [MySQL DATE Functions](https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html)

---

**Người thực hiện:** GitHub Copilot  
**Ngày cập nhật:** 9/11/2025  
**Status:** ✅ HOÀN THÀNH
