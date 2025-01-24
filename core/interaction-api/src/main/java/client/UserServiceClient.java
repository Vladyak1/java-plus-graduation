package client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import dto.user.UserCreateDto;
import dto.user.AdminUserDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/internal/users/{userId}")
    AdminUserDto findUserById(@PathVariable long userId);

    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("userId") long userId);

    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    AdminUserDto add(@RequestBody @Valid UserCreateDto userCreateDto);

    @GetMapping("/admin/users")
    List<AdminUserDto> getAll(
            @RequestParam(required = false) Long[] ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    );

    @GetMapping("/internal/users/all")
    Map<Long, AdminUserDto> getAll(@RequestParam List<Long> userIds);

}