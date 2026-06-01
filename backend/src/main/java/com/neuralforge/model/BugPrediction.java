package com.neuralforge.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bug_predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_content", columnDefinition = "TEXT")
    private String fileContent;

    @OneToMany(mappedBy = "prediction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BugPredictionItem> predictions;

    @Column(name = "total_bugs_found")
    private Integer totalBugsFound;

    @Column(name = "critical_bugs")
    private Integer criticalBugs;

    @CreationTimestamp
    @Column(name = "analyzed_at", updatable = false)
    private LocalDateTime analyzedAt;
}
