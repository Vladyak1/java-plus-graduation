package ru.practicum.model;

import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @JoinColumn(name = "event_id")
    private Long eventId;
    @JoinColumn(name = "author_id")
    private Long authorId;
    @Column(name = "created_on")
    private LocalDateTime create;
}
