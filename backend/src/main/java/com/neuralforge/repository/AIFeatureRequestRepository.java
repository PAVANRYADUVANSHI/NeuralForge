package com.neuralforge.repository;

import com.neuralforge.model.AIFeatureRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIFeatureRequestRepository extends JpaRepository<AIFeatureRequest, String> {
    Page<AIFeatureRequest> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM AIFeatureRequest r WHERE r.user.id = :userId AND r.status = 'COMPLETED'")
    Long countCompletedByUserId(String userId);

    List<AIFeatureRequest> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
}
