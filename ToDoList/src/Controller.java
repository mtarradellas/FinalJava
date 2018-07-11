import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Button addButton;

    @FXML
    Button searchButton;

    @FXML
    Button archiveButton;

    @FXML
    Button listAllButton;

    @FXML
    Button listOverdueButton;

    @FXML
    Button listDueTodayButton;

    @FXML
    MenuItem saveFileButton;

    @FXML
    MenuItem loadFileButton;

    @FXML
    DatePicker datePicker;

    @FXML
    TextField descriptionForAdd;

    @FXML
    TextField descriptionForSearch;

    @FXML
    TableView<Task> tableView;

    @FXML
    Button editButton;

    @FXML
    Button deleteButton;

    @FXML
    Button completeButton;

    @FXML
    TableColumn<Task, String> taskColumn;

    @FXML
    TableColumn<Task, String> dateColumn;

    @FXML
    TableColumn<Task, String> statusColumn;


    private TaskManager taskManager;

    //Observable task list to show on table
    private ObservableList<Task> listFx = FXCollections.observableArrayList();

    //Listener to select tasks from table rows
    private final ListChangeListener<Task> TASK_SELECTOR =
            new ListChangeListener<Task>() {
                @Override
                public void onChanged(Change<? extends Task> c) {
                    setDisableButtons(false);   //Enables Edit, Delete and Complete buttons after a task is selected
                }
            };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskManager = new TaskManager();
        tableView.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());     //Allows user to edit task description directly from table
        setDisableButtons(true);        //Program starts with Edit, Delete and Complete buttons disabled
        final ObservableList<Task> taskTable = tableView.getSelectionModel().getSelectedItems();
        taskTable.addListener(TASK_SELECTOR);
    }

    //Adds task to list if not empty
    @FXML
    public void addTask(Event e){
        String desc = descriptionForAdd.getText();
        if(desc.trim().isEmpty()){
            errorAlert("Description cannot be null");
        }
        else {
            taskManager.addTask(desc, datePicker.getValue());
            refresh();
            showList(taskManager.getList());
        }
    }

    //Archives completed tasks
    @FXML
    public void archiveTasks(Event e){
        taskManager.archive();
        showList(taskManager.getList());
    }

    //Validates and creates list of tasks including computed key-words
    @FXML
    public void searchTasks(Event e){
        List<Task> l = taskManager.search(descriptionForSearch.getText());
        if(l.isEmpty()){
            informationAlert("There are no tasks matching this description");
        }
        else showList(l);
        refresh();
    }

    //Shows all created tasks on table
    @FXML
    public void showAllTasks(Event e){
        showList(taskManager.getList());
    }

    //Shows tasks with due-date previous to current date on table
    @FXML
    public void showOverdue(Event e){
        showList(taskManager.getOverdueList());
    }

    //Shows tasks with due-date equal to current date on table
    @FXML
    public void showToday(Event e){
        showList(taskManager.getTodayList());
    }

    //Deletes any text on date picker, description text box or search text box
    private void refresh(){
        datePicker.setValue(null);
        descriptionForAdd.clear();
        descriptionForSearch.clear();
    }

    //Creates observable list from computed list and shows obtained data on table
    private void showList(List<Task> l){
        taskColumn.setCellValueFactory(cellD -> new SimpleStringProperty(cellD.getValue().description));
        dateColumn.setCellValueFactory(cellID -> new SimpleObjectProperty<>(cellID.getValue().getDateFormat()));
        statusColumn.setCellValueFactory(cellID -> new SimpleObjectProperty<>(cellID.getValue().completed?"[X]":"[  ]"));
        listFx.clear();
        listFx.addAll(l);
        tableView.setItems(listFx);
    }

    //Allows user to modify task description directly from table
    public void changeTaskDescription(TableColumn.CellEditEvent editedCell) {
        Task taskSelected = tableView.getSelectionModel().getSelectedItem();
        String newDescription = editedCell.getNewValue().toString();
        if (newDescription.trim().isEmpty()) errorAlert("Description cannot be null");
        else taskSelected.setDescription(editedCell.getNewValue().toString());
        setDisableButtons(true);
        showList(taskManager.getList());
    }

    //Creates and shows task edition window
    @FXML
    public void editTask() {
        Stage stage = new Stage();
        Task selectedTask = getSelectedTask();
        if (selectedTask != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("EditPanel.fxml"));
                Parent parent = loader.load();
                ControllerEditPanel controller = loader.getController();
                controller.initController(selectedTask, taskManager);  //Gives the new window controller access to taskManager and editing task
                Scene scene = new Scene(parent);
                stage.setTitle("Edit");
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);     //Edition window appears always over previous window
                stage.initModality(Modality.APPLICATION_MODAL);     //Prevents user from accessing application window before finishing task edition
                stage.showAndWait();        //Waits until edition is finished to disable buttons and refresh tasks table
            } catch (IOException x) {
                errorAlert(x.getMessage());
            }
        }
        setDisableButtons(true);
        showList(taskManager.getList());
    }

    //Deletes selected task
    @FXML
    public void deleteTask() {
        taskManager.deleteTask(getSelectedTask());
        showList(taskManager.getList());
        setDisableButtons(true);
    }

    //Completes selected task
    @FXML
    public void completeTask(Event e) {
        Task selectedTask = getSelectedTask();
        if (selectedTask != null) {
            taskManager.completeTask(selectedTask);
            showList(taskManager.getList());
        }
        setDisableButtons(true);
    }

    //Sets Edit, Delete and Complete buttons so that they are only clickable after user selected a task
    private void setDisableButtons(boolean state) {
        deleteButton.setDisable(state);
        completeButton.setDisable(state);
        editButton.setDisable(state);
    }

    //Returns user selected task
    private Task getSelectedTask() {
        Task selectedTask = null;
        List<Task> l = tableView.getSelectionModel().getSelectedItems();
        if(l.size()==1) selectedTask = l.get(0);
        if (selectedTask == null) errorAlert("No task selected");
        return selectedTask;
    }

    //Opens confirmation alert for loading new file
    @FXML
    public void confirmLoad(Event e) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Load File");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Loading a new file will delete all unsaved data");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                chooseLoadFile();
            }
        }
    }


    //Opens file selector for loading
    private void chooseLoadFile() {
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        try{
            File toLoadFile = fileChooser.showOpenDialog(stage);
            loadFile(toLoadFile);
        }catch (Exception ex){
            errorAlert("File could not be loaded");
        }
        showList(taskManager.getList());
    }

    //Loads selected file to application
    private void loadFile(File file) {
        try{
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);
            taskManager = (TaskManager) o.readObject();
        }catch (Exception e) {
            errorAlert("File could not be loaded");
        }
    }

    //Opens file selector for saving
    @FXML
    public void chooseSaveFile(Event e){
        FileChooser fileChooser = new FileChooser();
        Stage stage = new Stage();
        try{
            File toSaveFile = fileChooser.showSaveDialog(stage);
            saveFile(toSaveFile);
        }catch (Exception ex){
            errorAlert("File could not be saved");
        }
    }

    //Saves application to file selected
    private void saveFile(File file) {
        try {
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(taskManager);
            o.close();
            f.close();
        }catch (FileNotFoundException e) {
            errorAlert("File not found");
        }catch (Exception e) {
            errorAlert("File could not be saved");
        }
    }

    //Shows Error Alert with computed message
    private void errorAlert(String message){
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.setTitle("Error");
        a.setHeaderText("");
        Button okButton = (Button) a.getDialogPane().lookupButton( ButtonType.OK);
        okButton.setText("Accept");
        a.initModality(Modality.APPLICATION_MODAL);     //Disables application until Alert is closed
        a.show();
    }

    //Shows Information Alert with computed message
    private void informationAlert(String message){
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setTitle("Message");
        a.setHeaderText("");
        Button okButton = (Button) a.getDialogPane().lookupButton( ButtonType.OK);
        okButton.setText("Accept");
        a.initModality(Modality.APPLICATION_MODAL);     //Disables application until Alert is closed
        a.show();
    }
}