package com.cryptolab.educational.exercises;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.DifficultyLevel;

/**
 * Model representing a university laboratory assignment or student challenge.
 *
 * Developed by Abderrazak BENAROUS
 */
public record LabExercise(
        String id,
        String title,
        AlgorithmCategory category,
        DifficultyLevel difficulty,
        String instructions,
        String providedData,
        String expectedAnswerPattern,
        String educationalExplanation,
        int points
) {
    public boolean checkAnswer(String studentSubmission) {
        if (studentSubmission == null || studentSubmission.trim().isEmpty()) {
            return false;
        }
        String cleanSub = studentSubmission.trim().toUpperCase().replaceAll("\\s+", " ");
        String cleanExp = expectedAnswerPattern.trim().toUpperCase().replaceAll("\\s+", " ");
        return cleanSub.contains(cleanExp) || cleanSub.equals(cleanExp);
    }
}
