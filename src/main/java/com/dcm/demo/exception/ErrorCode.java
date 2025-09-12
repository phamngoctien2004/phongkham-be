package com.dcm.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    NOT_NULL("NOT_NULL_400", "Giá trị không xác định", HttpStatus.BAD_REQUEST),
    USER_EXISTED("USER_409", "Người dùng đã tồn tại", HttpStatus.CONFLICT),
    RECORD_EXISTED("REC_409", "Bản ghi đã tồn tại", HttpStatus.CONFLICT),
    RECORD_NOTFOUND("REC_404", "Bản ghi không tồn tại", HttpStatus.NOT_FOUND),
    RECORD_UNDELETED("REC_400", "Bản khi không thể xóa", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("USER_404", "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("ROLE_404", "Không tìm thấy role", HttpStatus.NOT_FOUND),
    OTP_INVALID("OTP_401", "Mã otp không chính xác", HttpStatus.UNAUTHORIZED),
    OTP_CANNOT_RESEND("OTP_429", "Chưa được gửi lại otp", HttpStatus.TOO_MANY_REQUESTS),
    SEND_EMAIL_FAILED("EMAIL_400", "Gửi email thất bại", HttpStatus.BAD_REQUEST),
    AUTH_FAILED("AUTH_401", "Xác thực không thành công", HttpStatus.UNAUTHORIZED),
    LOGIN_AGAIN("LOGIN_401", "Session expired, please login again", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("TOKEN_401", "Token het han", HttpStatus.BAD_REQUEST),
    JWT_EXPIRED("JWT_402", "Token expired", HttpStatus.BAD_REQUEST),
    JWT_INVALID("JWT_401", "Token invalid", HttpStatus.BAD_REQUEST),
    CHANGE_PASSWORD_FAILED("PASS_400", "Change password failed", HttpStatus.BAD_REQUEST),
    FILE_EMPTY("FILE_EMPTY", "File is not empty", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "File size < 10MB", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "upload failed", HttpStatus.BAD_REQUEST),
    FILE_INVALID_FORMAT("FILE_INVALID_FORMAT", "Invalid file format", HttpStatus.BAD_REQUEST),
    SLOT_INVALID("SLOT_INVALID", "Slot is not valid", HttpStatus.BAD_REQUEST),;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;


    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
