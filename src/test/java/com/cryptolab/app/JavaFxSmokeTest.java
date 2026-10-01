package com.cryptolab.app;

import com.cryptolab.ui.components.LabCard;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.dashboard.DashboardView;
import com.cryptolab.ui.navigation.HeaderBar;
import com.cryptolab.ui.navigation.SidebarView;
import com.cryptolab.ui.navigation.StatusBar;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test verifying that JavaFX UI components, layout structures,
 * and CSS resources instantiate without runtime exceptions.
 *
 * Developed by Abderrazak BENAROUS
 */
class JavaFxSmokeTest {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @BeforeAll
    static void initJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timeout waiting for JavaFX platform startup");
            }
            initialized.set(true);
        } catch (IllegalStateException e) {
            // Already initialized in test JVM
            initialized.set(true);
        }
    }

    @Test
    @DisplayName("Verify UI components and views initialize cleanly on JavaFX thread")
    void testUiComponentInstantiation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        Throwable[] errorRef = new Throwable[1];

        Platform.runLater(() -> {
            try {
                StackPane contentContainer = new StackPane();
                NavigationManager nav = new NavigationManager(contentContainer);

                // Instantiate all shell components
                SidebarView sidebar = new SidebarView(nav, () -> {});
                HeaderBar header = new HeaderBar(() -> {});
                StatusBar statusBar = new StatusBar();
                DashboardView dashboard = new DashboardView(nav);

                assertThat(sidebar).isNotNull();
                assertThat(header).isNotNull();
                assertThat(statusBar).isNotNull();
                assertThat(dashboard).isNotNull();

                var classicalLab = new com.cryptolab.ui.views.classical.ClassicalLabView(nav);
                var analysisLab = new com.cryptolab.ui.views.cryptanalysis.CryptanalysisLabView();
                var modernLab = new com.cryptolab.ui.views.modern.ModernLabView();
                var signaturesLab = new com.cryptolab.ui.views.signatures.SignaturesAndHashLabView();
                var stegoLab = new com.cryptolab.ui.views.steganography.SteganographyLabView();
                var exercisesLab = new com.cryptolab.ui.views.exercises.ExercisesView();
                var historyLab = new com.cryptolab.ui.views.history.HistoryView();
                var learningLab = new com.cryptolab.ui.views.learning.LearningExplorerView(nav);
                assertThat(classicalLab).isNotNull();
                assertThat(analysisLab).isNotNull();
                assertThat(modernLab).isNotNull();
                assertThat(signaturesLab).isNotNull();
                assertThat(stegoLab).isNotNull();
                assertThat(exercisesLab).isNotNull();
                assertThat(historyLab).isNotNull();
                assertThat(learningLab).isNotNull();

                // Test CSS resource availability
                var cssUrl = getClass().getResource("/css/dark-lab.css");
                assertThat(cssUrl).isNotNull();

                success.set(true);
            } catch (Throwable t) {
                errorRef[0] = t;
            } finally {
                latch.countDown();
            }
        });

        boolean finished = latch.await(5, TimeUnit.SECONDS);
        assertThat(finished).isTrue();
        if (errorRef[0] != null) {
            throw new AssertionError("UI initialization threw exception", errorRef[0]);
        }
        assertThat(success.get()).isTrue();
    }
}
