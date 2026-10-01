package com.cryptolab.core.model;

import com.cryptolab.app.AppContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests validating core model integrity, state transitions, and result wrappers.
 *
 * Developed by Abderrazak BENAROUS
 */
class CoreModelTest {

    @Test
    @DisplayName("CryptoResult ok factory should correctly populate text and step traces")
    void testCryptoResultText() {
        List<String> traces = List.of("Step 1: Parse input", "Step 2: Apply Caesar shift +3");
        CryptoResult result = CryptoResult.ok("KHOOR", "Caesar shift operation completed", traces, 1500000L);

        assertThat(result.success()).isTrue();
        assertThat(result.outputText()).isEqualTo("KHOOR");
        assertThat(result.outputBytes()).isNotEmpty();
        assertThat(result.educationalExplanation()).contains("Caesar shift");
        assertThat(result.stepTraces()).hasSize(2).contains("Step 1: Parse input");
        assertThat(result.executionDurationNanos()).isEqualTo(1500000L);
        assertThat(result.errorMessage()).isNull();
    }

    @Test
    @DisplayName("CryptoResult failure factory should record error message")
    void testCryptoResultFailure() {
        CryptoResult failure = CryptoResult.failure("Invalid key length: Expected 16, 24, or 32 bytes for AES");

        assertThat(failure.success()).isFalse();
        assertThat(failure.errorMessage()).contains("Invalid key length");
        assertThat(failure.outputText()).isNull();
        assertThat(failure.outputBytes()).isEmpty();
        assertThat(failure.stepTraces()).isEmpty();
    }

    @Test
    @DisplayName("SecurityStatus definitions must have non-empty titles and color hexes")
    void testSecurityStatusDefinitions() {
        for (SecurityStatus status : SecurityStatus.values()) {
            assertThat(status.getTitle()).isNotBlank();
            assertThat(status.getAdvisory()).isNotBlank();
            assertThat(status.getColorHex()).startsWith("#");
        }
    }

    @Test
    @DisplayName("AppContext difficulty level property transitions reactively")
    void testAppContextDifficultyTransitions() {
        AppContext context = AppContext.getInstance();
        context.setCurrentDifficulty(DifficultyLevel.ADVANCED);

        assertThat(context.getCurrentDifficulty()).isEqualTo(DifficultyLevel.ADVANCED);
        assertThat(context.currentDifficultyProperty().get()).isEqualTo(DifficultyLevel.ADVANCED);

        context.setCurrentDifficulty(DifficultyLevel.INTERMEDIATE);
        assertThat(context.getCurrentDifficulty()).isEqualTo(DifficultyLevel.INTERMEDIATE);
    }
}
