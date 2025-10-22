# API Documentation - Hệ thống Quản lý Phòng khám
ok
## Mục lục
1. [Authentication API](#1-authentication-api)
2. [Appointment API](#2-appointment-api)
3. [Department API](#3-department-api)
4. [Doctor API](#4-doctor-api)
5. [Patient API](#5-patient-api)
6. [Health Plan (Services) API](#6-health-plan-services-api)
7. [Medical Record API](#7-medical-record-api)
8. [Prescription API](#8-prescription-api)
9. [Medicine API](#9-medicine-api)
10. [Lab Order API](#10-lab-order-api)
11. [Lab Result API](#11-lab-result-api)
12. [Invoice API](#12-invoice-api)
13. [Payment API](#13-payment-api)
14. [Schedule API](#14-schedule-api)
15. [Receptionist API](#15-receptionist-api)
16. [User API](#16-user-api)
17. [HTML Export API](#17-html-export-api)

---

## 1. Authentication API

### Base URL: `/api/auth`

### 1.1 Đăng nhập (Login)
**Endpoint:** `POST /api/auth/login`

**Mô tả:** Đăng nhập cho bệnh nhân

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "type": "PASSWORD" // hoặc "OTP"
}
```

**Response:**
```json
{
  "data": {
    "token": "string",
    "userInfo": {}
  },
  "message": "Login successful"
}
```

---

### 1.2 Đăng nhập Dashboard
**Endpoint:** `POST /api/auth/dashboard/login`

**Mô tả:** Đăng nhập cho nhân viên y tế (bác sĩ, lễ tân, v.v.)

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "type": "PASSWORD"
}
```

**Response:**
```json
{
  "data": {
    "token": "string",
    "userInfo": {}
  },
  "message": "Login successful"
}
```

---

### 1.3 Gửi OTP
**Endpoint:** `POST /api/auth/send-otp`

**Mô tả:** Gửi mã OTP để đăng nhập hoặc xác thực

**Request Body:**
```json
{
  "phone": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Send OTP successful"
}
```

---

### 1.4 Gửi OTP đăng ký
**Endpoint:** `POST /api/auth/register-otp`

**Mô tả:** Gửi mã OTP cho quá trình đăng ký tài khoản mới

**Request Body:**
```json
{
  "phone": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Send OTP successful"
}
```

---

### 1.5 Xác thực OTP
**Endpoint:** `POST /api/auth/verify-otp`

**Mô tả:** Xác thực mã OTP

**Request Body:**
```json
{
  "phone": "string",
  "otp": "string"
}
```

**Response:**
```json
{
  "data": true,
  "message": "Verify OTP successful"
}
```

---

### 1.6 Đăng ký tài khoản
**Endpoint:** `POST /api/auth/register`

**Mô tả:** Đăng ký tài khoản bệnh nhân mới

**Request Body:**
```json
{
  "phone": "string (required)",
  "email": "string (email format)",
  "name": "string (required)",
  "birth": "2000-01-01",
  "gender": "MALE", // MALE, FEMALE, OTHER
  "password": "string (min 6 chars)",
  "confirmPassword": "string"
}
```

**Response:**
```json
{
  "data": {
    "token": "string",
    "userInfo": {}
  },
  "message": "Register successful"
}
```

---

## 2. Appointment API

### Base URL: `/api/appointments`

### 2.1 Đặt lịch hẹn
**Endpoint:** `POST /api/appointments`

**Mô tả:** Tạo lịch hẹn khám bệnh mới

**Request Body:**
```json
{
  "healthPlanId": 1,
  "doctorId": 1,
  "departmentId": 1,
  "patientId": 1,
  "fullName": "string",
  "phone": "string",
  "gender": "MALE",
  "birth": "2000-01-01",
  "email": "string",
  "address": "string",
  "date": "2024-12-20",
  "time": "09:00:00",
  "symptoms": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Appointment booked successfully"
}
```

---

### 2.2 Lấy danh sách lịch hẹn
**Endpoint:** `GET /api/appointments`

**Mô tả:** Lấy danh sách lịch hẹn theo bộ lọc

**Query Parameters:**
- `phone` (optional): Số điện thoại để tìm kiếm
- `date` (optional): Ngày khám (format: yyyy-MM-dd)
- `status` (optional): Trạng thái (PENDING, CONFIRMED, CANCELLED, COMPLETED)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "healthPlanName": "string",
      "doctorName": "string",
      "departmentName": "string",
      "patientName": "string",
      "phone": "string",
      "date": "2024-12-20",
      "time": "09:00:00",
      "status": "PENDING"
    }
  ],
  "message": "success"
}
```

---

### 2.3 Xác nhận/Thay đổi trạng thái lịch hẹn
**Endpoint:** `PUT /api/appointments/confirm`

**Mô tả:** Xác nhận hoặc thay đổi trạng thái lịch hẹn

**Request Body:**
```json
{
  "id": 1,
  "status": "CONFIRMED" // PENDING, CONFIRMED, CANCELLED, COMPLETED
}
```

**Response:**
```json
{
  "data": "",
  "message": "Appointment confirmed successfully"
}
```

---

## 3. Department API

### Base URL: `/api/departments`

### 3.1 Lấy danh sách khoa
**Endpoint:** `GET /api/departments`

**Mô tả:** Lấy tất cả các khoa trong bệnh viện

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Khoa Nội",
      "description": "string"
    }
  ],
  "message": "success"
}
```

---

### 3.2 Lấy danh sách bác sĩ theo khoa
**Endpoint:** `GET /api/departments/{id}/doctors`

**Mô tả:** Lấy danh sách bác sĩ thuộc một khoa cụ thể

**Path Parameters:**
- `id`: ID của khoa

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Dr. Nguyễn Văn A",
      "specialization": "string",
      "phone": "string",
      "email": "string"
    }
  ],
  "message": "success"
}
```

---

## 4. Doctor API

### Base URL: `/api/doctors`

### 4.1 Lấy danh sách bác sĩ
**Endpoint:** `GET /api/doctors`

**Mô tả:** Lấy danh sách tất cả bác sĩ

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Dr. Nguyễn Văn A",
      "specialization": "string",
      "departmentName": "string",
      "phone": "string",
      "email": "string"
    }
  ],
  "message": "Fetched all doctors successfully"
}
```

---

### 4.2 Lấy thông tin bác sĩ hiện tại
**Endpoint:** `GET /api/doctors/me`

**Mô tả:** Lấy thông tin của bác sĩ đang đăng nhập

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "Dr. Nguyễn Văn A",
    "specialization": "string",
    "departmentName": "string",
    "phone": "string",
    "email": "string"
  },
  "message": "Fetched my info successfully"
}
```

---

## 5. Patient API

### Base URL: `/api/patients`

### 5.1 Tạo bệnh nhân mới
**Endpoint:** `POST /api/patients`

**Mô tả:** Tạo hồ sơ bệnh nhân mới

**Request Body:**
```json
{
  "name": "string",
  "phone": "string",
  "email": "string",
  "gender": "MALE",
  "birth": "2000-01-01",
  "address": "string",
  "cccd": "string",
  "bhyt": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "string",
    "phone": "string"
  },
  "message": "Create patient successfully"
}
```

---

### 5.2 Tìm kiếm bệnh nhân
**Endpoint:** `GET /api/patients`

**Mô tả:** Tìm kiếm bệnh nhân theo từ khóa

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm (tên, số điện thoại, CCCD)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "string",
      "phone": "string",
      "email": "string",
      "gender": "MALE",
      "birth": "2000-01-01"
    }
  ],
  "message": "Find patient successfully"
}
```

---

### 5.3 Lấy thông tin bệnh nhân theo ID
**Endpoint:** `GET /api/patients/{id}`

**Mô tả:** Lấy chi tiết thông tin bệnh nhân

**Path Parameters:**
- `id`: ID của bệnh nhân

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "string",
    "phone": "string",
    "email": "string",
    "gender": "MALE",
    "birth": "2000-01-01",
    "address": "string",
    "cccd": "string",
    "bhyt": "string"
  },
  "message": "Find patient successfully"
}
```

---

### 5.4 Cập nhật thông tin bệnh nhân
**Endpoint:** `PUT /api/patients`

**Mô tả:** Cập nhật thông tin bệnh nhân

**Request Body:**
```json
{
  "id": 1,
  "name": "string",
  "phone": "string",
  "email": "string",
  "gender": "MALE",
  "birth": "2000-01-01",
  "address": "string",
  "cccd": "string",
  "bhyt": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "string"
  },
  "message": "Update patient successfully"
}
```

---

### 5.5 Lấy thông tin bệnh nhân hiện tại
**Endpoint:** `GET /api/patients/me`

**Mô tả:** Lấy thông tin bệnh nhân đang đăng nhập

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "string",
    "phone": "string",
    "email": "string"
  },
  "message": "Get my patient successfully"
}
```

---

### 5.6 Lấy danh sách quan hệ bệnh nhân
**Endpoint:** `GET /api/patients/relationships`

**Mô tả:** Lấy danh sách các bệnh nhân liên quan (gia đình)

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "string",
      "phone": "string",
      "relationship": "string"
    }
  ],
  "message": "Get all patients successfully"
}
```

---

### 5.7 Thêm quan hệ bệnh nhân
**Endpoint:** `POST /api/patients/relationships`

**Mô tả:** Thêm bệnh nhân vào danh sách quan hệ (yêu cầu xác thực OTP)

**Request Body:**
```json
{
  "cccd": "string",
  "relationship": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Add relationship successfully"
}
```

---

### 5.8 Xác thực quan hệ bệnh nhân
**Endpoint:** `POST /api/patients/relationships/verify`

**Mô tả:** Xác thực OTP để hoàn tất thêm quan hệ

**Request Body:**
```json
{
  "phone": "string",
  "otp": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Verify relationship successfully"
}
```

---

### 5.9 Xóa quan hệ bệnh nhân
**Endpoint:** `DELETE /api/patients/relationships/{patientId}`

**Mô tả:** Xóa bệnh nhân khỏi danh sách quan hệ

**Path Parameters:**
- `patientId`: ID của bệnh nhân cần xóa

**Response:** 
- Status: 204 No Content

---

## 6. Health Plan (Services) API

### Base URL: `/api/services`

### 6.1 Lấy danh sách dịch vụ
**Endpoint:** `GET /api/services`

**Mô tả:** Lấy danh sách các gói khám/dịch vụ

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Khám tổng quát",
      "description": "string",
      "price": 500000,
      "duration": 30
    }
  ],
  "message": "success"
}
```

---

### 6.2 Lấy chi tiết dịch vụ
**Endpoint:** `GET /api/services/{id}`

**Mô tả:** Lấy thông tin chi tiết của một dịch vụ

**Path Parameters:**
- `id`: ID của dịch vụ

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "Khám tổng quát",
    "description": "string",
    "price": 500000,
    "duration": 30,
    "details": []
  },
  "message": "success"
}
```

---

## 7. Medical Record API

### Base URL: `/api/medical-record`

### 7.1 Lấy danh sách hồ sơ bệnh án
**Endpoint:** `GET /api/medical-record`

**Mô tả:** Lấy danh sách hồ sơ bệnh án với bộ lọc

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm
- `date` (optional): Ngày khám (yyyy-MM-dd)
- `status` (optional): Trạng thái (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "recordCode": "MR001",
      "patientName": "string",
      "doctorName": "string",
      "date": "2024-12-20",
      "status": "PENDING"
    }
  ],
  "message": "Get all medical record successfully"
}
```

---

### 7.2 Lấy chi tiết hồ sơ bệnh án
**Endpoint:** `GET /api/medical-record/{id}`

**Mô tả:** Lấy thông tin chi tiết của hồ sơ bệnh án

**Path Parameters:**
- `id`: ID của hồ sơ bệnh án

**Response:**
```json
{
  "data": {
    "id": 1,
    "recordCode": "MR001",
    "patient": {},
    "doctor": {},
    "symptoms": "string",
    "clinicalExamination": "string",
    "diagnosis": "string",
    "treatmentPlan": "string",
    "prescriptions": [],
    "labOrders": [],
    "invoice": {},
    "status": "PENDING"
  },
  "message": "Get medical record by id successfully"
}
```

---

### 7.3 Lấy hồ sơ bệnh án theo bệnh nhân
**Endpoint:** `GET /api/medical-record/patient/{id}`

**Mô tả:** Lấy tất cả hồ sơ bệnh án của một bệnh nhân

**Path Parameters:**
- `id`: ID của bệnh nhân

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "recordCode": "MR001",
      "date": "2024-12-20",
      "doctorName": "string",
      "diagnosis": "string"
    }
  ],
  "message": "Get medical record by id successfully"
}
```

---

### 7.4 Lấy hồ sơ của tôi
**Endpoint:** `GET /api/medical-record/me`

**Mô tả:** Lấy hồ sơ bệnh án của bệnh nhân đang đăng nhập

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "recordCode": "MR001",
      "date": "2024-12-20",
      "doctorName": "string",
      "status": "COMPLETED"
    }
  ],
  "message": "Get my medical record successfully"
}
```

---

### 7.5 Lấy hồ sơ người thân theo CCCD
**Endpoint:** `GET /api/medical-record/me/{cccd}`

**Mô tả:** Lấy hồ sơ bệnh án của người thân theo CCCD

**Path Parameters:**
- `cccd`: Số CCCD của người thân

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "recordCode": "MR001",
      "date": "2024-12-20",
      "doctorName": "string"
    }
  ],
  "message": "Get medical record by cccd successfully"
}
```

---

### 7.6 Tạo hồ sơ bệnh án
**Endpoint:** `POST /api/medical-record`

**Mô tả:** Tạo hồ sơ bệnh án mới

**Request Body:**
```json
{
  "patientId": 1,
  "healthPlanId": 1,
  "doctorId": 1,
  "symptoms": "string",
  "clinicalExamination": "string",
  "diagnosis": "string",
  "treatmentPlan": "string",
  "note": "string"
}
```

**Response:**
```json
{
  "data": 1,
  "message": "successfully"
}
```

---

### 7.7 Cập nhật hồ sơ bệnh án
**Endpoint:** `PUT /api/medical-record`

**Mô tả:** Cập nhật thông tin hồ sơ bệnh án

**Request Body:**
```json
{
  "id": 1,
  "symptoms": "string",
  "clinicalExamination": "string",
  "diagnosis": "string",
  "treatmentPlan": "string",
  "note": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "successfully"
}
```

---

### 7.8 Cập nhật trạng thái hồ sơ
**Endpoint:** `PUT /api/medical-record/status`

**Mô tả:** Cập nhật trạng thái hồ sơ bệnh án

**Request Body:**
```json
{
  "id": 1,
  "status": "COMPLETED" // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
}
```

**Response:**
```json
{
  "data": "",
  "message": "successfully"
}
```

---

## 8. Prescription API

### Base URL: `/api/prescriptions`

### 8.1 Lấy đơn thuốc theo hồ sơ bệnh án
**Endpoint:** `GET /api/prescriptions/medical-record/{id}`

**Mô tả:** Lấy các đơn thuốc của một hồ sơ bệnh án

**Path Parameters:**
- `id`: ID của hồ sơ bệnh án

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "note": "string",
      "createdDate": "2024-12-20",
      "details": [
        {
          "id": 1,
          "medicineName": "string",
          "quantity": 10,
          "dosage": "1 viên/lần",
          "frequency": "2 lần/ngày",
          "duration": "7 ngày"
        }
      ]
    }
  ],
  "message": "Fetch prescriptions successfully"
}
```

---

### 8.2 Tạo đơn thuốc
**Endpoint:** `POST /api/prescriptions`

**Mô tả:** Tạo đơn thuốc mới

**Request Body:**
```json
{
  "medicalRecordId": 1,
  "note": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "medicalRecordId": 1
  },
  "message": "Create prescription successfully"
}
```

---

### 8.3 Cập nhật đơn thuốc
**Endpoint:** `PUT /api/prescriptions`

**Mô tả:** Cập nhật thông tin đơn thuốc

**Request Body:**
```json
{
  "id": 1,
  "note": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1
  },
  "message": "Update prescription successfully"
}
```

---

### 8.4 Thêm chi tiết đơn thuốc
**Endpoint:** `POST /api/prescriptions/details`

**Mô tả:** Thêm thuốc vào đơn thuốc

**Request Body:**
```json
{
  "prescriptionId": 1,
  "medicineId": 1,
  "quantity": 10,
  "dosage": "1 viên/lần",
  "frequency": "2 lần/ngày",
  "duration": "7 ngày",
  "note": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1
  },
  "message": "Create prescription detail successfully"
}
```

---

### 8.5 Cập nhật chi tiết đơn thuốc
**Endpoint:** `PUT /api/prescriptions/details`

**Mô tả:** Cập nhật thông tin thuốc trong đơn

**Request Body:**
```json
{
  "id": 1,
  "quantity": 10,
  "dosage": "1 viên/lần",
  "frequency": "2 lần/ngày",
  "duration": "7 ngày",
  "note": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1
  },
  "message": "Update prescription detail successfully"
}
```

---

### 8.6 Xóa chi tiết đơn thuốc
**Endpoint:** `DELETE /api/prescriptions/details/{id}`

**Mô tả:** Xóa thuốc khỏi đơn thuốc

**Path Parameters:**
- `id`: ID của chi tiết đơn thuốc

**Response:**
- Status: 204 No Content

---

## 9. Medicine API

### Base URL: `/api/medicines`

### 9.1 Lấy danh sách thuốc
**Endpoint:** `GET /api/medicines`

**Mô tả:** Lấy danh sách thuốc có thể tìm kiếm

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm tên thuốc

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Paracetamol",
      "unit": "Viên",
      "price": 5000,
      "stock": 1000,
      "description": "string"
    }
  ],
  "message": "Fetch medicines successfully"
}
```

---

## 10. Lab Order API

### Base URL: `/api/lab-orders`

### 10.1 Lấy phiếu xét nghiệm theo mã hồ sơ
**Endpoint:** `GET /api/lab-orders/code/{code}`

**Mô tả:** Lấy các phiếu xét nghiệm theo mã hồ sơ bệnh án

**Path Parameters:**
- `code`: Mã hồ sơ bệnh án

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "testName": "string",
      "status": "PENDING",
      "orderDate": "2024-12-20"
    }
  ],
  "message": "Get lab order by record code successfully"
}
```

---

### 10.2 Lấy chi tiết phiếu xét nghiệm
**Endpoint:** `GET /api/lab-orders/{id}`

**Mô tả:** Lấy thông tin chi tiết phiếu xét nghiệm

**Path Parameters:**
- `id`: ID của phiếu xét nghiệm

**Response:**
```json
{
  "data": {
    "id": 1,
    "testName": "string",
    "status": "PENDING",
    "orderDate": "2024-12-20",
    "result": {},
    "doctorPerforming": {}
  },
  "message": "Get lab order by id successfully"
}
```

---

### 10.3 Lấy tất cả phiếu xét nghiệm
**Endpoint:** `GET /api/lab-orders`

**Mô tả:** Lấy danh sách tất cả phiếu xét nghiệm

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "testName": "string",
      "patientName": "string",
      "status": "PENDING"
    }
  ],
  "message": "Get all lab orders successfully"
}
```

---

### 10.4 Lấy phiếu xét nghiệm của bác sĩ
**Endpoint:** `GET /api/lab-orders/doctor/me`

**Mô tả:** Lấy các phiếu xét nghiệm được phân cho bác sĩ đang đăng nhập

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm
- `status` (optional): Trạng thái (PENDING, IN_PROGRESS, COMPLETED)
- `date` (optional): Ngày (yyyy-MM-dd)

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "testName": "string",
      "patientName": "string",
      "status": "PENDING",
      "orderDate": "2024-12-20"
    }
  ],
  "message": "Get all lab orders of doctor successfully"
}
```

---

### 10.5 Tạo phiếu xét nghiệm
**Endpoint:** `POST /api/lab-orders`

**Mô tả:** Tạo phiếu xét nghiệm mới

**Request Body:**
```json
{
  "medicalRecordId": 1,
  "testId": 1,
  "doctorPerformingId": 1,
  "note": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Create lab order successfully"
}
```

---

### 10.6 Cập nhật trạng thái phiếu xét nghiệm
**Endpoint:** `PUT /api/lab-orders/status`

**Mô tả:** Cập nhật trạng thái phiếu xét nghiệm

**Request Body:**
```json
{
  "id": 1,
  "status": "COMPLETED" // PENDING, IN_PROGRESS, COMPLETED
}
```

**Response:**
```json
{
  "data": "",
  "message": "Update lab order status successfully"
}
```

---

### 10.7 Cập nhật phiếu xét nghiệm
**Endpoint:** `PUT /api/lab-orders`

**Mô tả:** Cập nhật thông tin phiếu xét nghiệm

**Request Body:**
```json
{
  "id": 1,
  "doctorPerformingId": 1,
  "note": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Update lab order successfully"
}
```

---

### 10.8 Xóa phiếu xét nghiệm
**Endpoint:** `DELETE /api/lab-orders`

**Mô tả:** Xóa một hoặc nhiều phiếu xét nghiệm

**Request Body:**
```json
{
  "ids": [1, 2, 3]
}
```

**Response:**
- Status: 204 No Content

---

## 11. Lab Result API

### Base URL: `/api/lab-results`

### 11.1 Tạo kết quả xét nghiệm
**Endpoint:** `POST /api/lab-results`

**Mô tả:** Tạo/cập nhật kết quả xét nghiệm

**Request Body:**
```json
{
  "labOrderId": 1,
  "result": "string",
  "note": "string",
  "conclusion": "string"
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "labOrderId": 1
  },
  "message": "Create lab result successfully"
}
```

---

## 12. Invoice API

### Base URL: `/api/invoices`

### 12.1 Thanh toán tiền mặt
**Endpoint:** `POST /api/invoices/pay-cash`

**Mô tả:** Thanh toán hóa đơn bằng tiền mặt

**Request Body:**
```json
{
  "medicalRecordId": 1,
  "amount": 500000,
  "paymentMethod": "CASH"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Payment successful"
}
```

---

## 13. Payment API

### Base URL: `/api/payments`

### 13.1 Tạo link thanh toán
**Endpoint:** `POST /api/payments/create-link`

**Mô tả:** Tạo link thanh toán online (PayOS)

**Request Body:**
```json
{
  "medicalRecordId": 1,
  "amount": 500000,
  "description": "string",
  "returnUrl": "string",
  "cancelUrl": "string"
}
```

**Response:**
```json
{
  "data": {
    "checkoutUrl": "string",
    "orderCode": 123456,
    "qrCode": "string"
  },
  "message": "Payment link created successfully"
}
```

---

### 13.2 Webhook thanh toán
**Endpoint:** `POST /api/payments/webhook`

**Mô tả:** Nhận webhook từ cổng thanh toán (chỉ dành cho hệ thống)

**Request Body:**
```json
{
  "orderCode": 123456,
  "status": "PAID",
  "transactionId": "string"
}
```

**Response:**
- Status: 200 OK

---

### 13.3 Kiểm tra trạng thái thanh toán
**Endpoint:** `GET /api/payments/status/{orderCode}`

**Mô tả:** Kiểm tra trạng thái đơn hàng thanh toán

**Path Parameters:**
- `orderCode`: Mã đơn hàng

**Response:**
```json
{
  "data": {
    "orderCode": 123456,
    "status": "PAID",
    "amount": 500000,
    "transactionId": "string"
  },
  "message": "Payment status retrieved successfully"
}
```

---

## 14. Schedule API

### Base URL: `/api/schedules`

### 14.1 Tạo lịch làm việc
**Endpoint:** `POST /api/schedules`

**Mô tả:** Tạo lịch làm việc cho bác sĩ

**Request Body:**
```json
{
  "doctorId": 1,
  "date": "2024-12-20",
  "shift": "MORNING", // MORNING, AFTERNOON, EVENING
  "maxPatients": 20
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "doctorId": 1,
    "date": "2024-12-20"
  },
  "message": "create schedule success"
}
```

---

### 14.2 Xóa lịch làm việc
**Endpoint:** `DELETE /api/schedules/{id}`

**Mô tả:** Xóa lịch làm việc

**Path Parameters:**
- `id`: ID của lịch làm việc

**Response:**
```json
{
  "data": "",
  "message": "delete schedule success"
}
```

---

### 14.3 Lấy lịch khả dụng
**Endpoint:** `GET /api/schedules/available`

**Mô tả:** Lọc lịch làm việc khả dụng

**Query Parameters:**
- `startDate` (required): Ngày bắt đầu (yyyy-MM-dd)
- `endDate` (required): Ngày kết thúc (yyyy-MM-dd)
- `departmentId` (optional): ID khoa
- `doctorId` (optional): ID bác sĩ
- `shift` (optional): Ca làm việc (MORNING, AFTERNOON, EVENING)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "doctorName": "string",
      "date": "2024-12-20",
      "shift": "MORNING",
      "availableSlots": 15,
      "maxPatients": 20
    }
  ],
  "message": "get available slots success"
}
```

---

### 14.4 Lấy lịch nghỉ của tôi
**Endpoint:** `GET /api/schedules/leave/me`

**Mô tả:** Lấy danh sách lịch nghỉ của bác sĩ đang đăng nhập

**Query Parameters:**
- `date` (optional): Ngày (yyyy-MM-dd)
- `status` (optional): Trạng thái (PENDING, APPROVED, REJECTED)

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "startDate": "2024-12-20",
      "endDate": "2024-12-22",
      "reason": "string",
      "status": "PENDING"
    }
  ],
  "message": "get my leaves success"
}
```

---

### 14.5 Đăng ký nghỉ phép
**Endpoint:** `POST /api/schedules/leave`

**Mô tả:** Tạo đơn xin nghỉ phép

**Request Body:**
```json
{
  "startDate": "2024-12-20",
  "endDate": "2024-12-22",
  "reason": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "create leave success"
}
```

---

### 14.6 Cập nhật lịch nghỉ
**Endpoint:** `PUT /api/schedules/leave`

**Mô tả:** Cập nhật hoặc duyệt đơn nghỉ phép

**Request Body:**
```json
{
  "id": 1,
  "status": "APPROVED", // PENDING, APPROVED, REJECTED
  "note": "string"
}
```

**Response:**
```json
{
  "data": "",
  "message": "update schedule success"
}
```

---

### 14.7 Xóa lịch nghỉ
**Endpoint:** `DELETE /api/schedules/leave`

**Mô tả:** Hủy đơn nghỉ phép

**Request Body:**
```json
{
  "id": 1
}
```

**Response:**
```json
{
  "data": "",
  "message": "delete leave success"
}
```

---

## 15. Receptionist API

### Base URL: `/api/receptionists`

### 15.1 Xác nhận lịch hẹn
**Endpoint:** `POST /api/receptionists/confirm`

**Mô tả:** Xác nhận và xử lý lịch hẹn (dành cho lễ tân)

**Request Body:**
```json
{
  "id": 1,
  "status": "CONFIRMED"
}
```

**Response:**
```json
{
  "data": "",
  "message": "Appointment confirmed successfully"
}
```

---

## 16. User API

### Base URL: `/api/users`

### 16.1 Tạo người dùng
**Endpoint:** `POST /api/users`

**Mô tả:** Tạo tài khoản người dùng mới (admin)

**Request Body:**
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "role": "BAC_SI", // BAC_SI, LE_TAN, BENH_NHAN, etc.
  "name": "string"
}
```

**Response:**
```json
{
  "id": 1,
  "username": "string",
  "email": "string",
  "role": "BAC_SI"
}
```

---

### 16.2 Lấy thông tin người dùng theo ID
**Endpoint:** `GET /api/users/{id}`

**Mô tả:** Lấy thông tin người dùng

**Path Parameters:**
- `id`: ID của người dùng

**Response:**
```json
{
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "role": "BAC_SI"
  },
  "message": "Get info successfully"
}
```

---

### 16.3 Lấy thông tin người dùng hiện tại
**Endpoint:** `GET /api/users/me`

**Mô tả:** Lấy thông tin người dùng đang đăng nhập

**Headers:**
- `Authorization`: Bearer {token}

**Response:**
```json
{
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "role": "BAC_SI",
    "profile": {}
  },
  "message": "Get info successfully"
}
```

---

### 16.4 Xóa người dùng
**Endpoint:** `DELETE /api/users/{id}`

**Mô tả:** Xóa tài khoản người dùng

**Path Parameters:**
- `id`: ID của người dùng

**Response:**
- Status: 204 No Content

---

## 17. HTML Export API

### Base URL: `/api/html`

### 17.1 Xuất HTML phiếu khám
**Endpoint:** `GET /api/html/medical-record/{id}`

**Mô tả:** Xuất phiếu khám bệnh dưới dạng HTML

**Path Parameters:**
- `id`: ID của hồ sơ bệnh án

**Response:**
- Content-Type: text/html
- Body: HTML content của phiếu khám

---

### 17.2 Xuất HTML hóa đơn
**Endpoint:** `GET /api/html/invoice/{medicalRecordId}`

**Mô tả:** Xuất hóa đơn thanh toán dưới dạng HTML

**Path Parameters:**
- `medicalRecordId`: ID của hồ sơ bệnh án

**Response:**
- Content-Type: text/html
- Body: HTML content của hóa đơn

---

## Thông tin chung

### Authentication
Hầu hết các endpoint yêu cầu authentication thông qua JWT token:
```
Authorization: Bearer {token}
```

### Error Response Format
```json
{
  "error": "string",
  "message": "string",
  "status": 400
}
```

### Status Codes
- `200`: Thành công
- `201`: Tạo mới thành công
- `204`: Xóa thành công (No Content)
- `400`: Lỗi request không hợp lệ
- `401`: Chưa xác thực
- `403`: Không có quyền truy cập
- `404`: Không tìm thấy
- `500`: Lỗi server

### Date/Time Format
- Date: `yyyy-MM-dd` (VD: 2024-12-20)
- Time: `HH:mm:ss` (VD: 09:30:00)
- DateTime: `yyyy-MM-dd'T'HH:mm:ss` (VD: 2024-12-20T09:30:00)

### Enums

#### Gender
- `MALE`: Nam
- `FEMALE`: Nữ
- `OTHER`: Khác

#### Appointment Status
- `PENDING`: Chờ xác nhận
- `CONFIRMED`: Đã xác nhận
- `CANCELLED`: Đã hủy
- `COMPLETED`: Hoàn thành

#### Medical Record Status
- `PENDING`: Chờ khám
- `IN_PROGRESS`: Đang khám
- `COMPLETED`: Hoàn thành
- `CANCELLED`: Đã hủy

#### Lab Order Status
- `PENDING`: Chờ thực hiện
- `IN_PROGRESS`: Đang thực hiện
- `COMPLETED`: Hoàn thành

#### Leave Status
- `PENDING`: Chờ duyệt
- `APPROVED`: Đã duyệt
- `REJECTED`: Từ chối

#### Shift
- `MORNING`: Sáng
- `AFTERNOON`: Chiều
- `EVENING`: Tối

#### User Roles
- `BENH_NHAN`: Bệnh nhân
- `BAC_SI`: Bác sĩ
- `LE_TAN`: Lễ tân
- `QUAN_TRI`: Quản trị viên

---

## Ghi chú
- Tài liệu này mô tả tất cả API endpoints của hệ thống quản lý phòng khám
- Một số endpoint có thể yêu cầu quyền đặc biệt tùy theo role của user
- Để test API, có thể sử dụng Postman hoặc các công cụ tương tự
- Base URL mặc định: `http://localhost:8080` (development)

