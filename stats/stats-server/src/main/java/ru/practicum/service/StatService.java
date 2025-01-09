package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.mapper.ServiceHitMapper;
import ru.practicum.model.ServiceHit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class StatService {
    private final StatRepository statRepository;
    private final ServiceHitMapper serviceHitMapper;

    public EndpointHit registerHit(EndpointHit endpointHit) {
        ServiceHit entity = serviceHitMapper.toEntity(endpointHit);
        log.info("StatService converted entity: {}", entity);
        ServiceHit saved = statRepository.save(entity);
        log.info("StatService saved entity: {}", saved);
        EndpointHit dto = serviceHitMapper.toDto(saved);
        log.info("StatService converted dto: {}", dto);
        return dto;
    }

    public List<ViewStats> getHits(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        List<ViewStats> viewStats;
        viewStats = statRepository.getHitListElementDtos(start, end, uris, unique);
        return viewStats;
    }
}
