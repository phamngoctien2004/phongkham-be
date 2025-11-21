# PHÂN NHÓM CÁC API THEO CHỨC NĂNG

## 🔐 XÁC THỰC (Authentication)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | POST | `/api/auth/login` | Đăng nhập cho bệnh nhân |
| 2 | POST | `/api/auth/dashboard/login` | Đăng nhập cho admin/nhân viên |
| 3 | POST | `/api/auth/register` | Đăng ký tài khoản |
| 4 | POST | `/api/auth/send-otp` | Gửi OTP quên mật khẩu |
| 5 | POST | `/api/auth/register-otp` | Gửi OTP đăng ký |
| 6 | POST | `/api/auth/verify-otp` | Xác thực OTP |
| 7 | POST | `/api/auth/reset-password` | Đặt lại mật khẩu |

---

## 👤 QUẢN LÝ NGƯỜI DÙNG (User Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/users` | Lấy danh sách người dùng |
| 2 | GET | `/api/users/{id}` | Lấy thông tin người dùng theo ID |
| 3 | GET | `/api/users/me` | Lấy thông tin người dùng hiện tại |
| 4 | POST | `/api/users` | Tạo người dùng mới |
| 5 | PUT | `/api/users` | Cập nhật thông tin người dùng |
| 6 | DELETE | `/api/users/{id}` | Xóa người dùng |
| 7 | POST | `/api/users/change-password` | Đổi mật khẩu |
| 8 | GET | `/api/users/notifications` | Lấy thông báo của người dùng |
| 9 | POST | `/api/users/notifications/mark-as-read` | Đánh dấu đã đọc thông báo |
| 10 | POST | `/api/users/send-newsletter/{id}` | Gửi tin tức đến tất cả người dùng |

---

## 🩺 QUẢN LÝ BÁC SĨ (Doctor Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/doctors` | Lấy danh sách bác sĩ |
| 2 | GET | `/api/doctors/admin` | Lấy danh sách bác sĩ cho admin |
| 3 | GET | `/api/doctors/{id}` | Lấy thông tin bác sĩ theo ID |
| 4 | GET | `/api/doctors/me` | Lấy thông tin bác sĩ hiện tại |
| 5 | POST | `/api/doctors` | Tạo bác sĩ mới |
| 6 | PUT | `/api/doctors` | Cập nhật thông tin bác sĩ |
| 7 | DELETE | `/api/doctors/{id}` | Xóa bác sĩ |

---

## 🏥 QUẢN LÝ BỆNH NHÂN (Patient Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/patients` | Lấy danh sách bệnh nhân |
| 2 | GET | `/api/patients/{id}` | Lấy thông tin bệnh nhân theo ID |
| 3 | GET | `/api/patients/me` | Lấy thông tin bệnh nhân của tôi |
| 4 | GET | `/api/patients/relationships` | Lấy danh sách người thân |
| 5 | POST | `/api/patients` | Tạo bệnh nhân mới |
| 6 | POST | `/api/patients/relationships` | Thêm quan hệ người thân |
| 7 | POST | `/api/patients/relationships/verify` | Xác thực quan hệ người thân |
| 8 | PUT | `/api/patients` | Cập nhật thông tin bệnh nhân |
| 9 | DELETE | `/api/patients/{id}` | Xóa bệnh nhân |
| 10 | DELETE | `/api/patients/relationships/{patientId}` | Xóa quan hệ người thân |

---

## 📅 ĐẶT LỊCH KHÁM (Appointment Booking)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | POST | `/api/appointments` | Đặt lịch khám |
| 2 | GET | `/api/appointments` | Lấy danh sách lịch khám theo số điện thoại |
| 3 | GET | `/api/appointments/{id}` | Lấy thông tin lịch khám theo ID |
| 4 | GET | `/api/appointments/me` | Lấy lịch khám của tôi |
| 5 | GET | `/api/appointments/invalid-services` | Lấy dịch vụ không khả dụng |
| 6 | PUT | `/api/appointments` | Cập nhật lịch khám |
| 7 | PUT | `/api/appointments/confirm` | Xác nhận lịch khám |
| 8 | GET | `/api/appointments/check-payment/{id}` | Kiểm tra trạng thái thanh toán |
| 9 | POST | `/api/appointments/send-email-success/{id}` | Gửi email thành công |

---

## 📋 QUẢN LÝ HỒ SƠ BỆNH ÁN (Medical Records)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/medical-record` | Lấy tất cả hồ sơ bệnh án |
| 2 | GET | `/api/medical-record/doctor` | Lấy hồ sơ theo bác sĩ |
| 3 | GET | `/api/medical-record/{id}` | Lấy chi tiết hồ sơ theo ID |
| 4 | GET | `/api/medical-record/patient/{id}` | Lấy hồ sơ theo ID bệnh nhân |
| 5 | GET | `/api/medical-record/me` | Lấy hồ sơ bệnh án của tôi |
| 6 | GET | `/api/medical-record/me/{cccd}` | Lấy hồ sơ theo CCCD |
| 7 | GET | `/api/medical-record/{id}/invoice` | Lấy hóa đơn theo ID hồ sơ |
| 8 | POST | `/api/medical-record` | Tạo hồ sơ bệnh án mới |
| 9 | PUT | `/api/medical-record` | Cập nhật hồ sơ bệnh án |
| 10 | PUT | `/api/medical-record/status` | Cập nhật trạng thái hồ sơ |

---

## 💊 QUẢN LÝ ĐơN THUỐC (Prescription Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/prescriptions/medical-record/{id}` | Lấy đơn thuốc theo ID hồ sơ |
| 2 | POST | `/api/prescriptions` | Tạo đơn thuốc |
| 3 | PUT | `/api/prescriptions` | Cập nhật đơn thuốc |
| 4 | POST | `/api/prescriptions/details` | Tạo chi tiết đơn thuốc |
| 5 | PUT | `/api/prescriptions/details` | Cập nhật chi tiết đơn thuốc |
| 6 | DELETE | `/api/prescriptions/details/{id}` | Xóa chi tiết đơn thuốc |

---


## 💰 THANH TOÁN (Payment)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | POST | `/api/payments/create-link` | Tạo link thanh toán online |
| 2 | POST | `/api/payments/webhook` | Xử lý webhook từ cổng thanh toán |
| 3 | GET | `/api/payments/status/{orderCode}` | Kiểm tra trạng thái thanh toán |

---

## 🧾 QUẢN LÝ HÓA ĐƠN (Invoice Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/invoices` | Lấy danh sách hóa đơn |
| 2 | POST | `/api/invoices/pay-cash` | Thanh toán tiền mặt |

---

## 📆 QUẢN LÝ LỊCH LÀM VIỆC (Schedule Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/schedules/available` | Lấy lịch trống |
| 2 | GET | `/api/schedules/leave/me` | Lấy lịch nghỉ của tôi |
| 3 | GET | `/api/schedules/leave/doctor/{doctorId}` | Lấy lịch nghỉ theo bác sĩ |
| 4 | POST | `/api/schedules` | Tạo lịch làm việc |
| 5 | POST | `/api/schedules/leave` | Tạo lịch nghỉ |
| 6 | PUT | `/api/schedules/leave` | Cập nhật lịch nghỉ |
| 7 | DELETE | `/api/schedules/{id}` | Xóa lịch làm việc |
| 8 | DELETE | `/api/schedules/leave` | Xóa lịch nghỉ |

---

## 🏢 QUẢN LÝ KHOA (Department Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/departments` | Lấy danh sách khoa |
| 2 | GET | `/api/departments/admin` | Lấy danh sách khoa cho admin |
| 3 | GET | `/api/departments/{id}` | Lấy thông tin khoa theo ID |
| 4 | GET | `/api/departments/{id}/doctors` | Lấy danh sách bác sĩ theo khoa |
| 5 | POST | `/api/departments` | Tạo khoa mới |
| 6 | PUT | `/api/departments` | Cập nhật thông tin khoa |
| 7 | DELETE | `/api/departments/{id}` | Xóa khoa |

---

## 🔔 QUẢN LÝ THÔNG BÁO (Notification Management)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/notifications` | Lấy danh sách thông báo hệ thống |
| 2 | GET | `/api/notifications/{id}` | Lấy thông báo theo ID |
| 3 | POST | `/api/notifications` | Tạo thông báo mới |
| 4 | PUT | `/api/notifications` | Cập nhật thông báo |
| 5 | DELETE | `/api/notifications/{id}` | Xóa thông báo |

---

## 📊 BÁO CÁO & THỐNG KÊ (Reports & Analytics)
| STT | Phương thức | Endpoint | Mô tả |
|-----|------------|----------|-------|
| 1 | GET | `/api/reports/revenue` | Báo cáo doanh thu |
| 2 | GET | `/api/reports/appointments` | Báo cáo lịch khám |
| 3 | GET | `/api/reports/patients` | Báo cáo bệnh nhân mới/tái khám |
| 4 | GET | `/api/reports/doctor-performance` | Thống kê hiệu suất bác sĩ |
| 5 | GET | `/api/reports/services` | Thống kê dịch vụ phổ biến |
| 6 | GET | `/api/reports/dashboard` | Báo cáo tổng quan |
| 7 | GET | `/api/reports/export/pdf` | Xuất báo cáo PDF |
| 8 | GET | `/api/reports/export/excel` | Xuất báo cáo Excel |

---

