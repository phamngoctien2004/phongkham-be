# Medical Records APIs

Base URL: `/api/medical-record`

## GET /
- **Description**: List medical records with optional filters
- **Query**: `keyword?`, `date?` (yyyy-MM-dd), `status?` (MedicalRecord.RecordStatus)
- **Response**: `ApiResponse<MedicalResponse[]>`

## GET /{id}
- **Description**: Get medical record detail by id
- **Response**: `ApiResponse<MedicalResponse>`

## GET /patient/{id}
- **Description**: List records by patient id
- **Response**: `ApiResponse<MedicalResponse[]>`

## GET /me
- **Description**: Records of current user
- **Response**: `ApiResponse<MedicalResponse[]>`

## GET /me/{cccd}
- **Description**: Relation records by CCCD
- **Response**: `ApiResponse<MedicalResponse[]>`

## POST /
- **Description**: Create medical record and auto-create lab order from health plan
- **Request Body** (`MedicalRequest`)
- **Response**: `ApiResponse<number>` (created record id)

## PUT /
- **Description**: Update medical record
- **Request Body** (`MedicalRequest`)
- **Response**: `ApiResponse<string>`

## PUT /status
- **Description**: Update record status
- **Request Body** (`MedicalRequest` with `id`, `status`)
- **Response**: `ApiResponse<string>`
