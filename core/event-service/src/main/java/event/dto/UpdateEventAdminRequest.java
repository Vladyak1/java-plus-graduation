package event.dto;

import lombok.*;
import event.model.Location;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "The annotation field must be between 20 and 2000 characters long.")
    private String annotation;
    @Positive
    private Long category;
    @Size(min = 20, max = 7000, message = "The description field must be between 20 and 7000 characters long.")
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120, message = "The title field must be between 3 and 120 characters long.")
    private String title;
}