# Medicines APIs

Base URL: `/api/medicines`

## GET /
- **Description**: List medicines by optional keyword
- **Query**: `keyword?`
- **Response**: `ApiResponse<MedicineResponse[]>`

### Curl
```bash
curl "http://localhost:8080/api/medicines?keyword=para"
```
