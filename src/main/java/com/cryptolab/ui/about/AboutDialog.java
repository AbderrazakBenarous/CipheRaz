package com.cryptolab.ui.about;

import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Professional About Dialog displaying system metadata, educational purpose,
 * architectural credits, and author attribution.
 *
 * Developed by Abderrazak BENAROUS
 */
public class AboutDialog {

    public static void show(Stage parentStage) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.setTitle("About CipheRaz");

        VBox root = new VBox(16);
        root.setStyle("-fx-background-color: #0F172A; -fx-padding: 24; -fx-border-color: #334155; -fx-border-width: 1;");
        root.setPrefWidth(520);

        // Header with Logo
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);

        Label logo = new Label();
        logo.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_SHIELD, 36, "#38BDF8"));

        VBox titleBox = new VBox(2);
        Label title = new Label("CipheRaz");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        Label subtitle = new Label("Educational Cryptography & Steganography Laboratory");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8;");

        Label version = new Label("Version 1.0.0 LTS • Java 21 • JavaFX 21");
        version.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        titleBox.getChildren().addAll(title, subtitle, version);
        header.getChildren().addAll(logo, titleBox);

        // Prominent Author Attribution
        HBox authorCard = new HBox(10);
        authorCard.setAlignment(Pos.CENTER_LEFT);
        authorCard.setStyle("-fx-background-color: rgba(56, 189, 248, 0.08); -fx-border-color: #38BDF8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12 16 12 16;");

        Label authorLabel = new Label("Developed by Abderrazak BENAROUS");
        authorLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #F8FAFC;");

        authorCard.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_PEN, 18, "#38BDF8"), authorLabel);

        // Purpose Description
        Label desc = new Label(
                "CipheRaz is an independent, modern cybersecurity educational workstation " +
                "designed for university lecture demonstrations, student coursework, and practical laboratory sessions. " +
                "It combines interactive algorithmic visualizations, step-by-step mathematical tracing, " +
                "educational cryptanalysis, and bit-level steganography inspections."
        );
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #CBD5E1; -fx-line-spacing: 3;");

        // Principles & Guarantees
        VBox specsBox = new VBox(6);
        specsBox.setStyle("-fx-background-color: #1E293B; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #334155; -fx-border-radius: 8;");

        Label spec1 = new Label("🔒 100% Offline & Private: Zero telemetry, zero external network connections.");
        Label spec2 = new Label("📐 Educational Clarity: Exposes internal state transitions, S-Boxes, and bit changes.");
        Label spec3 = new Label("⚠️ Honest Security Status: Clearly flags classical/broken ciphers vs modern standards.");
        Label spec4 = new Label("🛡️ Tech Stack: Java 21 LTS, JavaFX 21, SQLite JDBC, JUnit 5, SLF4J/Logback.");

        String specStyle = "-fx-font-size: 11px; -fx-text-fill: #94A3B8;";
        spec1.setStyle(specStyle);
        spec2.setStyle(specStyle);
        spec3.setStyle(specStyle);
        spec4.setStyle(specStyle);

        specsBox.getChildren().addAll(spec1, spec2, spec3, spec4);

        // Bottom Actions
        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_RIGHT);

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("btn-primary");
        closeBtn.setOnAction(e -> stage.close());

        bottom.getChildren().add(closeBtn);

        root.getChildren().addAll(header, authorCard, desc, specsBox, bottom);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(AboutDialog.class.getResource("/css/dark-lab.css").toExternalForm());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
