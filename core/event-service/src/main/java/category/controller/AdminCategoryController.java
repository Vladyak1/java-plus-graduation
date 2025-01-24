package category.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import category.dto.CategoryDto;
import category.dto.CategoryDtoRequest;
import category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/categories")
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDtoRequest categoryDtoRequest) {
        log.info("Calling the POST request to /admin/categories endpoint");
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(categoryDtoRequest));
    }

    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@NonNull @PathVariable Long catId) {
        log.info("Calling the DELETE request to /admin/categories/{catId} endpoint");
        categoryService.deleteCategory(catId);
    }

    @PatchMapping(value = "/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long catId,
                                                      @Valid @RequestBody CategoryDtoRequest categoryDtoRequest) {
        log.info("Calling the PATCH request to /admin/categories/{catId} endpoint");
        return ResponseEntity.ok().body(categoryService.updateCategory(catId, categoryDtoRequest));
    }
}
