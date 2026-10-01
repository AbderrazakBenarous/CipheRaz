package com.cryptolab.core.storage;

import com.cryptolab.educational.exercises.ExerciseManager;
import com.cryptolab.educational.exercises.LabExercise;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests validating SQLite experiment logging, HTML report export,
 * and coursework exercise verification.
 *
 * Developed by Abderrazak BENAROUS
 */
class StorageAndExercisesTest {

    @Test
    @DisplayName("ExperimentRepository: record, retrieve, and generate HTML report from SQLite")
    void testExperimentRepository() {
        ExperimentRepository repo = new ExperimentRepository();
        repo.clearAll();

        repo.record("AES-256-GCM", "ENCRYPT", "Key=256b, Tag=128b", 128, 144, 2.5, true, "Unit test entry");
        repo.record("RSA-2048", "SIGN", "SHA256withRSA", 64, 256, 12.1, true, "Signature test entry");

        List<ExperimentLog> logs = repo.findAll(10);
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).algorithmName()).isIn("AES-256-GCM", "RSA-2048");

        String html = ExperimentRepository.generateHtmlReport(logs, "Test Student");
        assertThat(html).contains("CipheRaz — Laboratory Experiment Report")
                .contains("AES-256-GCM")
                .contains("RSA-2048")
                .contains("Developed by Abderrazak BENAROUS");

        // Clean up
        repo.clearAll();
        assertThat(repo.findAll(10)).isEmpty();
    }

    @Test
    @DisplayName("ExerciseManager: preloaded challenges and automated answer verification")
    void testExerciseManager() {
        ExerciseManager manager = new ExerciseManager();
        List<LabExercise> exercises = manager.getExercises();
        assertThat(exercises).hasSizeGreaterThanOrEqualTo(5);

        // Test Exercise 1: Caesar challenge
        LabExercise ex1 = manager.findById("EX-01");
        assertThat(ex1).isNotNull();
        assertThat(ex1.checkAnswer("THIS IS A SECRET CAESAR MESSAGE")).isTrue();
        assertThat(ex1.checkAnswer("this is a secret caesar message")).isTrue(); // Case insensitive
        assertThat(ex1.checkAnswer("Wrong Answer")).isFalse();

        // Test Exercise 2: Atbash CipheRaz challenge
        LabExercise ex2 = manager.findById("EX-02");
        assertThat(ex2).isNotNull();
        assertThat(ex2.checkAnswer("CIPHERAZ")).isTrue();
        assertThat(ex2.checkAnswer("cipheraz")).isTrue();
        assertThat(ex2.checkAnswer("Wrong")).isFalse();

        // Test Exercise 3: RSA totient & private exponent
        LabExercise ex3 = manager.findById("EX-03");
        assertThat(ex3).isNotNull();
        assertThat(ex3.checkAnswer("2753")).isTrue();
        assertThat(ex3.checkAnswer("9999")).isFalse();
    }
}
