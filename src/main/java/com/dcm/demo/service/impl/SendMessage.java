package com.dcm.demo.service.impl;

import com.dcm.demo.dto.request.OtpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
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
        String message = "you code is " + number;
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", request.getTo());
        payload.put("message", message);


        request.setMessage("ma cua ban la: " + number);
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", phone_api_key);
//        headers.add("Authorization", phone_api_key);
        HttpEntity<?> httpEntity = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();
        Object o = restTemplate.postForEntity(
                ip_phone,
                httpEntity,
                String.class
        );
        System.out.println(o.toString());
        redisTemplate.opsForValue().set("otp:" + request.getTo(), number, 300, TimeUnit.SECONDS);
    }
    public void checkOtp(String phone, Integer otp) {
        Integer cachedOtp = (Integer) redisTemplate.opsForValue().get("otp:" + phone);
        if (cachedOtp == null || !cachedOtp.equals(otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        // Xóa OTP sau khi xác thực thành công
        redisTemplate.delete("otp:" + phone);
    }
}
