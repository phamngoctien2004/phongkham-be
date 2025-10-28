# Authentication APIs

Base URL: `/api/auth`

## POST /login
- **Description**: Authenticate user and return JWT + profile
- **Request Body** (`LoginRequest`):
```json
{
  "username": "string",
  "password": "string"
}
```
- **Response** (`ApiResponse<LoginResponse>`): accessToken, userResponse
- **Curl**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"secret"}'
```

## POST /send-otp
- **Description**: Send OTP to destination
- **Request Body** (`OtpRequest`): `{ "to": "string", "message": "string" }`
- **Response**: `ApiResponse<string>` with success message

## POST /dashboard/login
- Same as `/login` for dashboard context

## GET /test
- Generate a sample token (dev utility)
