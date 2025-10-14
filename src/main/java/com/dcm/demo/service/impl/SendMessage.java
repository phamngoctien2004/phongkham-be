package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.OtpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SendMessage {
    @Value("${ID_PHONE}")
    private String ip_phone;

    @Value("${API_KEY_PHONE}")
    private String phone_api_key;

    private final RedisTemplate<String, Object> redisTemplate;
    public void sendOtp(OtpRequest request) {
        Random random = new Random();
        Integer number = 100000 + random.nextInt(900000);
        System.out.println("Random 6 digits: " + number);

        request.setMessage("Mã OTP của bạn là: " + number);
        
        // gui otp qua dien thoai
//        org.springframework.http.HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
//        headers.add("Authorization", phone_api_key);
//        HttpEntity<?> httpEntity = new HttpEntity<>(request, headers);
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.postForEntity(
//                ip_phone,
//                httpEntity,
//                OtpRequest.class
//        );
        redisTemplate.opsForValue().set("otp:sync:" + request.getTo(), number, 300, TimeUnit.SECONDS);
        // Gửi OTP qua SMS (giả lập)
        System.out.println("Sending OTP " + number + " to phone number: " + request.getTo());
    }
    public void checkOtp(String phone, Integer otp) {
        Integer cachedOtp = (Integer) redisTemplate.opsForValue().get("otp:sync:" + phone);
        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        // Xóa OTP sau khi xác thực thành công
        redisTemplate.delete("otp:sync:" + phone);
    }
}
