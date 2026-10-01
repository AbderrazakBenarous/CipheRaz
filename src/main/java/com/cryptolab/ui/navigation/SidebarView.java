package com.cryptolab.ui.navigation;

import com.cryptolab.app.NavigationManager;
import com.cryptolab.app.NavigationManager.ViewId;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * Modern navigation sidebar for the CryptoLab digital laboratory workstation.
 *
 * Developed by Abderrazak BENAROUS
 */
public class SidebarView extends VBox {

    private final NavigationManager navigationManager;
    private final Map<ViewId, Button> navButtons = new HashMap<>();
    private final Runnable onOpenAbout;

    public SidebarView(NavigationManager navigationManager, Runnable onOpenAbout) {
        this.navigationManager = navigationManager;
        this.onOpenAbout = onOpenAbout;
        getStyleClass().add("sidebar");
        setSpacing(6);

        // Brand Banner
        HBox brandBox = new HBox(8);
        brandBox.getStyleClass().add("brand-box");
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label();
        logoIcon.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_SHIELD, 24, "#38BDF8"));

        Label brandTitle = new Label("CipheRaz");
        brandTitle.getStyleClass().add("brand-title");

        Label badge = new Label("v1.0");
        badge.getStyleClass().add("brand-badge");

        brandBox.getChildren().addAll(logoIcon, brandTitle, badge);

        // Scrollable Navigation Items
        VBox menuContainer = new VBox(4);

        // Section: Overview
        menuContainer.getChildren().add(createSectionLabel("MAIN WORKSPACE"));
        menuContainer.getChildren().add(createNavButton(ViewId.DASHBOARD, "Dashboard", SvgIcons.ICON_DASHBOARD));

        // Section: Laboratories
        menuContainer.getChildren().add(createSectionLabel("LABORATORIES"));
        menuContainer.getChildren().add(createNavButton(ViewId.CLASSICAL, "Classical Ciphers", SvgIcons.ICON_SHIELD));
        menuContainer.getChildren().add(createNavButton(ViewId.MODERN, "Modern Cryptography", SvgIcons.ICON_LOCK));
        menuContainer.getChildren().add(createNavButton(ViewId.CRYPTANALYSIS, "Cryptanalysis Lab", SvgIcons.ICON_SEARCH));
        menuContainer.getChildren().add(createNavButton(ViewId.STEGANOGRAPHY, "Steganography", SvgIcons.ICON_EYE));
        menuContainer.getChildren().add(createNavButton(ViewId.SIGNATURES, "Digital Signatures", SvgIcons.ICON_PEN));

        // Section: Learning
        menuContainer.getChildren().add(createSectionLabel("LEARNING & PRACTICE"));
        menuContainer.getChildren().add(createNavButton(ViewId.LEARNING, "Algorithm Explorer", SvgIcons.ICON_BOOK));
        menuContainer.getChildren().add(createNavButton(ViewId.EXERCISES, "Practical Exercises", SvgIcons.ICON_PLAY));

        // Section: System
        menuContainer.getChildren().add(createSectionLabel("DATA & SYSTEM"));
        menuContainer.getChildren().add(createNavButton(ViewId.HISTORY, "Experiment History", SvgIcons.ICON_HISTORY));
        menuContainer.getChildren().add(createNavButton(ViewId.SETTINGS, "Settings", SvgIcons.ICON_SETTINGS));

        Button aboutButton = new Button("About CipheRaz");
        aboutButton.getStyleClass().add("nav-button");
        aboutButton.setMaxWidth(Double.MAX_VALUE);
        aboutButton.setGraphic(SvgIcons.createIcon(SvgIcons.ICON_INFO, 16, "#9CA3AF"));
        aboutButton.setOnAction(e -> {
            if (this.onOpenAbout != null) {
                this.onOpenAbout.run();
            }
        });
        menuContainer.getChildren().add(aboutButton);

        ScrollPane scrollPane = new ScrollPane(menuContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(brandBox, scrollPane);

        navigationManager.setOnNavigationChanged(this::updateActiveState);
        updateActiveState();
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("nav-section-label");
        return label;
    }

    private Button createNavButton(ViewId viewId, String labelText, String iconPath) {
        Button btn = new Button(labelText);
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setGraphic(SvgIcons.createIcon(iconPath, 16, "#9CA3AF"));
        btn.setOnAction(e -> navigationManager.navigateTo(viewId));

        navButtons.put(viewId, btn);
        return btn;
    }

    private void updateActiveState() {
        ViewId activeId = navigationManager.getCurrentViewId();
        for (Map.Entry<ViewId, Button> entry : navButtons.entrySet()) {
            Button btn = entry.getValue();
            boolean isActive = entry.getKey() == activeId;
            btn.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("active"), isActive);
            if (isActive) {
                if (!btn.getStyleClass().contains("active")) {
                    btn.getStyleClass().add("active");
                }
            } else {
                btn.getStyleClass().remove("active");
            }
        }
    }
}
