package com.cryptolab.ui.navigation;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.model.DifficultyLevel;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 * Top application header providing breadcrumbs, global laboratory search,
 * difficulty mode selector, and quick action shortcuts.
 *
 * Developed by Abderrazak BENAROUS
 */
public class HeaderBar extends HBox {

    public HeaderBar(Runnable onOpenAbout) {
        getStyleClass().add("header-bar");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(16);

        // Breadcrumbs
        Label breadcrumbLabel = new Label();
        breadcrumbLabel.getStyleClass().add("breadcrumb-current");
        breadcrumbLabel.textProperty().bind(AppContext.getInstance().currentBreadcrumbProperty());

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Search Input
        TextField searchField = new TextField();
        searchField.setPromptText("Quick search ciphers, tools, attacks... (Ctrl+K)");
        searchField.getStyleClass().add("search-field");

        // Difficulty Selector
        Label levelLabel = new Label("Mode:");
        levelLabel.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 12px; -fx-font-weight: bold;");

        ComboBox<DifficultyLevel> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll(DifficultyLevel.values());
        difficultyCombo.setValue(AppContext.getInstance().getCurrentDifficulty());
        difficultyCombo.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #F9FAFB; -fx-font-size: 12px; -fx-border-color: #374151; -fx-border-radius: 6;");

        difficultyCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(DifficultyLevel object) {
                return object != null ? object.getLabel() : "";
            }

            @Override
            public DifficultyLevel fromString(String string) {
                return DifficultyLevel.valueOf(string.toUpperCase());
            }
        });

        difficultyCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                AppContext.getInstance().setCurrentDifficulty(newVal);
                AppContext.getInstance().setStatusMessage("Difficulty mode switched to: " + newVal.getLabel());
            }
        });

        // About Button
        Button aboutBtn = new Button();
        aboutBtn.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_INFO, 16, "#38BDF8"));
        aboutBtn.setStyle("-fx-background-color: #1F2937; -fx-border-color: #374151; -fx-border-radius: 6; -fx-padding: 6 10 6 10; -fx-cursor: hand;");
        aboutBtn.setOnAction(e -> {
            if (onOpenAbout != null) onOpenAbout.run();
        });

        getChildren().addAll(breadcrumbLabel, spacer, searchField, levelLabel, difficultyCombo, aboutBtn);
    }
}
