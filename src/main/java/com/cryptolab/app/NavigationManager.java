package com.cryptolab.app;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Coordinates view transitions and updates application navigation state.
 *
 * Developed by Abderrazak BENAROUS
 */
public class NavigationManager {

    public enum ViewId {
        DASHBOARD("Dashboard"),
        CLASSICAL("Laboratories > Classical Cryptography"),
        MODERN("Laboratories > Modern Cryptography"),
        CRYPTANALYSIS("Laboratories > Cryptanalysis Lab"),
        STEGANOGRAPHY("Laboratories > Steganography & Steganalysis"),
        SIGNATURES("Laboratories > Digital Signatures"),
        LEARNING("Learning > Concepts & Algorithm Explorer"),
        EXERCISES("Learning > Practical Exercises"),
        HISTORY("Experiments > Session History"),
        SETTINGS("Application > Laboratory Settings");

        private final String breadcrumb;

        ViewId(String breadcrumb) {
            this.breadcrumb = breadcrumb;
        }

        public String getBreadcrumb() {
            return breadcrumb;
        }
    }

    private final StackPane contentContainer;
    private final Map<ViewId, Supplier<Node>> viewSuppliers = new HashMap<>();
    private final Map<ViewId, Node> cachedViews = new HashMap<>();
    private ViewId currentViewId = ViewId.DASHBOARD;
    private Runnable onNavigationChanged;

    public NavigationManager(StackPane contentContainer) {
        this.contentContainer = contentContainer;
    }

    public void registerView(ViewId id, Supplier<Node> supplier) {
        viewSuppliers.put(id, supplier);
    }

    public void navigateTo(ViewId id) {
        this.currentViewId = id;
        AppContext.getInstance().setBreadcrumb(id.getBreadcrumb());

        Node node = cachedViews.computeIfAbsent(id, k -> {
            Supplier<Node> supplier = viewSuppliers.get(k);
            return supplier != null ? supplier.get() : null;
        });

        if (node != null) {
            contentContainer.getChildren().setAll(node);
        }

        if (onNavigationChanged != null) {
            onNavigationChanged.run();
        }
    }

    public ViewId getCurrentViewId() {
        return currentViewId;
    }

    public void setOnNavigationChanged(Runnable callback) {
        this.onNavigationChanged = callback;
    }
}
