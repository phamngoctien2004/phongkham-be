# System Overview

This project is a Spring Boot application exposing RESTful APIs for a clinic management domain. Public APIs are organized around:

- Authentication and JWT
- User, Patient, Doctor and Department resources
- Appointments and Schedules
- Medical Records, Prescriptions, Medicines
- Lab Orders and Lab Results
- Payments and Invoices
- HTML content export (server-side rendered)

## Response Wrapper
All successful endpoints (except 204 responses) return an `ApiResponse<T>`:

```json
{
  "data": { /* payload */ },
  "message": "human readable message"
}
```

## Security
- JWT resource server is configured; however, `SecurityConfig` currently whitelists `/api/**` for public access.
- Error responses use `ApiResponse` with an error message and appropriate HTTP status from `ErrorCode`.

## Components
- Controllers: under `com.dcm.demo.controller`
- Services: interfaces under `com.dcm.demo.service.interfaces`
- Mappers: under `com.dcm.demo.mapper`
- Repositories: under `com.dcm.demo.repository`
- DTOs: under `com.dcm.demo.dto.request` and `com.dcm.demo.dto.response`
