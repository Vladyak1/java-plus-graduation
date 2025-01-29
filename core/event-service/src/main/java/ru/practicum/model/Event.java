package ru.practicum.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.enums.EventState;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    private String description;

    @Column(name = "event_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @Column(name = "initiator_id")
    @Basic
    private Long initiator;

    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "loc_lat")
    private Float lat;
    @Column(name = "loc_lon")
    private Float lon;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
    @Column(name = "views")
    private List<Long> views;
}
