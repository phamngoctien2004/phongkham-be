package com.dcm.demo.controller;

import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>(userService.findById(id), "Get info successfully")
        );
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int id = Integer.parseInt(authentication.getName());
        return ResponseEntity.ok(
                new ApiResponse<>(userService.findById(id), "Get info successfully")
        );
    }
//    @PutMapping("/{id}")
//    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequest) {
//        UserResponse response = userService.updateUser(id, userRequest);
//        if (response != null) {
//            return ResponseEntity.ok(response);
//        }
//        return ResponseEntity.notFound().build();
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
