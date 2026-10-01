package com.cryptolab.ui.views.exercises;

import com.cryptolab.app.AppContext;
import com.cryptolab.educational.exercises.ExerciseManager;
import com.cryptolab.educational.exercises.LabExercise;
import com.cryptolab.ui.components.SvgIcons;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

/**
 * Interactive Coursework Exercises View.
 * Provides student challenges, automated grading, and instant pedagogical feedback.
 *
 * Developed by Abderrazak BENAROUS
 */
public class ExercisesView extends ScrollPane {

    private final ExerciseManager exerciseManager = new ExerciseManager();

    private ListView<LabExercise> exerciseListView;
    private Label titleLabel;
    private Label categoryLabel;
    private Label pointsLabel;
    private Label instructionLabel;
    private TextArea providedDataArea;
    private TextField submissionField;
    private Label evaluationLabel;
    private TextArea explanationArea;

    private LabExercise currentExercise;

    public ExercisesView() {
        setFitToWidth(true);
        setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        VBox root = new VBox(20);
        root.getStyleClass().add("content-area");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label hTitle = new Label("Practical Student Exercises & Coursework");
        hTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #F9FAFB;");
        header.getChildren().addAll(SvgIcons.createIcon(SvgIcons.ICON_PLAY, 24, "#06B6D4"), hTitle);

        HBox split = new HBox(20);

        // Left List: Available Exercises
        VBox leftCol = new VBox(10);
        leftCol.getStyleClass().add("lab-card");
        leftCol.setPrefWidth(320);

        Label listTitle = new Label("LABORATORY ASSIGNMENTS");
        listTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #06B6D4;");

        exerciseListView = new ListView<>();
        exerciseListView.getItems().addAll(exerciseManager.getExercises());
        exerciseListView.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F9FAFB;");
        VBox.setVgrow(exerciseListView, Priority.ALWAYS);

        exerciseListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LabExercise item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.id() + ": " + item.title() + " (" + item.points() + " pts)");
                    setStyle("-fx-text-fill: #CBD5E1; -fx-font-size: 12px;");
                }
            }
        });

        exerciseListView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) loadExercise(n);
        });

        leftCol.getChildren().addAll(listTitle, exerciseListView);

        // Right Col: Exercise Workbench
        VBox rightCol = new VBox(14);
        rightCol.getStyleClass().add("lab-card");
        HBox.setHgrow(rightCol, Priority.ALWAYS);

        HBox exHeader = new HBox(12);
        exHeader.setAlignment(Pos.CENTER_LEFT);
        titleLabel = new Label("Select an exercise from the left list");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        categoryLabel = new Label();
        categoryLabel.getStyleClass().add("lab-card-tag");

        pointsLabel = new Label();
        pointsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #F59E0B; -fx-font-size: 12px;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        exHeader.getChildren().addAll(titleLabel, categoryLabel, spacer, pointsLabel);

        instructionLabel = new Label();
        instructionLabel.setWrapText(true);
        instructionLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #CBD5E1;");

        Label dataTitle = new Label("Intercepted Ciphertext / Problem Data:");
        dataTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        providedDataArea = new TextArea();
        providedDataArea.setEditable(false);
        providedDataArea.setWrapText(true);
        providedDataArea.setPrefRowCount(2);
        providedDataArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #F59E0B; -fx-font-family: monospace;");

        Button copyDataBtn = new Button("Copy Problem Data");
        copyDataBtn.getStyleClass().add("btn-secondary");
        copyDataBtn.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(providedDataArea.getText());
            Clipboard.getSystemClipboard().setContent(cc);
            AppContext.getInstance().setStatusMessage("Problem data copied to clipboard");
        });

        // Submission Row
        Label subTitle = new Label("Your Solution / Recovered Plaintext:");
        subTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #38BDF8;");

        HBox subRow = new HBox(10);
        subRow.setAlignment(Pos.CENTER_LEFT);

        submissionField = new TextField();
        submissionField.setPromptText("Enter your calculated answer or decrypted text...");
        submissionField.setStyle("-fx-background-color: #0B0F19; -fx-text-fill: #F9FAFB; -fx-font-family: monospace; -fx-font-size: 13px;");
        HBox.setHgrow(submissionField, Priority.ALWAYS);

        Button submitBtn = new Button("Submit Answer");
        submitBtn.getStyleClass().add("btn-primary");
        submitBtn.setOnAction(e -> evaluateSubmission());

        subRow.getChildren().addAll(submissionField, submitBtn);

        evaluationLabel = new Label("Enter your answer above and click 'Submit Answer'.");
        evaluationLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");

        explanationArea = new TextArea();
        explanationArea.setPromptText("Pedagogical explanation will be revealed here upon completion...");
        explanationArea.setWrapText(true);
        explanationArea.setEditable(false);
        explanationArea.setPrefRowCount(3);
        explanationArea.setStyle("-fx-control-inner-background: #0B0F19; -fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        rightCol.getChildren().addAll(exHeader, instructionLabel, dataTitle, providedDataArea, copyDataBtn,
                subTitle, subRow, evaluationLabel, new Label("Educational Solution Trace:") {{
                    setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
                }}, explanationArea);

        split.getChildren().addAll(leftCol, rightCol);
        root.getChildren().addAll(header, split);
        setContent(root);

        // Select first exercise
        exerciseListView.getSelectionModel().select(0);
    }

    private void loadExercise(LabExercise ex) {
        this.currentExercise = ex;
        titleLabel.setText(ex.id() + ": " + ex.title());
        categoryLabel.setText(ex.category().getDisplayName());
        pointsLabel.setText(ex.points() + " Points");
        instructionLabel.setText(ex.instructions());
        providedDataArea.setText(ex.providedData());
        submissionField.clear();
        evaluationLabel.setText("Awaiting submission...");
        evaluationLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #9CA3AF;");
        explanationArea.clear();
    }

    private void evaluateSubmission() {
        if (currentExercise == null) return;
        String answer = submissionField.getText();
        boolean pass = currentExercise.checkAnswer(answer);

        if (pass) {
            evaluationLabel.setText(String.format("PASS: Correct solution! Awarded %d points.", currentExercise.points()));
            evaluationLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
            explanationArea.setText(currentExercise.educationalExplanation());
            AppContext.getInstance().setStatusMessage(String.format("Exercise %s solved successfully!", currentExercise.id()));
        } else {
            evaluationLabel.setText("INCORRECT: The submission does not match the expected solution. Check your methodology and try again.");
            evaluationLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
            explanationArea.setText("Hint: Use the specialized laboratory modules (e.g. Cryptanalysis Lab or RSA Math) to verify intermediate calculations.");
            AppContext.getInstance().setStatusMessage(String.format("Exercise %s attempt failed. Keep trying!", currentExercise.id()));
        }
    }
}
