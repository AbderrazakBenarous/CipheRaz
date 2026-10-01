package com.cryptolab.ui.views;

import com.cryptolab.core.model.AlgorithmCategory;
import com.cryptolab.core.model.SecurityStatus;
import com.cryptolab.ui.components.SecurityNoticeBanner;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Clean laboratory workstation placeholder used during incremental phase delivery.
 *
 * Developed by Abderrazak BENAROUS
 */
public class LabPlaceholderView extends VBox {

    public LabPlaceholderView(String labTitle, AlgorithmCategory category, SecurityStatus status, String statusNotice, String details) {
        getStyleClass().add("content-area");
        setSpacing(18);

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(labTitle);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");

        Label catTag = new Label(category.getDisplayName());
        catTag.getStyleClass().add("lab-card-tag");

        header.getChildren().addAll(title, catTag);

        // Security Notice
        SecurityNoticeBanner banner = new SecurityNoticeBanner(status, statusNotice);

        // Workspace Card
        VBox card = new VBox(14);
        card.getStyleClass().add("lab-card");
        VBox.setVgrow(card, Priority.ALWAYS);

        HBox promptBox = new HBox(12);
        promptBox.setAlignment(Pos.CENTER_LEFT);
        promptBox.getChildren().addAll(
                SvgIcons.createIcon(SvgIcons.ICON_CPU, 22, "#38BDF8"),
                new Label(details) {{
                    setStyle("-fx-font-size: 13px; -fx-text-fill: #CBD5E1; -fx-wrap-text: true;");
                }}
        );

        card.getChildren().add(promptBox);

        getChildren().addAll(header, banner, card);
    }
}
