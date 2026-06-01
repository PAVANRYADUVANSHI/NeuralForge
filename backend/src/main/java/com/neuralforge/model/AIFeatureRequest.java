package com.neuralforge.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ai_feature_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIFeatureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String intent;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.PENDING;

    @ElementCollection
    @CollectionTable(name = "generated_files", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "file_name")
    private List<String> generatedFiles;

    @Column(name = "generated_code", columnDefinition = "TEXT")
    private String generatedCode;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
