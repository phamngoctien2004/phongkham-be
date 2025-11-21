# TÀI LIỆU API CHI TIẾT - HỆ THỐNG QUẢN LÝ PHÒNG KHÁM

## Thông tin chung
- **Base URL**: `http://localhost:8080`
- **Authentication**: Bearer Token (JWT) - Thêm vào header: `Authorization: Bearer {token}`
- **Content-Type**: `application/json`
- **Tổng số API**: 127 endpoints

---

# 🔐 1. XÁC THỰC (Authentication)

## 1.1. Đăng nhập bệnh nhân
**Endpoint**: `POST /api/auth/login`

**Request Body**:
```json
{
  "username": "0123456789",
  "password": "password123",
  "type": "PASSWORD"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| username | String | ✓ | Số điện thoại hoặc email |
| password | String | ✓ | Mật khẩu |
| type | String | ✓ | Loại đăng nhập: "PASSWORD" hoặc "OTP" |

**Response** (200 OK):
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userResponse": {
      "id": 1,
      "email": "patient@example.com",
      "phone": "0123456789",
      "name": "Nguyen Van A",
      "role": "PATIENT",
      "status": true,
      "createdAt": "2024-01-01T10:00:00",
      "isCreatedPassword": true
    }
  },
  "message": "Login successful"
}
```

---

## 1.2. Đăng nhập Dashboard (Admin/Nhân viên)
**Endpoint**: `POST /api/auth/dashboard/login`

**Request Body**:
```json
{
  "username": "admin@clinic.com",
  "password": "admin123",
  "type": "PASSWORD"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userResponse": {
      "id": 2,
      "email": "admin@clinic.com",
      "phone": "0987654321",
      "name": "Admin User",
      "role": "ADMIN",
      "status": true,
      "doctor": {
        "id": 1,
        "fullName": "Dr. Tran Van B",
        "departmentResponse": {
          "id": 1,
          "name": "Khoa Nội"
        }
      }
    }
  },
  "message": "Login successful"
}
```

---

## 1.3. Đăng ký tài khoản
**Endpoint**: `POST /api/auth/register`

**Request Body**:
```json
{
  "phone": "0123456789",
  "email": "newuser@example.com",
  "name": "Nguyen Van C",
  "birth": "1990-01-01",
  "gender": "MALE",
  "password": "password123",
  "confirmPassword": "password123"
}
```

**Request Fields**:
| Field | Type | Required | Validation | Description |
|-------|------|----------|-----------|-------------|
| phone | String | ✓ | Not blank | Số điện thoại |
| email | String | ✓ | Email format | Địa chỉ email |
| name | String | ✓ | Not blank | Họ và tên |
| birth | Date | ✓ | yyyy-MM-dd | Ngày sinh |
| gender | Enum | ✓ | MALE/FEMALE/OTHER | Giới tính |
| password | String | ✓ | - | Mật khẩu |
| confirmPassword | String | ✓ | Match password | Xác nhận mật khẩu |

**Response** (200 OK):
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userResponse": {
      "id": 3,
      "email": "newuser@example.com",
      "phone": "0123456789",
      "name": "Nguyen Van C",
      "role": "PATIENT"
    }
  },
  "message": "Register successful"
}
```

---

## 1.4. Gửi OTP quên mật khẩu
**Endpoint**: `POST /api/auth/send-otp`

**Request Body**:
```json
{
  "to": "0123456789",
  "message": ""
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Send OTP successful"
}
```

---

## 1.5. Gửi OTP đăng ký
**Endpoint**: `POST /api/auth/register-otp`

**Request Body**:
```json
{
  "to": "0123456789",
  "message": ""
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Send OTP successful"
}
```

---

## 1.6. Xác thực OTP
**Endpoint**: `POST /api/auth/verify-otp`

**Request Body**:
```json
{
  "phone": "0123456789",
  "otp": "123456"
}
```

**Response** (200 OK):
```json
{
  "data": true,
  "message": "Verify OTP successful"
}
```

---

## 1.7. Đặt lại mật khẩu
**Endpoint**: `POST /api/auth/reset-password`

**Request Body**:
```json
{
  "phone": "0123456789",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Reset password successful"
}
```

---

# 👤 2. QUẢN LÝ NGƯỜI DÙNG (User Management)

## 2.1. Lấy danh sách người dùng
**Endpoint**: `GET /api/users`

**Authentication**: Required

**Query Parameters**:
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| keyword | String | ✗ | - | Tìm kiếm theo tên, email, số điện thoại |
| role | Enum | ✗ | - | Lọc theo vai trò: ADMIN, DOCTOR, PATIENT, RECEPTIONIST |
| page | Integer | ✗ | 0 | Số trang |
| size | Integer | ✗ | 10 | Số bản ghi mỗi trang |

**Example Request**:
```
GET /api/users?keyword=nguyen&role=PATIENT&page=0&size=10
```

**Response** (200 OK):
```json
{
  "data": {
    "content": [
      {
        "id": 1,
        "email": "patient@example.com",
        "phone": "0123456789",
        "name": "Nguyen Van A",
        "role": "PATIENT",
        "status": true,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "number": 0,
    "size": 10
  },
  "message": "Fetched all users successfully"
}
```

---

## 2.2. Lấy thông tin người dùng hiện tại
**Endpoint**: `GET /api/users/me`

**Authentication**: Required

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "email": "user@example.com",
    "phone": "0123456789",
    "name": "Nguyen Van A",
    "role": "PATIENT",
    "status": true,
    "createdAt": "2024-01-01T10:00:00",
    "isCreatedPassword": true
  },
  "message": "Get info successfully"
}
```

---

## 2.3. Đổi mật khẩu
**Endpoint**: `POST /api/users/change-password`

**Authentication**: Required

**Request Body**:
```json
{
  "oldPassword": "oldpassword123",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**Response** (204 No Content)

---

# 🩺 3. QUẢN LÝ BÁC SĨ (Doctor Management)

## 3.1. Lấy danh sách bác sĩ
**Endpoint**: `GET /api/doctors`

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "fullName": "Dr. Tran Van B",
      "phone": "0987654321",
      "profileImage": "https://example.com/doctor1.jpg",
      "degreeResponse": {
        "id": 1,
        "name": "Bác sĩ chuyên khoa II"
      },
      "departmentResponse": {
        "id": 1,
        "name": "Khoa Nội"
      },
      "exp": 10,
      "position": "Trưởng khoa",
      "examinationFee": 200000
    }
  ],
  "message": "Fetched all doctors successfully"
}
```

---

## 3.2. Lấy danh sách bác sĩ cho Admin
**Endpoint**: `GET /api/doctors/admin`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | ✗ | Tìm kiếm theo tên |
| departmentId | Integer | ✗ | Lọc theo khoa |
| degreeId | Integer | ✗ | Lọc theo bằng cấp |
| page | Integer | ✗ | Số trang (default: 0) |
| size | Integer | ✗ | Kích thước trang (default: 10) |

**Response** (200 OK):
```json
{
  "data": {
    "content": [...],
    "totalElements": 20,
    "totalPages": 2
  },
  "message": "Fetched all doctors for admin successfully"
}
```

---

# 🏥 4. QUẢN LÝ BỆNH NHÂN (Patient Management)

## 4.1. Tạo bệnh nhân mới
**Endpoint**: `POST /api/patients`

**Authentication**: Required

**Request Body**:
```json
{
  "phone": "0123456789",
  "email": "patient@example.com",
  "fullName": "Nguyen Van D",
  "address": "123 Đường ABC, Quận 1, TP.HCM",
  "cccd": "123456789012",
  "birth": "1990-05-15",
  "gender": "MALE",
  "bloodType": "O+",
  "weight": 70.5,
  "height": 175.0,
  "profileImage": "https://example.com/avatar.jpg"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| phone | String | ✓ | Số điện thoại |
| email | String | ✗ | Email |
| fullName | String | ✓ | Họ và tên đầy đủ |
| address | String | ✗ | Địa chỉ |
| cccd | String | ✓ | Số CCCD/CMND |
| birth | Date | ✓ | Ngày sinh (yyyy-MM-dd) |
| gender | Enum | ✓ | MALE, FEMALE, OTHER |
| bloodType | String | ✗ | Nhóm máu |
| weight | Decimal | ✗ | Cân nặng (kg) |
| height | Decimal | ✗ | Chiều cao (cm) |
| profileImage | String | ✗ | URL ảnh đại diện |

**Response** (200 OK):
```json
{
  "data": {
    "id": 1,
    "code": "BN000001",
    "fullName": "Nguyen Van D",
    "phone": "0123456789",
    "email": "patient@example.com",
    "cccd": "123456789012",
    "birth": "1990-05-15",
    "gender": "MALE",
    "bloodType": "O+",
    "weight": 70.5,
    "height": 175.0,
    "registrationDate": "2024-01-15T09:30:00"
  },
  "message": "Create patient successfully"
}
```

---

## 4.2. Thêm quan hệ người thân
**Endpoint**: `POST /api/patients/relationships`

**Authentication**: Required

**Request Body**:
```json
{
  "phoneLink": "0987654321",
  "relationshipName": "Con trai"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 2,
    "fullName": "Nguyen Van E",
    "relationship": "Con trai",
    "isVerified": false
  },
  "message": "Add relationship successfully"
}
```

---

## 4.3. Xác thực quan hệ người thân
**Endpoint**: `POST /api/patients/relationships/verify`

**Authentication**: Required

**Request Body**:
```json
{
  "phone": "0987654321",
  "otp": "123456"
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Verify relationship successfully"
}
```

---

# 📅 5. ĐẶT LỊCH KHÁM (Appointment Booking)

## 5.1. Đặt lịch khám
**Endpoint**: `POST /api/appointments`

**Authentication**: Required

**Request Body**:
```json
{
  "healthPlanId": 5,
  "doctorId": 3,
  "patientId": 1,
  "date": "2024-02-20",
  "time": "09:00:00",
  "symptoms": "Đau đầu, sốt nhẹ"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| healthPlanId | Integer | ✓ | ID gói khám/dịch vụ |
| doctorId | Integer | ✓ | ID bác sĩ |
| patientId | Integer | ✓ | ID bệnh nhân |
| date | Date | ✓ | Ngày khám (yyyy-MM-dd) |
| time | Time | ✓ | Giờ khám (HH:mm:ss) |
| symptoms | String | ✗ | Triệu chứng |

**Response** (200 OK):
```json
{
  "data": {
    "id": 10,
    "patientResponse": {
      "id": 1,
      "code": "BN000001",
      "fullName": "Nguyen Van D"
    },
    "healthPlanResponse": {
      "id": 5,
      "name": "Khám tổng quát",
      "price": 500000
    },
    "doctorResponse": {
      "id": 3,
      "fullName": "Dr. Tran Van B",
      "departmentResponse": {
        "name": "Khoa Nội"
      }
    },
    "date": "2024-02-20",
    "time": "09:00:00",
    "status": "PENDING",
    "symptoms": "Đau đầu, sốt nhẹ",
    "qr": "data:image/png;base64,iVBORw0KG...",
    "totalAmount": 500000
  },
  "message": "Appointment booked successfully"
}
```

**Appointment Status Enum**:
- `PENDING`: Chờ xác nhận
- `CONFIRMED`: Đã xác nhận
- `COMPLETED`: Đã hoàn thành
- `CANCELLED`: Đã hủy
- `NO_SHOW`: Không đến

---

## 5.2. Lấy danh sách lịch khám
**Endpoint**: `GET /api/appointments`

**Authentication**: Required (RECEPTIONIST/ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| phone | String | ✗ | Số điện thoại bệnh nhân |
| date | Date | ✗ | Ngày khám (yyyy-MM-dd) |
| status | Enum | ✗ | Trạng thái: PENDING, CONFIRMED, COMPLETED, CANCELLED |
| page | Integer | ✗ | Số trang (default: 1) |
| limit | Integer | ✗ | Số bản ghi (default: 10) |

**Example Request**:
```
GET /api/appointments?date=2024-02-20&status=PENDING&page=1&limit=10
```

**Response** (200 OK):
```json
{
  "data": {
    "content": [...],
    "totalElements": 25,
    "totalPages": 3
  },
  "message": "success"
}
```

---

## 5.3. Xác nhận lịch khám
**Endpoint**: `PUT /api/appointments/confirm`

**Authentication**: Required (RECEPTIONIST/ADMIN)

**Request Body**:
```json
{
  "id": 10,
  "status": "CONFIRMED"
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Appointment confirmed successfully"
}
```

---

# 📋 6. QUẢN LÝ HỒ SƠ BỆNH ÁN (Medical Records)

## 6.1. Tạo hồ sơ bệnh án
**Endpoint**: `POST /api/medical-record`

**Authentication**: Required (DOCTOR)

**Request Body**:
```json
{
  "patientId": 1,
  "healthPlanId": 5,
  "doctorId": 3,
  "appointmentId": 10,
  "symptoms": "Đau đầu, sốt nhẹ",
  "clinicalExamination": "Nhiệt độ 38.5°C, huyết áp 120/80",
  "diagnosis": "Cảm cúm",
  "treatmentPlan": "Nghỉ ngơi, uống nhiều nước",
  "note": "Tái khám sau 3 ngày"
}
```

**Request Fields**:
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| patientId | Integer | ✓ | ID bệnh nhân |
| healthPlanId | Integer | ✓ | ID gói khám |
| doctorId | Integer | ✓ | ID bác sĩ |
| appointmentId | Integer | ✗ | ID lịch hẹn |
| symptoms | String | ✗ | Triệu chứng |
| clinicalExamination | String | ✗ | Khám lâm sàng |
| diagnosis | String | ✗ | Chẩn đoán |
| treatmentPlan | String | ✗ | Phương án điều trị |
| note | String | ✗ | Ghi chú |

**Response** (200 OK):
```json
{
  "data": 15,
  "message": "successfully"
}
```

---

## 6.2. Lấy danh sách hồ sơ bệnh án
**Endpoint**: `GET /api/medical-record`

**Authentication**: Required

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | ✗ | Tìm kiếm theo tên, mã BN |
| date | Date | ✗ | Ngày khám (yyyy-MM-dd) |
| status | Enum | ✗ | IN_PROGRESS, COMPLETED, PENDING_PAYMENT, PAID |
| page | Integer | ✗ | Số trang (default: 1) |
| limit | Integer | ✗ | Số bản ghi (default: 10) |

**Response** (200 OK):
```json
{
  "data": {
    "content": [
      {
        "id": 15,
        "code": "HSB000015",
        "patientResponse": {
          "fullName": "Nguyen Van D",
          "code": "BN000001"
        },
        "doctorResponse": {
          "fullName": "Dr. Tran Van B"
        },
        "diagnosis": "Cảm cúm",
        "status": "COMPLETED",
        "createdAt": "2024-02-20T09:30:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10
  },
  "message": "Get all medical record successfully"
}
```

---

# 💊 7. QUẢN LÝ ĐƠN THUỐC (Prescription Management)

## 7.1. Tạo đơn thuốc
**Endpoint**: `POST /api/prescriptions`

**Authentication**: Required (DOCTOR)

**Request Body**:
```json
{
  "medicalRecordId": 15,
  "note": "Uống sau bữa ăn",
  "details": [
    {
      "medicineId": 5,
      "quantity": 20,
      "dosage": "2 viên/lần",
      "frequency": "2 lần/ngày",
      "duration": "10 ngày"
    }
  ]
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 8,
    "medicalRecordId": 15,
    "note": "Uống sau bữa ăn",
    "details": [...]
  },
  "message": "Create prescription successfully"
}
```

---

# 🔬 8. QUẢN LÝ XÉT NGHIỆM (Lab Orders)

## 8.1. Tạo phiếu xét nghiệm
**Endpoint**: `POST /api/lab-orders`

**Authentication**: Required (DOCTOR)

**Request Body**:
```json
{
  "medicalRecordId": 15,
  "healthPlanId": 12,
  "performDoctorId": 7,
  "note": "Xét nghiệm máu tổng quát"
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Create lab order successfully"
}
```

---

## 8.2. Lấy danh sách phiếu xét nghiệm
**Endpoint**: `GET /api/lab-orders/doctor`

**Authentication**: Required (DOCTOR)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | ✗ | Tìm kiếm |
| status | Enum | ✗ | PENDING, PROCESSING, COMPLETED, CANCELLED |
| date | Date | ✗ | Ngày (yyyy-MM-dd) |
| page | Integer | ✗ | Số trang (default: 1) |
| limit | Integer | ✗ | Số bản ghi (default: 10) |

**Lab Test Status Enum**:
- `PENDING`: Chờ thực hiện
- `PROCESSING`: Đang thực hiện
- `COMPLETED`: Đã hoàn thành
- `CANCELLED`: Đã hủy

**Response** (200 OK):
```json
{
  "data": {
    "content": [
      {
        "id": 20,
        "code": "XN000020",
        "patientResponse": {
          "fullName": "Nguyen Van D"
        },
        "healthPlanResponse": {
          "name": "Xét nghiệm máu tổng quát"
        },
        "status": "PENDING",
        "createdAt": "2024-02-20T10:00:00"
      }
    ]
  },
  "message": "Get all lab orders of doctor successfully"
}
```

---

# 💰 9. THANH TOÁN (Payment)

## 9.1. Tạo link thanh toán online
**Endpoint**: `POST /api/payments/create-link`

**Authentication**: Required

**Request Body**:
```json
{
  "medicalRecordId": 15,
  "healthPlanIds": [5, 12],
  "totalAmount": 750000
}
```

**Response** (200 OK):
```json
{
  "data": {
    "checkoutUrl": "https://pay.payos.vn/web/...",
    "orderCode": 123456789
  },
  "message": "Payment link created successfully"
}
```

---

## 9.2. Kiểm tra trạng thái thanh toán
**Endpoint**: `GET /api/payments/status/{orderCode}`

**Example Request**:
```
GET /api/payments/status/123456789
```

**Response** (200 OK):
```json
{
  "data": {
    "orderCode": 123456789,
    "status": "PAID",
    "amount": 750000,
    "transactionDateTime": "2024-02-20T10:30:00"
  },
  "message": "Payment status retrieved successfully"
}
```

---

# 🧾 10. QUẢN LÝ HÓA ĐƠN (Invoice Management)

## 10.1. Thanh toán tiền mặt
**Endpoint**: `POST /api/invoices/pay-cash`

**Authentication**: Required (RECEPTIONIST/ADMIN)

**Request Body**:
```json
{
  "medicalRecordId": 15,
  "totalAmount": 750000
}
```

**Response** (200 OK):
```json
{
  "data": "",
  "message": "Payment successful"
}
```

---

## 10.2. Lấy danh sách hóa đơn
**Endpoint**: `GET /api/invoices`

**Authentication**: Required (RECEPTIONIST/ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| keyword | String | ✗ | Tìm kiếm |
| paymentStatus | Enum | ✗ | PAID, UNPAID, REFUNDED |
| method | Enum | ✗ | CASH, ONLINE |
| fromDate | Date | ✗ | Từ ngày (yyyy-MM-dd) |
| toDate | Date | ✗ | Đến ngày (yyyy-MM-dd) |
| page | Integer | ✗ | Số trang |
| size | Integer | ✗ | Kích thước trang |

**Response** (200 OK):
```json
{
  "data": {
    "content": [
      {
        "id": 25,
        "invoiceCode": "HD000025",
        "patientName": "Nguyen Van D",
        "totalAmount": 750000,
        "paymentStatus": "PAID",
        "paymentMethod": "ONLINE",
        "paymentDate": "2024-02-20T10:30:00"
      }
    ],
    "totalElements": 200
  },
  "message": "Get all medical record successfully"
}
```

---

# 📆 11. QUẢN LÝ LỊCH LÀM VIỆC (Schedule Management)

## 11.1. Lấy lịch trống
**Endpoint**: `GET /api/schedules/available`

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| startDate | Date | ✗ | Ngày bắt đầu (yyyy-MM-dd) |
| endDate | Date | ✗ | Ngày kết thúc (yyyy-MM-dd) |
| departmentId | Integer | ✗ | ID khoa |
| doctorId | Integer | ✗ | ID bác sĩ |
| shift | Enum | ✗ | MORNING, AFTERNOON, NIGHT |

**Shift Enum**:
- `MORNING`: Ca sáng (08:00 - 12:00)
- `AFTERNOON`: Ca chiều (14:00 - 18:00)
- `NIGHT`: Ca tối (18:00 - 22:00)

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 30,
      "doctorResponse": {
        "id": 3,
        "fullName": "Dr. Tran Van B"
      },
      "date": "2024-02-21",
      "shift": "MORNING",
      "startTime": "08:00:00",
      "endTime": "12:00:00",
      "availableSlots": 8
    }
  ],
  "message": "get available slots success"
}
```

---

## 11.2. Tạo lịch làm việc
**Endpoint**: `POST /api/schedules`

**Authentication**: Required (ADMIN)

**Request Body**:
```json
{
  "doctorId": 3,
  "date": "2024-02-25",
  "shift": "MORNING",
  "roomId": 5
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 35,
    "doctorResponse": {...},
    "date": "2024-02-25",
    "shift": "MORNING"
  },
  "message": "create schedule success"
}
```

---

# 🏢 12. QUẢN LÝ KHOA (Department Management)

## 12.1. Lấy danh sách khoa
**Endpoint**: `GET /api/departments`

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 1,
      "code": "NOI",
      "name": "Khoa Nội",
      "description": "Chuyên khám và điều trị các bệnh nội khoa",
      "headDoctorId": 3
    },
    {
      "id": 2,
      "code": "NGOAI",
      "name": "Khoa Ngoại",
      "description": "Chuyên khám và điều trị các bệnh ngoại khoa"
    }
  ]
}
```

---

## 12.2. Lấy danh sách bác sĩ theo khoa
**Endpoint**: `GET /api/departments/{id}/doctors`

**Example Request**:
```
GET /api/departments/1/doctors
```

**Response** (200 OK):
```json
{
  "data": [
    {
      "id": 3,
      "fullName": "Dr. Tran Van B",
      "position": "Trưởng khoa",
      "exp": 10,
      "examinationFee": 200000
    }
  ]
}
```

---

# 🔔 13. QUẢN LÝ THÔNG BÁO (Notification Management)

## 13.1. Lấy danh sách thông báo hệ thống
**Endpoint**: `GET /api/notifications`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| page | Integer | ✗ | Số trang |
| size | Integer | ✗ | Kích thước trang |

**Response** (200 OK):
```json
{
  "data": {
    "content": [
      {
        "id": 5,
        "title": "Thông báo bảo trì hệ thống",
        "content": "Hệ thống sẽ bảo trì vào ngày 25/02/2024",
        "type": "SYSTEM",
        "createdAt": "2024-02-20T08:00:00"
      }
    ]
  },
  "message": "Get notifications successfully"
}
```

---

## 13.2. Tạo thông báo
**Endpoint**: `POST /api/notifications`

**Authentication**: Required (ADMIN)

**Request Body**:
```json
{
  "title": "Thông báo quan trọng",
  "content": "Nội dung thông báo...",
  "type": "SYSTEM"
}
```

**Response** (200 OK):
```json
{
  "data": {
    "id": 6,
    "title": "Thông báo quan trọng",
    "content": "Nội dung thông báo...",
    "type": "SYSTEM"
  },
  "message": "Create notification successfully"
}
```

---

# 📊 14. BÁO CÁO & THỐNG KÊ (Reports & Analytics)

## 14.1. Báo cáo doanh thu
**Endpoint**: `GET /api/reports/revenue`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| fromDate | Date | ✓ | Từ ngày (yyyy-MM-dd) |
| toDate | Date | ✓ | Đến ngày (yyyy-MM-dd) |

**Example Request**:
```
GET /api/reports/revenue?fromDate=2024-01-01&toDate=2024-12-31
```

**Response** (200 OK):
```json
{
  "data": {
    "totalRevenue": 150000000,
    "totalInvoices": 500,
    "cashRevenue": 80000000,
    "onlineRevenue": 70000000,
    "dailyRevenue": [
      {
        "date": "2024-01-01",
        "revenue": 500000,
        "invoices": 5
      }
    ]
  },
  "message": "Get revenue report successfully"
}
```

---

## 14.2. Báo cáo lịch khám
**Endpoint**: `GET /api/reports/appointments`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| fromDate | Date | ✓ | Từ ngày (yyyy-MM-dd) |
| toDate | Date | ✓ | Đến ngày (yyyy-MM-dd) |
| doctorId | Integer | ✗ | ID bác sĩ |
| departmentId | Integer | ✗ | ID khoa |

**Response** (200 OK):
```json
{
  "data": {
    "totalAppointments": 300,
    "completedAppointments": 250,
    "cancelledAppointments": 30,
    "noShowAppointments": 20,
    "byStatus": {
      "COMPLETED": 250,
      "CANCELLED": 30,
      "NO_SHOW": 20
    }
  },
  "message": "Get appointment report successfully"
}
```

---

## 14.3. Báo cáo tổng quan (Dashboard)
**Endpoint**: `GET /api/reports/dashboard`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| fromDate | Date | ✓ | Từ ngày (yyyy-MM-dd) |
| toDate | Date | ✓ | Đến ngày (yyyy-MM-dd) |

**Response** (200 OK):
```json
{
  "data": {
    "revenue": {
      "totalRevenue": 150000000,
      "totalInvoices": 500
    },
    "appointments": {
      "totalAppointments": 300,
      "completedAppointments": 250
    },
    "patients": {
      "totalPatients": 1200,
      "newPatients": 150
    },
    "services": {
      "topServices": [...]
    }
  },
  "message": "Get dashboard report successfully"
}
```

---

## 14.4. Xuất báo cáo PDF
**Endpoint**: `GET /api/reports/export/pdf`

**Authentication**: Required (ADMIN)

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| reportType | String | ✓ | Loại báo cáo: revenue, appointments, patients |
| fromDate | Date | ✓ | Từ ngày (yyyy-MM-dd) |
| toDate | Date | ✓ | Đến ngày (yyyy-MM-dd) |

**Example Request**:
```
GET /api/reports/export/pdf?reportType=revenue&fromDate=2024-01-01&toDate=2024-12-31
```

**Response** (200 OK):
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename=report.pdf`
- Body: Binary PDF data

---

## 14.5. Xuất báo cáo Excel
**Endpoint**: `GET /api/reports/export/excel`

**Authentication**: Required (ADMIN)

**Query Parameters**: Giống như export PDF

**Response** (200 OK):
- Content-Type: `application/octet-stream`
- Content-Disposition: `attachment; filename=report.xlsx`
- Body: Binary Excel data

---

# 📝 PHỤ LỤC

## A. Enum Values

### User.Role
- `ADMIN`: Quản trị viên
- `DOCTOR`: Bác sĩ
- `PATIENT`: Bệnh nhân
- `RECEPTIONIST`: Lễ tân
- `LAB_TECHNICIAN`: Kỹ thuật viên xét nghiệm

### User.Gender
- `MALE`: Nam
- `FEMALE`: Nữ
- `OTHER`: Khác

### Appointment.AppointmentStatus
- `PENDING`: Chờ xác nhận
- `CONFIRMED`: Đã xác nhận
- `COMPLETED`: Đã hoàn thành
- `CANCELLED`: Đã hủy
- `NO_SHOW`: Không đến

### MedicalRecord.RecordStatus
- `IN_PROGRESS`: Đang khám
- `COMPLETED`: Hoàn thành
- `PENDING_PAYMENT`: Chờ thanh toán
- `PAID`: Đã thanh toán

### LabOrder.TestStatus
- `PENDING`: Chờ thực hiện
- `PROCESSING`: Đang thực hiện
- `COMPLETED`: Đã hoàn thành
- `CANCELLED`: Đã hủy

### Invoice.PaymentStatus
- `PAID`: Đã thanh toán
- `UNPAID`: Chưa thanh toán
- `REFUNDED`: Đã hoàn tiền

### Invoice.PaymentMethod
- `CASH`: Tiền mặt
- `ONLINE`: Chuyển khoản/QR

### Schedule.Shift
- `MORNING`: Ca sáng (08:00 - 12:00)
- `AFTERNOON`: Ca chiều (14:00 - 18:00)
- `NIGHT`: Ca tối (18:00 - 22:00)

---

## B. Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email is not valid"
    }
  ]
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-02-20T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## C. Pagination Response Format

Tất cả các API trả về danh sách đều tuân theo format pagination chuẩn:

```json
{
  "data": {
    "content": [...],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "offset": 0
    },
    "totalElements": 100,
    "totalPages": 10,
    "last": false,
    "first": true,
    "size": 10,
    "number": 0,
    "numberOfElements": 10,
    "empty": false
  },
  "message": "Success"
}
```

---

**Ngày cập nhật**: 20/11/2024  
**Phiên bản**: 1.0  
**Tổng số API**: 127 endpoints
