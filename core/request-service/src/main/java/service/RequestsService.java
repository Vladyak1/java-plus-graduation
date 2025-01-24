package service;


import dto.request.ParticipationRequestDto;
import model.ParticipationRequest;

import java.util.List;

public interface RequestsService {
    List<ParticipationRequestDto> getRequest(Long userId);

    ParticipationRequestDto setRequest(Long eventId, Long userId);

    ParticipationRequestDto updateRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllByEventId(Long eventId);

    List<ParticipationRequestDto> getAllByRequestIdIn(List<Long> requestIds);

    List<ParticipationRequest> saveAll(List<ParticipationRequest> requests);

}