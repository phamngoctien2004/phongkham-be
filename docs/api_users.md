# Users APIs

Base URL: `/api/users`

## POST /
- **Description**: Create a user
- **Request Body** (`UserRequest`)
- **Response**: `UserResponse`
- **Curl**:
```bash
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{"email":"a@b.com","phone":"0123","name":"Alice","role":"BENH_NHAN"}'
```

## GET /{id}
- **Description**: Get user by id
- **Response**: `ApiResponse<UserResponse>`

## GET /me
- **Description**: Current authenticated user's profile
- **Response**: `ApiResponse<ProfileData>`

## DELETE /{id}
- **Description**: Delete a user
- **Response**: `204 No Content`
