package com.dcm.demo.controller;

import com.dcm.demo.dto.request.RoomDTO;
import com.dcm.demo.dto.response.ApiResponse;
import com.dcm.demo.model.Room;
import com.dcm.demo.service.interfaces.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getAllRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Integer> departmentIds,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                new ApiResponse<>( roomService.findAll(pageable, keyword, departmentIds), "Fetched all rooms successfully")
        );
    }
    @GetMapping("/not-paginated")
    public ResponseEntity<?> getAllRoomsNoPagination(
            @RequestParam(required = false) String keyword){
        return ResponseEntity.ok(
                new ApiResponse<>(roomService.findAll(Pageable.unpaged(), keyword, null).getContent(), "Fetched all rooms successfully")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                new ApiResponse<>( roomService.findById(id), "Fetched room successfully")
        );
    }
    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody RoomDTO room) {

        return ResponseEntity.ok(
                new ApiResponse<>( roomService.create(room), "Added room successfully")
        );
    }
    @PutMapping
    public ResponseEntity<?> updateRoom(@RequestBody RoomDTO room) {

        return ResponseEntity.ok(
                new ApiResponse<>(  roomService.update(room), "Updated room successfully")
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer id) {
        roomService.delete(id);
        return ResponseEntity.ok(
                new ApiResponse<>(null, "Deleted room successfully")
        );
    }
}
