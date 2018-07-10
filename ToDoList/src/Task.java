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

    public String getDateFormat() {
        if (date != null) {
            if (date.equals(LocalDate.now())) return "Today";
            else if (date.equals(LocalDate.now().minusDays(1))) return "Yesterday";
            else if (date.equals(LocalDate.now().plusDays(1))) return "Tomorrow";
            else return String.format("%td/%tm/%tY", date, date, date);
        }
        return null;
    }

    public void complete() {
        completed = !completed;
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
        return String.format("Completed: %s  Id: %d Due-Date: %s  Description: %s", completed, id, date, description);
    }
}
