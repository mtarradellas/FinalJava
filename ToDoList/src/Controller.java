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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    Button addButton;

    @FXML
    Button searchButton;

    @FXML
    Button archiveButton;

    @FXML
    Button listAll;

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
    TableColumn<Task, String> taskColumn;

    @FXML
    TableColumn<Task, String> dateColumn;

    @FXML
    TableColumn<Task, String> statusColumn;

    @FXML
    TableColumn<Task, Integer> idColumn;



    private TaskManager taskManager;

    private ObservableList<Task> listFx = FXCollections.observableArrayList();

    private final ListChangeListener<Task> taskSelector =
            new ListChangeListener<Task>() {
                @Override
                public void onChanged(Change<? extends Task> c) {
                    editButton.setDisable(false);
                }
            };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskManager = new TaskManager();
        tableView.setEditable(true);
        taskColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        editButton.setDisable(true);

        final ObservableList<Task> taskTable = tableView.getSelectionModel().getSelectedItems();
        taskTable.addListener(taskSelector);
    }

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

    @FXML
    public void archiveTasks(Event e){

        taskManager.archive();

        showList(taskManager.getList());

    }

    @FXML
    public void searchTasks(Event e){

        List<Task> l = taskManager.search(descriptionForSearch.getText());

        if(l.isEmpty()){
            informationAlert("There are no tasks matching this description");

        }
        else showList(l);
        refresh();
    }

    @FXML
    public void showAllTasks(Event e){

        showList(taskManager.getList());
        refresh();
    }

    @FXML
    public void showOverdue(Event e){

        showList(taskManager.getOverdueList());

    }

    @FXML
    public void showToday(Event e){

        showList(taskManager.getTodayList());
    }

    private void refresh(){

        datePicker.setValue(null);
        descriptionForAdd.clear();
        descriptionForSearch.clear();
    }

    protected void showList(List<Task> l){

        taskColumn.setCellValueFactory(cellD -> new SimpleStringProperty(cellD.getValue().description));
        idColumn.setCellValueFactory(cellID -> new SimpleObjectProperty<>(cellID.getValue().id));
        dateColumn.setCellValueFactory(cellID -> new SimpleObjectProperty<>(cellID.getValue().getDate()));
        statusColumn.setCellValueFactory(cellID -> new SimpleObjectProperty<>(cellID.getValue().getCompleted()));


        listFx.clear();

        listFx.addAll(l);

        tableView.setItems(listFx);
    }

    public void changeTaskDescription(TableColumn.CellEditEvent editedCell) {

        Task taskSelected = tableView.getSelectionModel().getSelectedItem();
        String newDescription = editedCell.getNewValue().toString();
        if (newDescription.trim().isEmpty()) errorAlert("Description cannot be null");
        else taskSelected.setDescription(editedCell.getNewValue().toString());
    }

    @FXML
    public void editTask() {


            Stage stage = new Stage();
            try {

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("EditPanel.fxml"));
                Parent parent = loader.load();

                Scene scene = new Scene(parent);

                ControllerEditPanel controller = loader.getController();

                controller.initTask(getSelectedTask(), taskManager, this);


                stage.setTitle("To-Do");
                stage.setScene(scene);
                stage.show();
            }catch (IOException x){}

    }

    private Task getSelectedTask() {
        if(tableView != null) {
            List<Task> l = tableView.getSelectionModel().getSelectedItems();
            if(l.size()==1) return l.get(0);
        }
        return null;
    }

    @FXML
    public void saveFile(Event event){

        FileChooser fileChooser = new FileChooser();
        Stage stage2 = new Stage();
        try{

            File toSaveFile = fileChooser.showSaveDialog(stage2);

            saveFile(toSaveFile);

        }catch (Exception e){

            errorAlert("File could not be saved");

        }
    }

    @FXML
    private void loadFile(Event event) {
        FileChooser fileChooser = new FileChooser();
        Stage stage2 = new Stage();

        try{

            File toLoadFile = fileChooser.showOpenDialog(stage2);

            loadFile(toLoadFile);

        }catch (Exception e){

            errorAlert("File could not be loaded");
        }

        showList(taskManager.getList());
    }

    private void loadFile(File file) {
        try{

            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);

            taskManager = (TaskManager) o.readObject();


        }catch (Exception e) {

            errorAlert("File could not be loaded");
        }
    }

    private void saveFile(File file) {
        try {

            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(taskManager);

            o.close();
            f.close();

        }catch (FileNotFoundException e) {
            errorAlert("File not found");
        }catch (IOException e) {
            errorAlert("File could not be saved");
        }
    }

    private void errorAlert(String message){
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.setTitle("Error");
        a.setHeaderText("");
        Button okButton = (Button) a.getDialogPane().lookupButton( ButtonType.OK);
        okButton.setText("Accept");
        a.show();
    }

    private void informationAlert(String message){
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setTitle("Message");
        a.setHeaderText("");
        Button okButton = (Button) a.getDialogPane().lookupButton( ButtonType.OK);
        okButton.setText("Accept");
        a.show();
    }
}