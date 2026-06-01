package com.neuralforge.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "code_decisions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String context;

    @Column(columnDefinition = "TEXT")
    private String decision;

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "vector_id")
    private String vectorId;

    @Column(name = "tags")
    private String tags;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
