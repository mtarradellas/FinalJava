import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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


    private Task task;
    private TaskManager taskManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){}

    //Gives controller access to editing task and taskManager
    public void initController(Task task, TaskManager taskManager){
        this.task = task;
        this.taskManager = taskManager;
        completedCheckBox.setSelected(task.completed);      //Sets completed checkbox according whether task is completed or not
    }

    //Validates and implements task modifications and closes window
    @FXML
    public void acceptEdit() {
        String newDescription = editDescriptionField.getText();
        task.completed = completedCheckBox.isSelected();
        if (!newDescription.trim().isEmpty()) task.description = newDescription;
        else {      //Description remains unaltered, alert appears only if user typed blank spaces
            if (!newDescription.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Description cannot be empty");
                a.setTitle("Error");
                a.setHeaderText("Description will remain unaltered");
                Button okButton = (Button) a.getDialogPane().lookupButton(ButtonType.OK);
                okButton.setText("Accept");
                a.show();
            }
        }
        if (noDateCheckBox.isSelected()) task.date = null;
        else {
            LocalDate newDate = editDatePicker.getValue();
            if (newDate != null) task.date = newDate;       //Else Due-Date remains unaltered
        }
        finishEdit();
    }

    //Deletes selected task
    @FXML
    public void deleteTask() {
        taskManager.deleteTask(task);
        finishEdit();
    }

    //Closes edition window
    @FXML
    private void finishEdit() {
        Stage stage = (Stage) acceptButton.getScene().getWindow();
        stage.close();
    }
}
