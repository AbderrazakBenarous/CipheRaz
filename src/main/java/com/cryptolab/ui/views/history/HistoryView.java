package com.cryptolab.ui.views.history;

import com.cryptolab.app.AppContext;
import com.cryptolab.core.storage.DatabaseManager;
import com.cryptolab.core.storage.ExperimentLog;
import com.cryptolab.core.storage.ExperimentRepository;
import com.cryptolab.ui.components.SvgIcons;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Local Experiment Session History View.
 * Displays local SQLite experiment logs and provides formatted HTML laboratory report export.
 *
 * Developed by Abderrazak BENAROUS
 */
public class HistoryView extends ScrollPane {

    private final ExperimentRepository repository = new ExperimentRepository();
    private TableView<ExperimentLog> table;
    private final ObservableList<ExperimentLog> tableData = FXCollections.observableArrayList();

    public HistoryView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Experiment Session History & Report Export");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_HISTORY, 24, "#38BDF8"), title);

        Label subNotice = new Label(
                "Local SQLite Database: All experiment records are stored locally on your workstation (" +
                DatabaseManager.getDatabasePath() + "). No passwords, secret keys, or confidential payloads are ever logged."
        );
        subNotice.setWrapText(true);
        subNotice.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");

        // Action Toolbar
        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button exportBtn = new Button("Export HTML Report");
        exportBtn.getStyleClass().add("btn-primary");
        exportBtn.setOnAction(e -> exportHtmlReport());

        Button addSampleBtn = new Button("Record Demo Entry");
        addSampleBtn.getStyleClass().add("btn-secondary");
        addSampleBtn.setOnAction(e -> {
            repository.record("AES-256-GCM", "ENCRYPT", "Key=256b, Tag=128b", 48, 64, 1.42, true, "Standard verification");
            refreshTable();
            AppContext.getInstance().setStatusMessage("Recorded sample experiment entry");
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("btn-secondary");
        deleteBtn.setOnAction(e -> {
            ExperimentLog selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                repository.delete(selected.id());
                refreshTable();
                AppContext.getInstance().setStatusMessage("Deleted experiment entry #" + selected.id());
            }
        });

        Button clearBtn = new Button("Clear All History");
        clearBtn.setStyle("-fx-background-color: rgba(239, 68, 68, 0.15); -fx-text-fill: #EF4444; -fx-border-color: #EF4444; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            repository.clearAll();
            refreshTable();
            AppContext.getInstance().setStatusMessage("All experiment logs cleared");
        });

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> refreshTable());

        toolbar.getChildren().addAll(exportBtn, addSampleBtn, deleteBtn, clearBtn, spacer, refreshBtn);

        // Table
        table = new TableView<>();
        table.setItems(tableData);
        table.setPrefHeight(450);
        table.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-size: 12px;");

        TableColumn<ExperimentLog, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().id()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<ExperimentLog, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().createdAt()));
        timeCol.setPrefWidth(140);

        TableColumn<ExperimentLog, String> algCol = new TableColumn<>("Algorithm");
        algCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().algorithmName()));
        algCol.setPrefWidth(140);

        TableColumn<ExperimentLog, String> opCol = new TableColumn<>("Operation");
        opCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().operationType()));
        opCol.setPrefWidth(100);

        TableColumn<ExperimentLog, String> paramCol = new TableColumn<>("Parameters");
        paramCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().parametersSummary()));
        paramCol.setPrefWidth(180);

        TableColumn<ExperimentLog, Double> durCol = new TableColumn<>("Duration (ms)");
        durCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().durationMillis()).asObject());
        durCol.setPrefWidth(100);

        TableColumn<ExperimentLog, String> statCol = new TableColumn<>("Result");
        statCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().success() ? "PASS" : "FAIL"));
        statCol.setPrefWidth(70);

        table.getColumns().addAll(idCol, timeCol, algCol, opCol, paramCol, durCol, statCol);

        root.getChildren().addAll(header, subNotice, toolbar, table);
        setContent(root);

        // Initial load
        refreshTable();
    }

    private void refreshTable() {
        List<ExperimentLog> logs = repository.findAll(100);
        tableData.setAll(logs);
    }

    private void exportHtmlReport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Laboratory Experiment Report");
        fc.setInitialFileName("cipheraz_experiment_report.html");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Document (*.html)", "*.html"));
        File file = fc.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                String report = ExperimentRepository.generateHtmlReport(tableData, "Cybersecurity Student");
                writer.write(report);
                AppContext.getInstance().setStatusMessage("HTML Report exported successfully to: " + file.getName());
            } catch (Exception ex) {
                AppContext.getInstance().setStatusMessage("Export failed: " + ex.getMessage());
            }
        }
    }
}
