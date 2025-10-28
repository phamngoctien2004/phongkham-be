# Appointments APIs

Base URL: `/api/appointments`

## POST /
- **Description**: Book appointment
- **Request Body** (`AppointmentRequest`)
- **Response**: `ApiResponse<string>`

## GET /
- **Description**: Search appointments by `phone` (optional), filter by `date` and `status`
- **Query**: `phone`, `date` (yyyy-MM-dd), `status` (enum: AppointmentStatus)
- **Response**: `ApiResponse<AppointmentResponse[]>`

## PUT /confirm
- **Description**: Confirm appointment
- **Request Body** (`AppointmentRequest` with `id` and `status`)
- **Response**: `ApiResponse<string>`
