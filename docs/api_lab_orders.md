# Lab Orders APIs

Base URL: `/api/lab-orders`

## GET /code/{code}
- **Description**: Get lab order by record code
- **Response**: `ApiResponse<LabOrderResponse>`

## GET /{id}
- **Description**: Get lab order by id
- **Response**: `ApiResponse<LabOrderResponse>`

## GET /
- **Description**: List all lab orders
- **Response**: `ApiResponse<LabOrderResponse[]>`

## GET /doctor/me
- **Description**: List lab orders for performing doctor with optional filters
- **Query**: `keyword?`, `status?` (LabOrder.TestStatus), `date?` (yyyy-MM-dd)
- **Response**: `ApiResponse<LabOrderResponse[]>`

## POST /
- **Description**: Create lab order
- **Request Body** (`LabOrderRequest`)
- **Response**: `ApiResponse<string>`

## PUT /status
- **Description**: Update lab order status
- **Request Body** (`LabOrderRequest` with `id`, `status`)
- **Response**: `ApiResponse<string>`

## PUT /
- **Description**: Update lab order
- **Request Body** (`LabOrderRequest`)
- **Response**: `ApiResponse<string>`

## DELETE /
- **Description**: Bulk delete by IDs
- **Request Body** (`LabDeleteRequest`)
- **Response**: `204 No Content`
