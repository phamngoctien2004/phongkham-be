# Hướng dẫn Đầy đủ Module Báo cáo và Thống kê

## Tổng quan

Module báo cáo và thống kê cung cấp các API để theo dõi và phân tích dữ liệu của hệ thống quản lý phòng khám.

## Danh sách API

### 1. Báo cáo Doanh thu
**Endpoint:** `GET /api/reports/revenue`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu (format: yyyy-MM-dd)
- `toDate` (required): Ngày kết thúc (format: yyyy-MM-dd)

**Ví dụ:**
```http
GET /api/reports/revenue?fromDate=2024-01-01&toDate=2024-12-31
```

**Response:**
```json
{
  "data": {
    "fromDate": "2024-01-01",
    "toDate": "2024-12-31",
    "totalRevenue": 150000000,
    "totalPaid": 150000000,
    "totalUnpaid": 0,
    "totalInvoices": 120,
    "totalPaidInvoices": 100,
    "totalUnpaidInvoices": 20,
    "revenueByDays": [
      {
        "date": "2024-01-01",
        "revenue": 5000000,
        "invoiceCount": 5
      }
    ],
    "revenueByPaymentMethods": [
      {
        "paymentMethod": "TIEN_MAT",
        "amount": 80000000,
        "count": 60
      },
      {
        "paymentMethod": "CHUYEN_KHOAN",
        "amount": 70000000,
        "count": 40
      }
    ]
  },
  "message": "Get revenue report successfully"
}
```

---

### 2. Báo cáo Lịch khám
**Endpoint:** `GET /api/reports/appointments`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc
- `doctorId` (optional): ID bác sĩ cần lọc
- `departmentId` (optional): ID khoa cần lọc

**Ví dụ:**
```http
GET /api/reports/appointments?fromDate=2024-01-01&toDate=2024-12-31&doctorId=1
```

**Response:**
```json
{
  "data": {
    "fromDate": "2024-01-01",
    "toDate": "2024-12-31",
    "totalAppointments": 500,
    "confirmedAppointments": 450,
    "completedAppointments": 400,
    "cancelledAppointments": 50,
    "noShowAppointments": 0,
    "appointmentsByDoctor": [
      {
        "doctorId": 1,
        "doctorName": "BS. Nguyễn Văn A",
        "departmentName": "Nội khoa",
        "totalAppointments": 100,
        "completedAppointments": 90,
        "cancelledAppointments": 10
      }
    ],
    "appointmentsByDepartment": [
      {
        "departmentId": 1,
        "departmentName": "Nội khoa",
        "totalAppointments": 200,
        "completedAppointments": 180
      }
    ],
    "appointmentsByDay": [
      {
        "date": "2024-01-01",
        "appointmentCount": 10,
        "completedCount": 9
      }
    ]
  },
  "message": "Get appointment report successfully"
}
```

---

### 3. Báo cáo Bệnh nhân
**Endpoint:** `GET /api/reports/patients`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc

**Ví dụ:**
```http
GET /api/reports/patients?fromDate=2024-01-01&toDate=2024-12-31
```

**Response:**
```json
{
  "data": {
    "fromDate": "2024-01-01",
    "toDate": "2024-12-31",
    "totalNewPatients": 300,
    "totalReturningPatients": 200,
    "totalPatients": 500,
    "patientsByDay": [
      {
        "date": "2024-01-01",
        "newPatientCount": 5,
        "returningPatientCount": 3
      }
    ],
    "patientsByGender": [
      {
        "gender": "NAM",
        "count": 250,
        "percentage": 50.0
      },
      {
        "gender": "NU",
        "count": 250,
        "percentage": 50.0
      }
    ],
    "patientsByAgeGroup": [
      {
        "ageGroup": "Dưới 18",
        "count": 50,
        "percentage": 10.0
      },
      {
        "ageGroup": "18-30",
        "count": 150,
        "percentage": 30.0
      },
      {
        "ageGroup": "31-50",
        "count": 200,
        "percentage": 40.0
      },
      {
        "ageGroup": "51-65",
        "count": 80,
        "percentage": 16.0
      },
      {
        "ageGroup": "Trên 65",
        "count": 20,
        "percentage": 4.0
      }
    ]
  },
  "message": "Get patient report successfully"
}
```

---

### 4. Thống kê Hiệu suất Bác sĩ
**Endpoint:** `GET /api/reports/doctor-performance`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc
- `doctorId` (optional): ID bác sĩ cần lọc

**Ví dụ:**
```http
GET /api/reports/doctor-performance?fromDate=2024-01-01&toDate=2024-12-31
```

**Response:**
```json
{
  "data": {
    "fromDate": "2024-01-01",
    "toDate": "2024-12-31",
    "doctorPerformances": [
      {
        "doctorId": 1,
        "doctorName": "BS. Nguyễn Văn A",
        "departmentName": "Nội khoa",
        "totalAppointments": 200,
        "completedAppointments": 180,
        "cancelledAppointments": 20,
        "totalPatients": 200,
        "totalRevenue": 0,
        "completionRate": 90.0,
        "averageRating": 0.0,
        "totalRatings": 0
      }
    ]
  },
  "message": "Get doctor performance successfully"
}
```

---

### 5. Thống kê Dịch vụ Phổ biến
**Endpoint:** `GET /api/reports/services`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc

**Ví dụ:**
```http
GET /api/reports/services?fromDate=2024-01-01&toDate=2024-12-31
```

**Response:**
```json
{
  "data": {
    "fromDate": "2024-01-01",
    "toDate": "2024-12-31",
    "totalServices": 10,
    "popularServices": [
      {
        "serviceId": 1,
        "serviceName": "Khám tổng quát",
        "usageCount": 150,
        "totalRevenue": 75000000,
        "price": 500000
      }
    ],
    "servicesByDepartment": [
      {
        "departmentId": 1,
        "departmentName": "Nội khoa",
        "serviceCount": 5,
        "usageCount": 200,
        "totalRevenue": 100000000
      }
    ]
  },
  "message": "Get service report successfully"
}
```

---

### 6. Xuất Báo cáo PDF
**Endpoint:** `GET /api/reports/export/pdf`

**Parameters:**
- `reportType` (required): Loại báo cáo (revenue, appointments, patients, doctor-performance, services)
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc

**Ví dụ:**
```http
GET /api/reports/export/pdf?reportType=revenue&fromDate=2024-01-01&toDate=2024-12-31
```

**Response:** File PDF được tải xuống

---

### 7. Xuất Báo cáo Excel
**Endpoint:** `GET /api/reports/export/excel`

**Parameters:**
- `reportType` (required): Loại báo cáo (revenue, appointments, patients, doctor-performance, services)
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc

**Ví dụ:**
```http
GET /api/reports/export/excel?reportType=revenue&fromDate=2024-01-01&toDate=2024-12-31
```

**Response:** File Excel (.xlsx) được tải xuống

---

### 8. Dashboard Tổng hợp
**Endpoint:** `GET /api/reports/dashboard`

**Parameters:**
- `fromDate` (required): Ngày bắt đầu
- `toDate` (required): Ngày kết thúc

**Ví dụ:**
```http
GET /api/reports/dashboard?fromDate=2024-01-01&toDate=2024-12-31
```

**Response:**
```json
{
  "data": {
    "revenue": { /* Dữ liệu báo cáo doanh thu */ },
    "appointments": { /* Dữ liệu báo cáo lịch khám */ },
    "patients": { /* Dữ liệu báo cáo bệnh nhân */ },
    "services": { /* Dữ liệu báo cáo dịch vụ */ }
  },
  "message": "Get dashboard report successfully"
}
```

---

## Lưu ý

1. **Định dạng ngày:** Tất cả các tham số ngày phải theo định dạng ISO: `yyyy-MM-dd` (ví dụ: 2024-01-01)

2. **Authorization:** Các API này có thể yêu cầu xác thực. Đảm bảo gửi JWT token trong header:
```
Authorization: Bearer {token}
```

3. **Phân quyền:** Chỉ người dùng có quyền quản lý (ADMIN, RECEPTIONIST) mới có thể truy cập các API báo cáo

4. **Xuất file:** Khi xuất PDF hoặc Excel, file sẽ được tải về tự động với tên `report.pdf` hoặc `report.xlsx`

## Công nghệ Sử dụng

- **Spring Boot:** Framework chính
- **Spring Data JPA:** Truy vấn cơ sở dữ liệu
- **Apache POI:** Xuất file Excel
- **HTML to PDF:** Xuất file PDF (sử dụng PdfService có sẵn)

## Kiến trúc

```
Controller (ReportController)
    ↓
Service Interface (ReportService)
    ↓
Service Implementation (ReportServiceImpl)
    ↓
Repository Layer (AppointmentRepository, InvoiceRepository, PatientRepository, ExaminationServiceRepository)
    ↓
Database
```

## Mở rộng

Để thêm báo cáo mới:

1. Tạo DTO Response trong package `dto.response`
2. Thêm query method vào Repository tương ứng
3. Implement method trong ReportServiceImpl
4. Thêm endpoint trong ReportController
5. Thêm logic xuất PDF/Excel nếu cần

## Các vấn đề đã được sửa

✅ Sửa lỗi enum `PaymentStatus.PAID` → `PaymentStatus.DA_THANH_TOAN`
✅ Sửa lỗi tên trường `dateOfBirth` → `birth` trong Patient
✅ Sửa lỗi enum `AppointmentStatus.COMPLETED` → `AppointmentStatus.HOAN_THANH`
✅ Hoàn thiện các query method trong Repository
✅ Implement đầy đủ chức năng xuất PDF/Excel

