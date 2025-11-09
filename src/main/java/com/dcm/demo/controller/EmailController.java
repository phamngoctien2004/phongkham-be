package com.dcm.demo.controller;

import com.dcm.demo.dto.request.EmailRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/contact")
    public ApiResponse<?> sendEmailVerification(@RequestBody EmailRequest request) {
        emailService.sendFormMessage(request);
        return new ApiResponse<>("", "Send email successfully");
    }

    @PostMapping("/newsletter")
    public ApiResponse<?> sendNewsletterEmail(@RequestBody EmailRequest request) {
        emailService.sendNewsletterToAllUsers(request);
        return new ApiResponse<>("", "Send newsletter email successfully");
    }
}
