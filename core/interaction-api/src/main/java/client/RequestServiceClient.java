package client;

import ru.practicum.dto.request.ParticipationRequestDto;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "request-service")
public interface RequestServiceClient {

    @GetMapping("/internal/request/byEvents")
    List<ParticipationRequestDto> getAllByEventId(Long eventId);

    @GetMapping("/internal/request")
    List<ParticipationRequestDto> getAllByRequestIdIn(@NotEmpty List<Long> requestIds);

    @PostMapping("/internal/request/all")
    void saveAll(@RequestBody List<ParticipationRequestDto> requests);
}
