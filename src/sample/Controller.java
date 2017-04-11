package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {

    @FXML
    private AnchorPane anchorPaneUp;
    @FXML
    private AnchorPane anchorPaneDown;
    @FXML
    private TextField log;
    @FXML
    private TitledPane jiraOptions;
    @FXML
    private TextField uiUserName;
    @FXML
    private PasswordField uiPassword;
    @FXML
    private TextField uiQuerry;
    @FXML
    private ComboBox uiProtocol;
    @FXML
    private TextField uiServerName;
    @FXML
    private Pane clearAllPopup;

    private static List<ActivityRecord> activityRecordList = new ArrayList<>();
    private List<String> summaryTasksList = new ArrayList<>();
    private List<Integer> summaryDurationsList = new ArrayList<>();
    private JiraRetrievedIssues jiraRetrievedIssues = new JiraRetrievedIssues();

    @FXML
    private void showClearAllPopup(){
        clearAllPopup.setVisible(true);
    }

    @FXML private void closeClearALlPopup(){
        clearAllPopup.setVisible(false);
    }

    @FXML private void clearAllRecords(){
        activityRecordList.clear();
        summaryTasksList.clear();
        summaryDurationsList.clear();
        anchorPaneUp.getChildren().clear();
        anchorPaneDown.getChildren().clear();
        clearAllPopup.setVisible(false);
        taskNamePopulated = false;
        createFirstRecord();
        createRecordNoFields(0);
    }

    @FXML
    public void getSistemStartTime(Event e) {
        int clickNo = ((MouseEvent) e).getClickCount();
        if (clickNo > 1) {
            TextField startTimeField = (TextField) e.getSource();
            int currentRecordIndex = Integer.valueOf(startTimeField.getId().replaceAll("[a-zA-Z]", ""));
            ActivityRecord currentRecord = activityRecordList.get(currentRecordIndex);
            currentRecord.setStartTime();
            startTimeField.setText(currentRecord.getStartTime().toString().substring(0, 5));
            startTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"Start time " + (currentRecordIndex + 1) + " set.");
            if (currentRecord.isComplete()) {
                System.out.println("Current object completed, refresh summary");
                log.setText(log.getText()+"     "+"Current object completed, refresh summary");
                refreshSummary();
                createNewRecord();
            }
        }
    }

    @FXML
    public void getSistemEndTime(Event e) {
        TextField endTimeField = (TextField) e.getSource();
        int clickNo = ((MouseEvent) e).getClickCount();
        if (clickNo > 1) {
            int currentRecordIndex = Integer.valueOf(endTimeField.getId().replaceAll("[a-zA-Z]", ""));
            ActivityRecord currentRecord = activityRecordList.get(currentRecordIndex);
            currentRecord.setEndTime();
            endTimeField.setText(currentRecord.getEndTime().toString().substring(0, 5));
            endTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"End time " + (currentRecordIndex + 1) + " set.");
            if (currentRecord.isComplete()) {
                refreshSummary();
                createNewRecord();
            }
        }
    }

    @FXML
    public void readTypedStartTime(Event e) {
        TextField startTimeField = (TextField) e.getSource();
        int currentRecordIndex = Integer.valueOf(startTimeField.getId().replaceAll("[a-zA-Z]", ""));
        ActivityRecord currentRecord = activityRecordList.get(currentRecordIndex);
        String text = startTimeField.getText();
        if (text.matches("\\d\\d:\\d\\d") && Integer.valueOf(text.substring(0, 2)) < 24 && Integer.valueOf(text.substring(3, 5)) < 60) {
            currentRecord.setStartTime(text + ":00");
            startTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"Start time " + (currentRecordIndex + 1) + " set.");
        } else if (text.matches("\\d\\d:\\d\\d:\\d\\d") && Integer.valueOf(text.substring(0, 2)) < 24 && Integer.valueOf(text.substring(3, 5)) < 60 & Integer.valueOf(text.substring(6, 8)) < 60) {
            currentRecord.setStartTime(text);
            startTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"Start time " + (currentRecordIndex + 1) + " set.");
        } else {
            startTimeField.clear();
            currentRecord.setStartTime(null);
        }
        if (currentRecord.isComplete()) {
            refreshSummary();
            createNewRecord();
        }
    }

    @FXML
    public void readTypedEndTime(Event e) {
        TextField endTimeField = (TextField) e.getSource();
        int currentRecordIndex = Integer.valueOf(endTimeField.getId().replaceAll("[a-zA-Z]", ""));
        ActivityRecord currentRecord = activityRecordList.get(currentRecordIndex);
        String text = endTimeField.getText();
        if (text.matches("\\d\\d:\\d\\d") && Integer.valueOf(text.substring(0, 2)) < 24 && Integer.valueOf(text.substring(3, 5)) < 60) {
            currentRecord.setEndTime(text + ":00");
            endTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"End time " + (currentRecordIndex + 1) + " set.");
        } else if (text.matches("\\d\\d:\\d\\d:\\d\\d") && Integer.valueOf(text.substring(0, 2)) < 24 && Integer.valueOf(text.substring(3, 5)) < 60 & Integer.valueOf(text.substring(6, 8)) < 60) {
            currentRecord.setEndTime(text);
            endTimeField.setAlignment(Pos.CENTER);
            log.setText(log.getText()+"     "+"End time " + (currentRecordIndex + 1) + " set.");
        } else {
            endTimeField.clear();
            currentRecord.setEndTime(null);
        }
        if (currentRecord.isComplete()) {
            refreshSummary();
            createNewRecord();
        }
    }

    @FXML
    public void getTaskName(Event e) {
        ComboBox taskNameField = (ComboBox) e.getSource();
        int currentRecordIndex = Integer.valueOf(taskNameField.getId().replaceAll("[a-zA-Z]", ""));
        ActivityRecord currentRecord = activityRecordList.get(currentRecordIndex);
        currentRecord.setTaskName((String) taskNameField.getValue());
        if (jiraRetrievedIssues.getIssues() != null) {
            FilteredList<Node> filteredFields = anchorPaneUp.getChildren().filtered(node -> node instanceof TextField && node.getId().equals("taskKey" + currentRecordIndex));
            TextField currentTaskKey = (TextField) filteredFields.get(0);
            currentTaskKey.setText(jiraRetrievedIssues.getKey(((String) taskNameField.getValue())));
        }
        log.setText(log.getText()+"     "+"Task " + (currentRecordIndex + 1) + " name set.");
        if (currentRecord.isComplete()) {
            refreshSummary();
            createNewRecord();
        }
    }

    @FXML
    public void editSummaryDuration(Event e) {
        TextField durationForEdit = (TextField) e.getSource();
        int index = Integer.valueOf(durationForEdit.getId().replaceAll("[a-zA-Z]", ""));
        summaryDurationsList.set(index, Integer.valueOf(durationForEdit.getText().replaceAll("[a-zA-Z]", "").replaceAll(" ", "")));
        log.setText(log.getText()+"     "+summaryTasksList.get(index) + " duration set to " + summaryDurationsList.get(index) + " minutes.");
        FilteredList<Node> filteredFields = anchorPaneDown.getChildren().filtered(node -> node instanceof TextField && node.getId().equals("summaryDurationHours" + index));
        TextField currentDurationHours = (TextField) filteredFields.get(0);
        currentDurationHours.setText(String.format("%.4f", (double) summaryDurationsList.get(index) / 60) + " h");
    }

    public void createFirstRecord() {
        ActivityRecord record1 = new ActivityRecord();
        activityRecordList.add(record1);
    }

    @FXML
    public void createNewRecord() {
        boolean newRecordNeeded = true;
        for (ActivityRecord activityRecord : activityRecordList) {
            if (!activityRecord.isComplete()) {
                newRecordNeeded = false;
            }
        }
        if (newRecordNeeded) {
            ActivityRecord newRecord = new ActivityRecord();
            activityRecordList.add(newRecord);
            createRecordNoFields(activityRecordList.size()-1);
        }
    }

    private void createRecordNoFields(int no) {
        Double newRowY = 41.0 + (30 * no);
        Double startTimeX = 14.0;
        Double endTimeX = 120.0;
        Double taskNameX = 341.0;
        Double timeWidth = 99.0;
        Double taskNameWidth = 231.0;
        Double taskIdX = 231.0;
        Double taskIdWidth = 99.0;

        TextField newStartTime = new TextField("");
        newStartTime.setId("startTime" + no);
        newStartTime.setOnAction(this::readTypedStartTime);
        newStartTime.setOnMouseClicked(this::getSistemStartTime);
        newStartTime.setOnKeyPressed(this::allignCenterLeft);
        newStartTime.setOnMouseEntered((event) -> clearLog());
        newStartTime.setLayoutX(startTimeX);
        newStartTime.setLayoutY(newRowY);
        newStartTime.setPrefWidth(timeWidth);
        newStartTime.setAlignment(Pos.CENTER_RIGHT);
        anchorPaneUp.getChildren().add(newStartTime);

        TextField newEndTime = new TextField();
        newEndTime.setId("endTime" + no);
        newEndTime.setOnAction(this::readTypedEndTime);
        newEndTime.setOnMouseClicked(this::getSistemEndTime);
        newEndTime.setOnKeyPressed(this::allignCenterLeft);
        newEndTime.setOnMouseEntered((event) -> clearLog());
        newEndTime.setLayoutX(endTimeX);
        newEndTime.setLayoutY(newRowY);
        newEndTime.setPrefWidth(timeWidth);
        newEndTime.setAlignment(Pos.CENTER_RIGHT);
        anchorPaneUp.getChildren().add(newEndTime);

        ComboBox newTaskName = new ComboBox();
        newTaskName.setId("taskName" + no);
        newTaskName.setOnAction(this::getTaskName);
        newTaskName.setOnKeyPressed(this::allignCenterLeft);
        newTaskName.setOnMouseEntered((event) -> clearLog());
        newTaskName.setLayoutX(taskNameX);
        newTaskName.setLayoutY(newRowY);
        newTaskName.setPrefWidth(taskNameWidth);
        newTaskName.setEditable(true);
        System.out.println(newTaskName.getId() + " populated");
        anchorPaneUp.getChildren().add(newTaskName);
        populateAllTaskNames();

        TextField newTaskKey = new TextField();
        newTaskKey.setId("taskKey" + no);
        newTaskKey.setOnMouseEntered((event) -> clearLog());
        newTaskKey.setLayoutX(taskIdX);
        newTaskKey.setLayoutY(newRowY);
        newTaskKey.setPrefWidth(taskIdWidth);
        newTaskKey.setAlignment(Pos.CENTER);
        newTaskKey.setEditable(false);
        anchorPaneUp.getChildren().add(newTaskKey);

        anchorPaneUp.setPrefHeight(newRowY + 30);
    }

    @FXML
    public void refreshSummary() {
        computeSummaryLists();
        anchorPaneDown.getChildren().clear();
        int tasksNo = summaryTasksList.size();
        for (int i = 0; i < tasksNo; i++) {
            TextField summaryTask = new TextField("");
            summaryTask.setText(summaryTasksList.get(i));
            summaryTask.setId("summaryTask" + i);
            summaryTask.setAlignment(Pos.CENTER);
            summaryTask.setEditable(false);
            summaryTask.setPrefHeight(25);
            summaryTask.setPrefWidth(150);
            summaryTask.setLayoutY(24);
            summaryTask.setLayoutX(25 + 130 * i);
            anchorPaneDown.getChildren().add(summaryTask);

            TextField summaryDurationMinutes = new TextField("");
            summaryDurationMinutes.setText(summaryDurationsList.get(i).toString() + " min");
            summaryDurationMinutes.setId("summaryDurationMinutes" + i);
            summaryDurationMinutes.setAlignment(Pos.CENTER);
            summaryDurationMinutes.setOnAction(this::editSummaryDuration);
            summaryDurationMinutes.setPrefHeight(25);
            summaryDurationMinutes.setPrefWidth(150);
            summaryDurationMinutes.setLayoutY(47);
            summaryDurationMinutes.setLayoutX(25 + 130 * i);
            anchorPaneDown.getChildren().add(summaryDurationMinutes);

            TextField summaryDurationHours = new TextField("");
            double durationHours = (double) summaryDurationsList.get(i) / 60;
            summaryDurationHours.setText(String.format("%.4f", durationHours) + " h");
            summaryDurationHours.setId("summaryDurationHours" + i);
            summaryDurationHours.setAlignment(Pos.CENTER);
            summaryDurationHours.setEditable(false);
            summaryDurationHours.setPrefHeight(25);
            summaryDurationHours.setPrefWidth(150);
            summaryDurationHours.setLayoutY(70);
            summaryDurationHours.setLayoutX(25 + 130 * i);
            anchorPaneDown.getChildren().add(summaryDurationHours);

            anchorPaneDown.setPrefWidth(50 + 130 * (i + 1));
        }
    }

    private void computeSummaryLists() {
        summaryTasksList = new ArrayList<>();
        summaryDurationsList = new ArrayList<>();
        for (ActivityRecord record : activityRecordList) {
            if (record.isComplete()) {
                if (summaryTasksList.contains(record.getTaskName())) {
                    int currentElement = summaryTasksList.indexOf(record.getTaskName());
                    int currentElementDuration = summaryDurationsList.get(currentElement);
                    currentElementDuration += record.getDurationMinutes();
                    summaryDurationsList.set(currentElement, currentElementDuration);
                } else {
                    summaryTasksList.add(record.getTaskName());
                    summaryDurationsList.add(record.getDurationMinutes());
                }
            }
        }
    }

    @FXML
    public void allignCenterLeft(Event e) {
        TextField field = (TextField) e.getSource();
        field.setAlignment(Pos.CENTER_RIGHT);
    }

    @FXML
    public void logWorkToJira() {
        boolean succes = false;
        if (jiraRetrievedIssues.getIssues() != null) {
            int count = 0;
            for (int i = 0; i < summaryTasksList.size(); i++) {
                String taskKey = jiraRetrievedIssues.getKey(summaryTasksList.get(i));
                int minutes = summaryDurationsList.get(i);

                if (taskKey != null) {
                    try {
                        succes = HttpRequest.doLogWorkRequest(taskKey, minutes);
                        if (succes) {
                            count++;
                        } else {
                            log.setText(log.getText()+"     "+"Could not log " + minutes + " minutes for " + taskKey);
                            System.out.println("Could not log " + minutes + " minutes for " + taskKey);
                            break;
                        }
                    } catch (Throwable throwable) {
                        log.setText(log.getText()+"     "+"Exception! Could not log " + minutes + " minutes for " + taskKey);
                        System.out.println("Exception! Could not log " + minutes + " minutes for " + taskKey);
                        System.out.println(Arrays.toString(throwable.getStackTrace()));
                        break;
                    }
                }
            }
            if (succes) {
                log.setText(log.getText()+"     "+"Successfully logged time for " + count + " tasks.");
            }
        } else log.setText(log.getText()+"     "+"No issues to log in jira!");
    }

    @FXML
    public void clearLog() {
        log.clear();
    }

    private boolean taskNamePopulated = false;

    @FXML
    public void populateTaskName0() {
        if (!taskNamePopulated) {
            readJiraOptions();
            populateAllTaskNames();
            System.out.println("Task names populated.");
            taskNamePopulated = true;
        }
    }

    private ObservableList combineLists(ObservableList<String> observableList, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!observableList.contains(list.get(i))) {
                observableList.add(list.get(i));
            }
        }
        return observableList;
    }

    @FXML
    public void showHideOptions() {
//        if the panel is expanded will return false .
        boolean expanded = jiraOptions.isExpanded();
        if (expanded) {
            jiraOptions.setExpanded(true);
            jiraOptions.setPrefHeight(230);
        } else closeOptions();
    }

    @FXML
    public void hideOptions() {
        if (!jiraOptions.isExpanded()) jiraOptions.setPrefHeight(25);
    }

    @FXML
    public void closeOptions() {
        jiraOptions.setExpanded(false);
    }

    @FXML
    public void saveOptions() {
        readJiraOptions();
        jiraOptions.setExpanded(false);
    }

    @FXML
    private void readJiraOptions() {
        HttpRequest.setJiraProtocol(uiProtocol.getValue().toString());
        HttpRequest.setJiraServerName(uiServerName.getText());
        HttpRequest.setJiraUserName(uiUserName.getText());
        HttpRequest.setJiraPassword(uiPassword.getText());
        HttpRequest.setJiraQuerry(uiQuerry.getText());
        boolean successLogin = false;
        try {
            successLogin = HttpRequest.doJiraLoginRequest();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        if (successLogin) {
            log.setText(log.getText()+"     "+"Successfully log in to jira");
            try {
                jiraRetrievedIssues = HttpRequest.getMyIssues();
                log.setText(log.getText()+"     "+"Retrieved " + jiraRetrievedIssues.getIssues().size() + " issues from jira.");
            } catch (Throwable throwable) {
                log.setText(log.getText()+"     "+"Could not retrieve issues from jira! Check the query!");
                throwable.printStackTrace();
            }
            populateAllTaskNames();
        } else {
            log.setText(log.getText()+"     "+"Cannot connect to jira server using given credentials and configs!");
        }
    }

    @FXML
    private void populateAllTaskNames() {
        FilteredList<Node> filteredFields = anchorPaneUp.getChildren().filtered(node -> node instanceof ComboBox && node.getId().contains("taskName"));
        if (jiraRetrievedIssues.getIssues() != null) {
            ObservableList<String> issuesSummary = FXCollections.observableArrayList();
            for (int i = 0; i < jiraRetrievedIssues.getIssues().size(); i++) {
                issuesSummary.add(jiraRetrievedIssues.getIssues().get(i).getSummary());
            }
            for (Node filteredField : filteredFields) {
                ComboBox currentTask = (ComboBox) filteredField;
                currentTask.setItems(combineLists(issuesSummary, summaryTasksList));
            }
        } else {
            log.setText(log.getText()+"     "+"No jira issues available!");
            for (Node filteredField : filteredFields) {
                ComboBox currentTask = (ComboBox) filteredField;
                ObservableList issuesSummary = FXCollections.observableArrayList(summaryTasksList);
                currentTask.setItems(issuesSummary);
            }
        }
    }
}
