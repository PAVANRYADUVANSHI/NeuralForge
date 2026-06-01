package com.neuralforge.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bug_prediction_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugPredictionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_id")
    private BugPrediction prediction;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "bug_type")
    private String bugType;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "suggested_fix", columnDefinition = "TEXT")
    private String suggestedFix;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
