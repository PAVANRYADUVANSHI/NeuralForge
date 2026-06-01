package com.neuralforge.service;

import com.neuralforge.ai.PredictiveBugOracle;
import com.neuralforge.model.BugPrediction;
import com.neuralforge.model.BugPredictionItem;
import com.neuralforge.model.User;
import com.neuralforge.repository.BugPredictionRepository;
import com.neuralforge.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugPredictionService {

    private final PredictiveBugOracle bugOracle;
    private final BugPredictionRepository bugPredictionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BugPrediction analyzeFile(String userId, String filePath, String fileContent) {
        User user = userRepository.findById(userId).orElseThrow();

        List<PredictiveBugOracle.BugPredictionResult> oracleResults =
                bugOracle.analyzeCode(filePath, fileContent);

        List<BugPredictionItem> items = oracleResults.stream()
                .map(r -> BugPredictionItem.builder()
                        .lineNumber(r.lineNumber())
                        .bugType(r.bugType())
                        .severity(BugPredictionItem.Severity.valueOf(r.severity()))
                        .confidenceScore(r.confidence())
                        .description(r.description())
                        .suggestedFix(r.suggestedFix())
                        .build())
                .toList();

        long criticalCount = items.stream()
                .filter(i -> i.getSeverity() == BugPredictionItem.Severity.CRITICAL)
                .count();

        BugPrediction prediction = BugPrediction.builder()
                .user(user)
                .filePath(filePath)
                .fileContent(fileContent)
                .predictions(items)
                .totalBugsFound(items.size())
                .criticalBugs((int) criticalCount)
                .build();

        items.forEach(item -> item.setPrediction(prediction));

        user.setTotalBugsPredicted(user.getTotalBugsPredicted() + items.size());
        userRepository.save(user);

        return bugPredictionRepository.save(prediction);
    }
}
