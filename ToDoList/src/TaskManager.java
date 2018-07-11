import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskManager implements Serializable {

    private static final long serialVersionUID = 1L;
    private Set<Task> taskSet;
    private int idCounter;

    public TaskManager() {
        taskSet = new TreeSet<>(new TaskDateComparator());
        idCounter = 1;
    }

    //Creates and adds task to set
    public void addTask(String desc, LocalDate date){
        taskSet.add(new Task(idCounter++, desc, date));
    }

    //Returns ArrayList of tasks
    public List<Task> getList(){
        return new ArrayList<>(taskSet);
    }

    //Returns List of tasks with due-date previous to current date
    public List<Task> getOverdueList(){
        Predicate<Task> overdue = t -> ((t.date != null) && t.date.isBefore(LocalDate.now()));
        return taskSet.stream().filter(overdue).collect(Collectors.toList());
    }

    //Returns List of tasks with due-date equal to current date
    public List<Task> getTodayList(){
        Predicate<Task> today = t -> ((t.date != null) && t.date.equals(LocalDate.now()));
        return taskSet.stream().filter(today).collect(Collectors.toList());
    }

    //Deletes task from set
    public void deleteTask(Task task){
        taskSet.removeIf(t -> t.equals(task));
    }

    //Completes task while maintaining set order
    public void completeTask(Task task){
        taskSet.removeIf(t -> t.equals(task));
        task.complete();
        taskSet.add(task);
    }

   //Returns a list of tasks including description
    public List<Task> search(String desc){
        Predicate<Task> matchDesc = t -> (t.description.toLowerCase().contains(desc.toLowerCase()));
        return taskSet.stream().filter(matchDesc).collect(Collectors.toList());
    }

    //Removes completed tasks from set
    public void archive(){
        taskSet.removeIf(t -> t.completed);
    }
}
