package com.cryptolab.app;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.ui.about.AboutDialog;
import com.cryptolab.ui.dashboard.DashboardView;
import com.cryptolab.ui.navigation.HeaderBar;
import com.cryptolab.ui.navigation.SidebarView;
import com.cryptolab.ui.navigation.StatusBar;
import com.cryptolab.ui.views.LabPlaceholderView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main JavaFX Application entry point for CryptoLab.
 *
 * Developed by Abderrazak BENAROUS
 */
public class CryptoLabApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(CryptoLabApp.class);

    private NavigationManager navigationManager;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        logger.info("Initializing CipheRaz Application Shell - Developed by Abderrazak BENAROUS");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("main-stage");

        // Content Area with View Router
        StackPane contentPane = new StackPane();
        navigationManager = new NavigationManager(contentPane);

        // Register Laboratory Views
        registerViews(contentPane);

        // Navigation Sidebar
        SidebarView sidebar = new SidebarView(navigationManager, this::openAboutDialog);
        root.setLeft(sidebar);

        // Top Header Bar
        HeaderBar header = new HeaderBar(this::openAboutDialog);
        root.setTop(header);

        // Center Content
        root.setCenter(contentPane);

        // Bottom Status Bar
        StatusBar statusBar = new StatusBar();
        root.setBottom(statusBar);

        // Initial View
        navigationManager.navigateTo(NavigationManager.ViewId.DASHBOARD);

        Scene scene = new Scene(root, 1280, 800);
        String css = getClass().getResource("/css/dark-lab.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("CipheRaz — Educational Cryptography & Steganography Laboratory [Developed by Abderrazak BENAROUS]");
        stage.setMinWidth(1080);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();

        logger.info("CipheRaz Application Window successfully displayed.");
    }

    private void registerViews(StackPane contentPane) {
        navigationManager.registerView(NavigationManager.ViewId.DASHBOARD, () -> new DashboardView(navigationManager));

        navigationManager.registerView(NavigationManager.ViewId.CLASSICAL, () -> new com.cryptolab.ui.views.classical.ClassicalLabView(navigationManager));

        navigationManager.registerView(NavigationManager.ViewId.MODERN, com.cryptolab.ui.views.modern.ModernLabView::new);

        navigationManager.registerView(NavigationManager.ViewId.CRYPTANALYSIS, com.cryptolab.ui.views.cryptanalysis.CryptanalysisLabView::new);

        navigationManager.registerView(NavigationManager.ViewId.STEGANOGRAPHY, com.cryptolab.ui.views.steganography.SteganographyLabView::new);

        navigationManager.registerView(NavigationManager.ViewId.SIGNATURES, com.cryptolab.ui.views.signatures.SignaturesAndHashLabView::new);

        navigationManager.registerView(NavigationManager.ViewId.LEARNING, () -> new com.cryptolab.ui.views.learning.LearningExplorerView(navigationManager));

        navigationManager.registerView(NavigationManager.ViewId.EXERCISES, com.cryptolab.ui.views.exercises.ExercisesView::new);

        navigationManager.registerView(NavigationManager.ViewId.HISTORY, com.cryptolab.ui.views.history.HistoryView::new);

        navigationManager.registerView(NavigationManager.ViewId.SETTINGS, () -> new LabPlaceholderView(
                "Laboratory Workstation Settings",
                AlgorithmCategory.LEARNING,
                SecurityStatus.SECURE_RECOMMENDED,
                "Configure local laboratory preferences, database storage paths, and display options.",
                "Custom settings, theme brightness, memory management, and database vacuuming."
        ));
    }

    private void openAboutDialog() {
        AboutDialog.show(primaryStage);
    }
}
