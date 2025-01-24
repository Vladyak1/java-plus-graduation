package controller;

import dto.request.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.ParticipationRequest;
import model.mapper.RequestsMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RequestsService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/internal/request")
@RequiredArgsConstructor
public class InternalRequestController {

    private final RequestsService requestsService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAllByRequestIdIn(@RequestParam List<Long> ids) {
        log.info("Calling the GET request to /internal/request with ids: {}", ids);

        List<ParticipationRequestDto> participationRequests = requestsService.getAllByRequestIdIn(ids);
        log.info("Retrieved {} participation requests", participationRequests.size());

        return ResponseEntity.ok(participationRequests);
    }

    @GetMapping("/byEvents")
    public ResponseEntity<List<ParticipationRequestDto>> getAllByEventId(@RequestParam Long eventId) {
        log.info("Calling the GET request to /internal/request/byEvents with eventId: {}", eventId);

        List<ParticipationRequestDto> participationRequests = requestsService.getAllByEventId(eventId);
        log.info("Retrieved {} participation requests for eventId {}", participationRequests.size(), eventId);

        return ResponseEntity.ok(participationRequests);
    }

    @PostMapping("/all")
    public ResponseEntity<List<ParticipationRequestDto>> saveAll(@RequestBody List<ParticipationRequestDto> requestsDto) {
        log.info("Calling the POST request to save all participation requests");

        List<ParticipationRequest> requests = requestsDto.stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toParticipationRequest)
                .collect(Collectors.toList());

        List<ParticipationRequest> savedRequests = requestsService.saveAll(requests);

        List<ParticipationRequestDto> savedRequestsDto = savedRequests.stream()
                .map(RequestsMapper.REQUESTS_MAPPER::toParticipationRequestDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(savedRequestsDto);
    }

}
