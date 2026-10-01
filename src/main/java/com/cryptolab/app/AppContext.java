package com.cryptolab.app;

import com.cryptolab.core.model.DifficultyLevel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Global application context managing shared state, reactive properties,
 * and current session configurations.
 *
 * Developed by Abderrazak BENAROUS
 */
public class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private final ObjectProperty<DifficultyLevel> currentDifficulty = new SimpleObjectProperty<>(DifficultyLevel.INTERMEDIATE);
    private final StringProperty statusMessage = new SimpleStringProperty("Laboratory Ready - 100% Local & Offline");
    private final StringProperty currentBreadcrumb = new SimpleStringProperty("Dashboard");

    private AppContext() {}

    public static AppContext getInstance() {
        return INSTANCE;
    }

    public ObjectProperty<DifficultyLevel> currentDifficultyProperty() {
        return currentDifficulty;
    }

    public DifficultyLevel getCurrentDifficulty() {
        return currentDifficulty.get();
    }

    public void setCurrentDifficulty(DifficultyLevel level) {
        this.currentDifficulty.set(level);
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage.get();
    }

    public void setStatusMessage(String message) {
        this.statusMessage.set(message);
    }

    public StringProperty currentBreadcrumbProperty() {
        return currentBreadcrumb;
    }

    public void setBreadcrumb(String path) {
        this.currentBreadcrumb.set(path);
    }
}
