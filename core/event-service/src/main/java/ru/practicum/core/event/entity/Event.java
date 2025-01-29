package ru.practicum.core.event.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.core.api.dto.location.LocationDto;
import ru.practicum.core.api.dto.user.UserDto;
import ru.practicum.core.api.enums.EventState;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Table(name = "EVENTS")
public class Event {

    @Id
    @Column(name = "EVENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ANNOTATION")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Transient
    private Long confirmedRequests;

    @Column(name = "CREATED_ON")
    private LocalDateTime createOn;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "EVENT_DATE")
    private LocalDateTime eventDate;

    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Transient
    private LocationDto location;

    @Column(name = "PAID")
    private boolean paid;

    @Column(name = "INITIATOR_ID")
    private Long initiatorId;

    @Transient
    private UserDto initiator;

    @Column(name = "PARTICIPANT_LIMIT")
    private int participantLimit;

    @Column(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;

    @Column(name = "REQUEST_MODERATION")
    private boolean requestModeration;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "TITLE")
    private String title;

    @Transient
    Long views;

    @Transient
    Long likes;

}
