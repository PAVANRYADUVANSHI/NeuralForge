package com.neuralforge.repository;

import com.neuralforge.model.CodeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeDecisionRepository extends JpaRepository<CodeDecision, String> {
    List<CodeDecision> findByUserIdOrderByCreatedAtDesc(String userId);
    List<CodeDecision> findByTagsContainingIgnoreCase(String tag);
}
