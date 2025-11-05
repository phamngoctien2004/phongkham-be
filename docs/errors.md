# Error Model

## GlobalExceptionHandler
All exceptions are translated to HTTP responses:
- `AppException` -> HTTP status from `ErrorCode`, body `{ data: "", message: error.message }`
- `MethodArgumentNotValidException` -> `400 Bad Request`, body is a JSON array of validation errors
- `RuntimeException` -> `500 Internal Server Error`, body `{ data: "", message: ex.message }`

## ErrorCode Enum
The following error codes may be returned in the `message` field. The HTTP status is determined by `ErrorCode.httpStatus`.

| Code | Key | HTTP Status | Message |
|---|---|---|---|
| NOT_NULL_400 | NOT_NULL | 400 | Giá trị không xác định |
| USER_409 | USER_EXISTED | 409 | Người dùng đã tồn tại |
| REC_409 | RECORD_EXISTED | 409 | Bản ghi đã tồn tại |
| REC_404 | RECORD_NOTFOUND | 404 | Bản ghi không tồn tại |
| REC_400 | RECORD_UNDELETED | 400 | Bản khi không thể xóa |
| USER_404 | USER_NOT_FOUND | 404 | Người dùng không tồn tại |
| ROLE_404 | ROLE_NOT_FOUND | 404 | Không tìm thấy role |
| OTP_401 | OTP_INVALID | 401 | Mã otp không chính xác |
| OTP_429 | OTP_CANNOT_RESEND | 429 | Chưa được gửi lại otp |
| EMAIL_400 | SEND_EMAIL_FAILED | 400 | Gửi email thất bại |
| AUTH_401 | AUTH_FAILED | 401 | Xác thực không thành công |
| LOGIN_401 | LOGIN_AGAIN | 401 | Session expired, please login again |
| TOKEN_401 | TOKEN_EXPIRED | 400 | Token het han |
| JWT_402 | JWT_EXPIRED | 400 | Token expired |
| JWT_401 | JWT_INVALID | 400 | Token invalid |
| PASS_400 | CHANGE_PASSWORD_FAILED | 400 | Change password failed |
| FILE_EMPTY | FILE_EMPTY | 400 | File is not empty |
| FILE_TOO_LARGE | FILE_TOO_LARGE | 400 | File size < 10MB |
| FILE_UPLOAD_FAILED | FILE_UPLOAD_FAILED | 400 | upload failed |
| FILE_INVALID_FORMAT | FILE_INVALID_FORMAT | 400 | Invalid file format |
| SLOT_INVALID | SLOT_INVALID | 400 | Slot is not valid |
| DT_404 | MEDICINE_NOT_FOUND | 200 | Medicine not found |

## Authentication Errors
Unauthenticated access may return a `401` with body:

```json
{
  "data": {
    "code": "JWT_402",
    "message": "Token expired",
    "httpStatus": "BAD_REQUEST"
  },
  "message": "<framework-specific message>"
}
```
