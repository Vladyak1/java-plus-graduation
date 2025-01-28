package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoReceived {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 250, message = "Name length must be between 2 and 250 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format. Please use a valid email address")
    private String email;

    private Boolean isAdmin;
}