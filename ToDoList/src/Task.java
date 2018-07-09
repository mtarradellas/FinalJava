import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Task implements Comparable<Task>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String description;
    protected int id;
    protected LocalDate date;
    protected boolean completed;


    public Task(int id, String description, LocalDate date){
        this.id = id;
        this.description = description;
        this.completed = false;
        this.date = date;

    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        if (date != null)
            return String.format("%tD", date);
        return "";
    }

    public void complete() {
        completed = true;
    }

    public String getCompleted() {
        return completed?"  [X]":"  [  ]";
    }

    @Override
    public int compareTo(Task task) {
        return id - task.id;

    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof Task))
            return false;
        Task t = (Task) o;
        return id == t.id;

    }

    @Override
    public int hashCode() {
        return 31 * id;
    }

    @Override
    public String toString() {
        return String.format(" [%s]  %d  %15s    %s", getCompleted(), id, getDate(), description);
    }
}
