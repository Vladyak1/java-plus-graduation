package ru.practicum.controller;

import ru.practicum.dto.AdminUserDto;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.practicum.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDto> getById(@PathVariable long userId) {
        log.info("|| ==> GET Getting user by id: {}", userId);
        AdminUserDto adminUserDto = userService.findUserById(userId);
        log.info("|| <== GET Returning user: {}", adminUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(adminUserDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminUserDto>> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Calling the GET request to /internal/users/all endpoint");
        List<AdminUserDto> response = UserMapper.toListAdminUserDto(userService.readUsers(ids, from, size));
        return ResponseEntity.ok(response);
    }

}
