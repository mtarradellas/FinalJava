import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ControllerEditPanel implements Initializable {


    @FXML
    Button deleteButton;

    @FXML
    TextField editDescriptionField;

    @FXML
    DatePicker editDatePicker;

    @FXML
    CheckBox noDateCheckBox;

    @FXML
    CheckBox completedCheckBox;

    @FXML
    Button acceptButton;

    @FXML
    Button cancelButton;

    @FXML

    private Task task;
    private TaskManager taskManager;
    private Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void initTask(Task task, TaskManager taskManager, Controller controller){
        this.task = task;
        this.taskManager = taskManager;
        this.controller = controller;
    }


    public void complete() {
        taskManager.completeTask(task);
        //taskManager.printTasks();
    }

    @FXML
    public void acceptEdit() {

        String newDescription = editDescriptionField.getText();
        if (completedCheckBox.isSelected()) complete();
        if (!newDescription.trim().isEmpty()) task.description = newDescription;
        if (noDateCheckBox.isSelected()) task.date = null;
        else {
            LocalDate newDate = editDatePicker.getValue();
            if (newDate != null) task.date = newDate;
        }

        refresh();
        Stage stage = (Stage) acceptButton.getScene().getWindow();
        stage.close();
    }

    private void refresh() {
        controller.showList(taskManager.getList());
        controller.editButton.setDisable(true);
    }

    @FXML
    public void deleteTask() {
        taskManager.deleteTask(task);
        refresh();
        cancelEdit();
    }

    @FXML
    public void cancelEdit() {
        Stage stage = (Stage) acceptButton.getScene().getWindow();
        stage.close();
    }
}