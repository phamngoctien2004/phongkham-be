package com.dcm.demo.controller;

import com.dcm.demo.dto.request.UserRequest;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.dto.response.UserResponse;
import com.dcm.demo.model.User;
import com.dcm.demo.service.impl.MeService;
import com.dcm.demo.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MeService meService;

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) User.Role role,
                                                @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                new ApiResponse<>(userService.findAll(pageable, keyword, role), "Fetched all users successfully")
        );
    }

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
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(
                new ApiResponse<>(meService.me(), "Get info successfully")
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
