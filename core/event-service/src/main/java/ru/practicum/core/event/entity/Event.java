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
@Table(name = "events")
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "annotation")
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Transient
    private Long confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "location_id")
    private Long locationId;

    @Transient
    private LocationDto location;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "initiator_id")
    private Long initiatorId;

    @Transient
    private UserDto initiator;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "TITLE")
    private String title;

    @Transient
    private Long views;

    @Transient
    private Long likes;

}
