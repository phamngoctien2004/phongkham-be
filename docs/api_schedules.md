# Schedules APIs

Base URL: `/api/schedules`

## POST /
- **Description**: Create schedule for doctor
- **Request Body** (`ScheduleRequest`)
- **Response**: `ApiResponse<ScheduleResponse>`

## DELETE /{id}
- **Description**: Delete schedule by id
- **Response**: `ApiResponse<string>`

## GET /available
- **Description**: Get available slots filtered by date range, departmentId, doctorId, shift
- **Query**: `startDate` (yyyy-MM-dd), `endDate` (yyyy-MM-dd), `departmentId?`, `doctorId?`, `shift?`
- **Response**: `ApiResponse<SlotResponse[]>`

## GET /leave/me
- **Description**: Get my leave requests (optional `date`, `status`)
- **Response**: `ApiResponse<LeaveResponse[]>`

## POST /leave
- **Description**: Create leave request
- **Request Body** (`LeaveRequest`)
- **Response**: `ApiResponse<string>`

## PUT /leave
- **Description**: Update leave request
- **Request Body** (`LeaveRequest`)
- **Response**: `ApiResponse<string>`

## DELETE /leave
- **Description**: Delete leave by id
- **Request Body**: `{ "id": number }
- **Response**: `ApiResponse<string>`
