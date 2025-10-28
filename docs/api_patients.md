# Patients APIs

Base URL: `/api/patients`

## POST /
- **Description**: Create patient
- **Request Body** (`PatientRequest`)
- **Response**: `ApiResponse<PatientResponse>`

## GET /
- **Description**: List patients (optional `keyword`)
- **Response**: `ApiResponse<PatientResponse[]>`

## GET /{id}
- **Description**: Get patient by id
- **Response**: `ApiResponse<PatientResponse>`

## PUT /
- **Description**: Update patient
- **Request Body** (`PatientRequest`)
- **Response**: `ApiResponse<PatientResponse>`

## GET /me
- **Description**: Get profile of patient linked to current user
- **Response**: `ApiResponse<PatientResponse>`

## GET /all
- **Description**: Get all patients
- **Response**: `ApiResponse<PatientResponse[]>`
