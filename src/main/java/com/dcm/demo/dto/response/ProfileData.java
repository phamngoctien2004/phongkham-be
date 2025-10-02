package com.dcm.demo.dto.response;

public sealed interface ProfileData permits DoctorResponse, PatientResponse, UserResponse {
}
