package com.neuralforge.repository;

import com.neuralforge.model.BugPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BugPredictionRepository extends JpaRepository<BugPrediction, String> {
    Page<BugPrediction> findByUserIdOrderByAnalyzedAtDesc(String userId, Pageable pageable);

    @Query("SELECT SUM(b.criticalBugs) FROM BugPrediction b WHERE b.user.id = :userId")
    Long sumCriticalBugsByUserId(String userId);
}
