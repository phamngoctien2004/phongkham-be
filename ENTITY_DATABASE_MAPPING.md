# TÀI LIỆU ĐỐI CHIẾU ENTITY VÀ DATABASE SCHEMA

## 📋 TỔNG QUAN
Tài liệu này đối chiếu các Entity classes (Java) đã tạo với các bảng trong database schema gốc (MySQL).

---

## 🏥 1. QUẢN LÝ NGƯỜI DÙNG

### 1.1 User Entity ↔ `nguoi_dung` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `userId` | `id_nguoi_dung` | INT (PK) | @Id @GeneratedValue |
| `email` | `email` | VARCHAR(100) | @Column(unique=true) |
| `password` | `mat_khau` | VARCHAR(255) | @Column(nullable=false) |
| `fullName` | `ho_ten` | VARCHAR(255) | @Column(nullable=false) |
| `phoneNumber` | `sdt` | VARCHAR(20) | @Column |
| `address` | `dia_chi` | TEXT | @Column(columnDefinition="TEXT") |
| `citizenId` | `CCCD` | TEXT | @Column(columnDefinition="TEXT") |
| `dateOfBirth` | `ngay_sinh` | DATE | @Column |
| `gender` | `gioi_tinh` | ENUM('NAM','NU') | @Enumerated(EnumType.STRING) |
| `role` | `vai_tro` | ENUM | @Enumerated(EnumType.STRING) |
| `profileImage` | `anh_nguoi_dung` | VARCHAR(500) | @Column |
| `status` | `trang_thai` | BOOLEAN | @Column (default=true) |
| `createdDate` | `ngay_tao` | DATETIME | @CreationTimestamp |

**Enums:**
- `Gender`: NAM, NU
- `Role`: BENH_NHAN, BAC_SI, LE_TAN, ADMIN

---

## 🏥 2. QUẢN LÝ KHOA & PHÒNG KHÁM

### 2.1 Department Entity ↔ `khoa_bac_si` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `departmentId` | `id_khoa` | INT (PK) | @Id @GeneratedValue |
| `departmentName` | `ten_khoa` | VARCHAR(255) | @Column(nullable=false) |
| `phoneNumber` | `sdt` | VARCHAR(15) | @Column(nullable=false) |
| `description` | `mo_ta` | TEXT | @Column(columnDefinition="TEXT") |
| `status` | `trang_thai` | BOOLEAN | @Column (default=true) |

### 2.2 ExaminationRoom Entity ↔ `phong_kham` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `roomId` | `id_phong_kham` | INT (PK) | @Id @GeneratedValue |
| `roomName` | `ten_phong_kham` | VARCHAR(255) | @Column(nullable=false) |
| `roomNumber` | `so_phong` | VARCHAR(10) | @Column(nullable=false) |
| `department` | `id_khoa` | INT (FK) | @ManyToOne → Department |

---

## 👨‍⚕️ 3. QUẢN LÝ BÁC SĨ

### 3.1 DegreeType Entity ↔ `loai_bang_cap` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `degreeId` | `id_bang_cap` | INT (PK) | @Id @GeneratedValue |
| `degreeName` | `ten_bang_cap` | VARCHAR(255) | @Column(nullable=false) |
| `examinationFee` | `phi_kham` | DECIMAL(12,0) | @Column(nullable=false) |

### 3.2 Doctor Entity ↔ `bac_si` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `doctorId` | `id_bac_si` | INT (PK) | @Id @GeneratedValue |
| `user` | `id_nguoi_dung` | INT (FK) | @OneToOne → User |
| `department` | `id_khoa` | INT (FK) | @ManyToOne → Department |
| `degreeType` | `id_bang_cap` | INT (FK) | @ManyToOne → DegreeType |
| `yearsOfExperience` | `so_nam_kinh_nghiem` | INT | @Column |
| `position` | `chuc_danh` | VARCHAR(255) | @Column |
| `status` | `trang_thai` | BOOLEAN | @Column (default=true) |

### 3.3 Leave Entity ↔ `nghi_phep` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `leaveId` | `id_nghi_phep` | INT (PK) | @Id @GeneratedValue |
| `doctor` | `id_bac_si` | INT (FK) | @ManyToOne → Doctor |
| `startDate` | `batdau_nghi` | DATE | @Column |
| `endDate` | `ketthuc_nghi` | DATE | @Column |
| `reason` | `ly_do_nghi` | VARCHAR(255) | @Column |
| `leaveStatus` | `trang_thai_nghi` | TINYINT | @Column (0: Pending, 1: Approved, 2: Rejected) |
| `submitDate` | `ngay_gui` | DATE | @Column |
| `approver` | `nguoi_duyet` | INT (FK) | @ManyToOne → User |

### 3.4 DoctorWorkSchedule Entity ↔ `lich_lam_viec_bac_si` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `scheduleId` | `id_lich` | INT (PK) | @Id @GeneratedValue |
| `doctor` | `id_bac_si` | INT (FK) | @ManyToOne → Doctor |
| `dayOfWeek` | `thu_trong_tuan` | ENUM | @Enumerated(EnumType.STRING) |
| `startTime` | `khung_gio_bat_dau` | TIME | @Column(nullable=false) |
| `endTime` | `khung_gio_ket_thuc` | TIME | @Column(nullable=false) |

**Enum:** `DayOfWeek`: T2, T3, T4, T5, T6, T7, CN

---

## 🏥 4. QUẢN LÝ BỆNH NHÂN

### 4.1 Patient Entity ↔ `benh_nhan` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `patientId` | `id_benh_nhan` | INT (PK) | @Id @GeneratedValue |
| `user` | `id_nguoi_dung` | INT (FK) | @OneToOne → User |
| `patientCode` | `ma_benh_nhan` | VARCHAR(20) | @Column(unique=true) |
| `bloodType` | `nhom_mau` | VARCHAR(10) | @Column |
| `height` | `chieu_cao` | DECIMAL(5,2) | @Column |
| `weight` | `can_nang` | DECIMAL(5,2) | @Column |
| `registrationDate` | `ngay_dang_ky` | DATETIME | @CreationTimestamp |

---

## 🩺 5. QUẢN LÝ DỊCH VỤ & ĐẶT LỊCH

### 5.1 MedicalService Entity ↔ `dich_vu_kham` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `serviceId` | `id_dich_vu` | INT (PK) | @Id @GeneratedValue |
| `serviceCode` | `ma_dich_vu` | VARCHAR(50) | @Column(unique=true) |
| `serviceName` | `ten_dich_vu` | VARCHAR(255) | @Column(nullable=false) |
| `type` | `loai` | ENUM | @Enumerated(EnumType.STRING) |
| `price` | `gia` | DECIMAL(15,2) | @Column (default=0) |
| `description` | `mo_ta` | TEXT | @Column(columnDefinition="TEXT") |
| `status` | `trang_thai` | BOOLEAN | @Column (default=true) |

**Enum:** `ServiceType`: KHAM, XET_NGHIEM, CHUAN_DOAN_HINH_ANH, KHAC

### 5.2 Appointment Entity ↔ `dat_lich_kham` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `appointmentId` | `id_dat_lich` | INT (PK) | @Id @GeneratedValue |
| `doctor` | `id_bac_si` | INT (FK) | @ManyToOne → Doctor |
| `examinationRoom` | `id_phong_kham` | INT (FK) | @ManyToOne → ExaminationRoom |
| `fullName` | `ho_ten` | VARCHAR(255) | @Column(nullable=false) |
| `phoneNumber` | `sdt` | CHAR(15) | @Column(nullable=false) |
| `address` | `dia_chi` | VARCHAR(255) | @Column(nullable=false) |
| `email` | `email` | VARCHAR(50) | @Column(nullable=false) |
| `dateOfBirth` | `ngay_sinh` | DATE | @Column |
| `appointmentDate` | `ngay_kham` | DATE | @Column(nullable=false) |
| `appointmentTime` | `gio_kham` | TIME | @Column(nullable=false) |
| `status` | `trang_thai` | ENUM | @Enumerated(EnumType.STRING) |
| `symptoms` | `trieu_chung` | TEXT | @Column(columnDefinition="TEXT") |
| `bookingDate` | `ngay_dat_lich` | DATETIME | @CreationTimestamp |
| `confirmedBy` | `nguoi_xac_nhan` | INT (FK) | @ManyToOne → User |

**Enum:** `AppointmentStatus`: CHO_XAC_NHAN, DA_XAC_NHAN, HOAN_THANH, KHONG_DEN

---

## 📋 6. QUẢN LÝ KHÁM BỆNH

### 6.1 MedicalRecord Entity ↔ `phieu_kham_benh` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `recordId` | `id_phieu_kham` | INT (PK) | @Id @GeneratedValue |
| `patient` | `id_benh_nhan` | INT (FK) | @ManyToOne → Patient |
| `appointment` | `id_dat_lich` | INT (FK) | @ManyToOne → Appointment |
| `doctor` | `id_bac_si` | INT (FK) | @ManyToOne → Doctor |
| `recordCode` | `ma_phieu_kham` | VARCHAR(20) | @Column(unique=true) |
| `examinationDate` | `ngay_kham` | DATETIME | @CreationTimestamp |
| `mainSymptoms` | `trieu_chung_chinh` | TEXT | @Column(columnDefinition="TEXT") |
| `clinicalExamination` | `kham_lam_sang` | TEXT | @Column(columnDefinition="TEXT") |
| `diagnosis` | `chan_doan` | TEXT | @Column(columnDefinition="TEXT") |
| `treatmentPlan` | `ke_hoach_dieu_tri` | TEXT | @Column(columnDefinition="TEXT") |
| `doctorNotes` | `ghi_chu_bac_si` | TEXT | @Column(columnDefinition="TEXT") |
| `examinationFee` | `phi_kham` | DECIMAL(15,2) | @Column (default=0) |
| `totalCost` | `tong_chi_phi` | DECIMAL(15,2) | @Column (default=0) |
| `status` | `trang_thai` | ENUM | @Enumerated(EnumType.STRING) |

**Enum:** `RecordStatus`: DANG_KHAM, CHO_XET_NGHIEM, HOAN_THANH, HUY

---

## 🧪 7. QUẢN LÝ XÉT NGHIỆM

### 7.1 LabTestOrder Entity ↔ `chi_dinh_xet_nghiem` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `orderId` | `id_chi_dinh` | INT (PK) | @Id @GeneratedValue |
| `medicalRecord` | `id_phieu_kham` | INT (FK) | @ManyToOne → MedicalRecord |
| `medicalService` | `id_dich_vu` | INT (FK) | @ManyToOne → MedicalService |
| `orderingDoctor` | `id_bac_si_chi_dinh` | INT (FK) | @ManyToOne → Doctor |
| `status` | `trang_thai` | ENUM | @Enumerated(EnumType.STRING) |
| `price` | `gia` | DECIMAL(15,2) | @Column (default=0) |
| `orderDate` | `ngay_chi_dinh` | DATETIME | @CreationTimestamp |
| `expectedResultDate` | `ngay_hen_tra_ket_qua` | DATETIME | @Column |
| `queueTime` | `thoi_gian_vao_hang` | DATETIME | @CreationTimestamp |

**Enum:** `TestStatus`: CHO_THUC_HIEN, DANG_THUC_HIEN, HOAN_THANH, HUY_BO

### 7.2 LabTestResult Entity ↔ `ket_qua_xet_nghiem` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `resultId` | `id_ket_qua` | INT (PK) | @Id @GeneratedValue |
| `labTestOrder` | `id_chi_dinh` | INT (FK) | @OneToOne → LabTestOrder |
| `performingDoctor` | `id_bac_si_thuc_hien` | INT (FK) | @ManyToOne → Doctor |
| `performedDate` | `ngay_thuc_hien` | DATETIME | @Column |
| `resultDetails` | `ket_qua_chi_tiet` | TEXT | @Column(columnDefinition="TEXT") |
| `resultFile` | `file_ket_qua` | VARCHAR(500) | @Column |
| `status` | `trang_thai` | ENUM | @Enumerated(EnumType.STRING) |

**Enum:** `ResultStatus`: HOAN_THANH, CAN_KIEM_TRA_LAI

---

## 💊 8. QUẢN LÝ THUỐC & ĐƠN THUỐC

### 8.1 Medicine Entity ↔ `thuoc` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `medicineId` | `id_thuoc` | INT (PK) | @Id @GeneratedValue |
| `medicineName` | `ten_thuoc` | VARCHAR(255) | @Column(nullable=false) |
| `concentration` | `ham_luong` | VARCHAR(100) | @Column |
| `dosageForm` | `dang_bao_che` | VARCHAR(100) | @Column |

### 8.2 Prescription Entity ↔ `don_thuoc` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `prescriptionId` | `id_don_thuoc` | INT (PK) | @Id @GeneratedValue |
| `medicalRecord` | `id_phieu_kham` | INT (FK) | @ManyToOne → MedicalRecord |
| `doctor` | `id_bac_si` | INT (FK) | @ManyToOne → Doctor |
| `prescriptionCode` | `ma_don_thuoc` | VARCHAR(20) | @Column(unique=true) |
| `prescriptionDate` | `ngay_ke_don` | DATETIME | @CreationTimestamp |
| `generalInstructions` | `huong_dan_chung` | TEXT | @Column(columnDefinition="TEXT") |

### 8.3 PrescriptionDetail Entity ↔ `chi_tiet_don_thuoc` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `detailId` | `id_chi_tiet` | INT (PK) | @Id @GeneratedValue |
| `prescription` | `id_don_thuoc` | INT (FK) | @ManyToOne → Prescription |
| `medicine` | `id_thuoc` | INT (FK) | @ManyToOne → Medicine |
| `quantity` | `so_luong` | INT | @Column |
| `usageInstructions` | `huong_dan_su_dung` | TEXT | @Column(columnDefinition="TEXT") |

---

## 💰 9. QUẢN LÝ THANH TOÁN

### 9.1 Invoice Entity ↔ `hoa_don_thanh_toan` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `invoiceId` | `id_hoa_don` | INT (PK) | @Id @GeneratedValue |
| `patient` | `id_benh_nhan` | INT (FK) | @ManyToOne → Patient |
| `medicalRecord` | `id_phieu_kham` | INT (FK) | @ManyToOne → MedicalRecord |
| `invoiceCode` | `ma_hoa_don` | VARCHAR(50) | @Column(unique=true) |
| `totalAmount` | `tong_tien` | DECIMAL(15,2) | @Column(nullable=false) |
| `paidAmount` | `so_tien_thanh_toan` | DECIMAL(15,2) | @Column(nullable=false) |
| `paymentMethod` | `phuong_thuc` | ENUM | @Enumerated(EnumType.STRING) |
| `paymentDate` | `ngay_thanh_toan` | DATETIME | @CreationTimestamp |
| `cashier` | `nguoi_thu_tien` | INT (FK) | @ManyToOne → User |
| `status` | `trang_thai` | ENUM | @Enumerated(EnumType.STRING) |
| `notes` | `ghi_chu` | TEXT | @Column(columnDefinition="TEXT") |

**Enums:**
- `PaymentMethod`: TIEN_MAT, THE_ATM, CHUYEN_KHOAN, QR_CODE
- `PaymentStatus`: CHUA_THANH_TOAN, HOAN_THANH

---

## 🌐 10. QUẢN LÝ CMS & WEBSITE

### 10.1 Blog Entity ↔ `blog` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `blogId` | `id_blog` | INT (PK) | @Id @GeneratedValue |
| `title` | `tieu_de` | VARCHAR(255) | @Column(nullable=false) |
| `content` | `noi_dung` | TEXT | @Column(columnDefinition="TEXT") |
| `publishDate` | `ngay_dang` | DATE | @Column |
| `author` | `tac_gia` | VARCHAR(255) | @Column |
| `image` | `hinh_anh` | VARCHAR(255) | @Column |
| `status` | `trang_thai` | VARCHAR(50) | @Column |

### 10.2 Banner Entity ↔ `banner` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `bannerId` | `id_banner` | INT (PK) | @Id @GeneratedValue |
| `title` | `tieu_de` | VARCHAR(255) | @Column(nullable=false) |
| `image` | `hinh_anh` | VARCHAR(255) | @Column(nullable=false) |
| `link` | `lien_ket` | VARCHAR(255) | @Column |
| `startDate` | `ngay_bat_dau` | DATE | @Column |
| `endDate` | `ngay_ket_thuc` | DATE | @Column |
| `status` | `trang_thai` | VARCHAR(50) | @Column |

---

## 💬 11. QUẢN LÝ CHAT & ĐÁNH GIÁ

### 11.1 ChatSession Entity ↔ `chat_session` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `chatSessionId` | `id_chat_session` | INT (PK) | @Id @GeneratedValue |
| `admin` | `id_admin` | INT (FK) | @ManyToOne → User |
| `patient` | `id_benh_nhan` | INT (FK) | @ManyToOne → Patient |
| `startDate` | `ngay_bat_dau` | DATETIME | @CreationTimestamp |
| `endDate` | `ngay_ket_thuc` | DATETIME | @Column |

### 11.2 ChatMessage Entity ↔ `chat_message` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `chatMessageId` | `id_chat_message` | INT (PK) | @Id @GeneratedValue |
| `chatSession` | `id_chat_session` | INT (FK) | @ManyToOne → ChatSession |
| `sender` | `id_nguoi_gui` | INT (FK) | @ManyToOne → User |
| `receiver` | `id_nguoi_nhan` | INT (FK) | @ManyToOne → User |
| `content` | `noi_dung` | TEXT | @Column(nullable=false, columnDefinition="TEXT") |
| `sentTime` | `thoi_gian_gui` | DATETIME | @CreationTimestamp |

### 11.3 Rating Entity ↔ `danh_gia` Table
| **Entity Field** | **Database Column** | **Type** | **Mapping** |
|-----------------|-------------------|----------|-------------|
| `ratingId` | `id_danh_gia` | INT (PK) | @Id @GeneratedValue |
| `user` | `id_nguoi_dung` | INT (FK) | @ManyToOne → User |
| `ratingScore` | `diem_danh_gia` | INT | @Column (1-5 stars) |
| `comment` | `nhan_xet` | TEXT | @Column(columnDefinition="TEXT") |

---

## 📊 THỐNG KÊ TỔNG QUAN

### Số lượng Entity/Table đã mapping:
- **Tổng số bảng trong DB:** 22 bảng
- **Tổng số Entity đã tạo:** 22 entity classes
- **Tỷ lệ mapping:** 100% ✅

### Các tính năng đã implement:
- ✅ Quản lý người dùng & phân quyền
- ✅ Quản lý khoa & phòng khám
- ✅ Quản lý bác sĩ & lịch làm việc
- ✅ Quản lý bệnh nhân
- ✅ Đặt lịch khám
- ✅ Khám bệnh & hồ sơ y tế
- ✅ Xét nghiệm
- ✅ Kê đơn thuốc
- ✅ Thanh toán & hoá đơn
- ✅ CMS (Blog, Banner)
- ✅ Chat & đánh giá

### Annotation patterns sử dụng:
- `@Entity`, `@Table` cho mapping cơ bản
- `@Id`, `@GeneratedValue` cho primary key
- `@Column` cho field mapping với constraints
- `@ManyToOne`, `@OneToOne`, `@OneToMany` cho relationships
- `@Enumerated(EnumType.STRING)` cho enum fields
- `@CreationTimestamp` cho auto timestamp
- `@JoinColumn` cho foreign key mapping

---

**📝 Ghi chú:** Tất cả entity classes đã được tạo với tên class và field bằng tiếng Anh, đồng thời giữ nguyên tên cột database bằng tiếng Việt thông qua annotation `@Column(name="...")` để đảm bảo tương thích hoàn toàn với schema hiện tại.
