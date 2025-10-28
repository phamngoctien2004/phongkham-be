# Service Interfaces Overview

Public service interfaces define the domain behaviors used by controllers. Implementations reside under `service.impl`.

- AppointmentService
  - createAppointment(request)
  - changeStatusAppointment(request): string
  - findById(id): Appointment
  - findByPhone(value, date, status): AppointmentResponse[]
  - findAllByDate(): AppointmentResponse[]

- MedicalRecordService
  - create(request): MedicalRecord
  - updateMedicalRecordInvoiceForCash(request)
  - update(request)
  - updateStatus(id, status)
  - me(): MedicalResponse[]
  - getRelationMedicalRecord(cccd): MedicalResponse[]
  - updateTotal(record, total)
  - findById(id): MedicalRecord
  - getDetailById(id): MedicalResponse
  - findByPatientId(patientId): MedicalResponse[]
  - findAll(keyword, status, date): MedicalResponse[]
  - webhookPayosForCheckStatus(request)

- LabOrderService
  - getAll(): LabOrderResponse[]
  - getByDoctorPerforming(keyword, date, status): LabOrderResponse[]
  - findByIds(ids): LabOrder[]
  - findByRecordCode(code): LabOrderResponse
  - buildResponse(labOrder): LabOrderResponse
  - findById(id): LabOrder
  - findResponseById(id): LabOrderResponse
  - createLabOrder(request)
  - updateLabOrder(request)
  - updateStatus(id, status)
  - createLabOrderFromHealthPlan(medicalRecord, healthPlanId)
  - deleteAllById(request)

- LabResultService
  - createResult(request): LabResultResponse

- UserService
  - getUserByEmailOrPhone()
  - findById(id): UserResponse
  - findByPhone(phone): User
  - getById(id): User
  - findByEmail(email): User
  - createUser(user): UserResponse
  - createAccountByPhone(phone): User
  - createUserEntity(user): User
  - updateUser(user): UserResponse
  - changePassword(request)
  - resetPassword(request)
  - deleteUser(id)
  - save(user): User
  - getCurrentUser(): User

- AuthService
  - login(request): LoginResponse
  - loginDashboard(request): LoginResponse
  - sendOtp(request)

- JwtService
  - generate(id, role, expiration): string
  - refresh(refreshToken): string
  - getClaims(token): Claims
  - encodeSecretKey(): SecretKey

- DoctorService
  - findById(id): Doctor
  - findAll(): DoctorResponse[]

- DepartmentService
  - getAllDepartment(): DepartmentResponse[]
  - findById(id): Department
  - findAll(): DepartmentResponse[]
  - findDoctorByDepartments(id): DoctorResponse[]
  - getRoomFromDepartment(dept): string

- PatientService
  - create(request): PatientResponse
  - update(request): PatientResponse
  - createPatientAndAccount(request): PatientResponse
  - createPatient(request, user): PatientResponse
  - findByPhone(phone): Patient
  - findByCccd(cccd): Patient
  - findById(id): PatientResponse
  - findAll(keyword): PatientResponse[]
  - me(): PatientResponse
  - all(): PatientResponse[]

- PrescriptionService
  - getPrescriptionsByMedicalRecordId(id): PrescriptionResponse
  - create(request): PrescriptionResponse
  - update(request): PrescriptionResponse
  - createPreDetail(request): PreDetailResponse
  - updatePreDetail(request): PreDetailResponse
  - deletePreDetail(id)

- ScheduleService
  - getLeaveByDoctor(date, leaveStatus): LeaveResponse[]
  - create(scheduleRequest): ScheduleResponse
  - createLeave(request)
  - deleteLeave(id)
  - delete(id)
  - updateLeave(request)
  - getShift(time): Schedule.Shift
  - filterSchedules(departmentId, doctorId, startDate, endDate, shift): SlotResponse[]

- HealthPlanService
  - getAllService(keyword): HealthPlanResponse[]
  - findById(id): HealthPlan
  - findResponseById(id): HealthPlanResponse
  - findAllById(ids): HealthPlan[]
  - calcTotalService(ids): BigDecimal

- MedicineService
  - getAllMedicines(keyword): MedicineResponse[]

- InvoiceService
  - findById(id): Invoice
  - findByPayosOrder(orderCode): Invoice
  - createInvoiceForQR(request): InvoiceResponse
  - createInvoiceForCash(record)
  - updateTotal(invoice, amount)
  - updatePaidAmount(invoice, amount)
  - checkStatusPayment(invoiceId): boolean
  - getServicesUnPay(recordId): number[]
  - save(invoice)
  - buildInvoiceDetail(invoice, healthPlanId, price, status, paymentMethod, paymentAmount)

- FileService
  - render(templateName, params, items): byte[]

- ProfileLoader
  - supports(role): boolean
  - loadProfile(): ProfileData
