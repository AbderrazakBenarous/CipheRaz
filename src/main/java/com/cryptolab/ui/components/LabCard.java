package com.cryptolab.ui.components;

import com.cryptolab.core.model.AlgorithmCategory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Interactive laboratory card component displayed on the CryptoLab Dashboard.
 *
 * Developed by Abderrazak BENAROUS
 */
public class LabCard extends VBox {

    public LabCard(
            String title,
            String description,
            String iconPath,
            String accentHex,
            AlgorithmCategory category,
            List<String> featureTags,
            Runnable onAction
    ) {
        getStyleClass().add("lab-card");
        setSpacing(10);
        setAlignment(Pos.TOP_LEFT);

        // Header with Icon and Category Tag
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Node iconNode = SvgIcons.createIcon(iconPath, 22, accentHex);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("lab-card-title");

        Label tagLabel = new Label(category != null ? category.getDisplayName() : "Laboratory");
        tagLabel.getStyleClass().add("lab-card-tag");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(iconNode, titleLabel, spacer, tagLabel);

        // Description
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("lab-card-desc");
        descLabel.setWrapText(true);

        // Feature Tags / Sub-algorithms
        HBox tagsBox = new HBox(6);
        tagsBox.setAlignment(Pos.CENTER_LEFT);
        if (featureTags != null) {
            for (String tag : featureTags) {
                Label pill = new Label(tag);
                pill.setStyle("-fx-background-color: #1F2937; -fx-text-fill: #9CA3AF; -fx-font-size: 10px; -fx-padding: 2 6 2 6; -fx-background-radius: 4; -fx-border-color: #374151; -fx-border-radius: 4;");
                tagsBox.getChildren().add(pill);
            }
        }

        getChildren().addAll(header, descLabel, tagsBox);

        if (onAction != null) {
            setOnMouseClicked(e -> onAction.run());
        }
    }
}
