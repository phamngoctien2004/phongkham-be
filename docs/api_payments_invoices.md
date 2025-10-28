# Payments & Invoices APIs

Base URLs: `/api/payments`, `/api/invoices`

## Payments

### POST /payments/create-link
- **Description**: Create a PayOS payment link
- **Request Body** (`PaymentRequest`)
- **Response**: `ApiResponse<PayosLink>` (provider-specific)

### POST /payments/webhook
- **Description**: Webhook receiver for PayOS (server-to-server)
- **Request Body** (`WebhookRequest`)
- **Response**: void

### GET /payments/status/{orderCode}
- **Description**: Get payment status by provider order code
- **Response**: `ApiResponse<PayosStatus>`

## Invoices

### POST /invoices/pay-cash
- **Description**: Mark invoice as paid in cash; updates medical record
- **Request Body** (`PaymentRequest`)
- **Response**: `ApiResponse<string>`
