package ru.practicum.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDtoRequest {

    @NotBlank
    @Size(max = 50, message = "The name must be no more than 50 characters long")
    private String name;
}
