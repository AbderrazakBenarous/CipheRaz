package com.cryptolab.ui.navigation;

import com.cryptolab.app.AppContext;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Modern footer status bar displaying operational status, memory metrics,
 * offline security indicator, and developer signature.
 *
 * Developed by Abderrazak BENAROUS
 */
public class StatusBar extends HBox {

    private final Label memoryLabel;

    public StatusBar() {
        getStyleClass().add("status-bar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(14);

        // Security / Offline Status Indicator
        Circle statusDot = new Circle(4, Color.web("#10B981"));
        Label offlineLabel = new Label("Local Offline Mode");
        offlineLabel.getStyleClass().add("status-label");
        offlineLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #10B981;");

        HBox offlineBox = new HBox(6, statusDot, offlineLabel);
        offlineBox.setAlignment(Pos.CENTER_LEFT);

        // Dynamic Status Message from AppContext
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("status-label");
        messageLabel.textProperty().bind(AppContext.getInstance().statusMessageProperty());

        // Spacer to push right-hand items
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Memory Monitor
        memoryLabel = new Label("Heap: -- MB");
        memoryLabel.getStyleClass().add("status-label");
        setupMemoryPoller();

        // Developer Signature
        Label signatureLabel = new Label("Developed by Abderrazak BENAROUS");
        signatureLabel.getStyleClass().add("author-signature");

        getChildren().addAll(offlineBox, new Label("│") {{ getStyleClass().add("status-label"); }}, messageLabel, spacer, memoryLabel, new Label("│") {{ getStyleClass().add("status-label"); }}, signatureLabel);
    }

    private void setupMemoryPoller() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long totalMemory = runtime.totalMemory() / (1024 * 1024);
            memoryLabel.setText(String.format("Heap: %d / %d MB", usedMemory, totalMemory));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
