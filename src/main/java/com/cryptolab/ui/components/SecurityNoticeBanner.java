package com.cryptolab.ui.components;

import com.cryptolab.core.model.SecurityStatus;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Educational Security Notice Banner providing clear visibility of algorithm vulnerabilities,
 * deprecations, or standard approvals.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SecurityNoticeBanner extends HBox {

    public SecurityNoticeBanner(SecurityStatus status, String customExplanation) {
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(14);
        getStyleClass().add("security-banner");

        String iconPath;
        String styleClass;

        switch (status) {
            case SECURE_RECOMMENDED -> {
                styleClass = "security-banner-info";
                iconPath = SvgIcons.ICON_SHIELD;
            }
            case DEPRECATED -> {
                styleClass = "security-banner-warning";
                iconPath = SvgIcons.ICON_ALERT;
            }
            case CLASSICAL_BROKEN -> {
                styleClass = "security-banner-danger";
                iconPath = SvgIcons.ICON_ALERT;
            }
            default -> {
                styleClass = "security-banner-info";
                iconPath = SvgIcons.ICON_INFO;
            }
        }

        getStyleClass().add(styleClass);

        Node iconNode = SvgIcons.createIcon(iconPath, 20, status.getColorHex());

        VBox textContainer = new VBox(3);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        Label titleLabel = new Label(status.getTitle());
        titleLabel.getStyleClass().add("security-banner-title");
        titleLabel.setStyle("-fx-text-fill: " + status.getColorHex() + ";");

        String explanation = customExplanation != null && !customExplanation.isBlank()
                ? customExplanation
                : status.getAdvisory();

        Label descLabel = new Label(explanation);
        descLabel.getStyleClass().add("security-banner-desc");
        descLabel.setWrapText(true);

        textContainer.getChildren().addAll(titleLabel, descLabel);
        getChildren().addAll(iconNode, textContainer);
    }
}
