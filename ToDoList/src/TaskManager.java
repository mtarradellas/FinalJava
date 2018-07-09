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



    public void addTask(String desc, LocalDate date){
        taskSet.add(new Task(idCounter++, desc, date));
    }


    public List<Task> getList(){
        return new ArrayList<>(taskSet);
    }


    public List<Task> getOverdueList(){
        Predicate<Task> overdue = t -> ((t.date != null) && t.date.isBefore(LocalDate.now()));
        return taskSet.stream().filter(overdue).collect(Collectors.toList());
    }


    public List<Task> getTodayList(){
        Predicate<Task> today = t -> ((t.date != null) && t.date.equals(LocalDate.now()));
        return taskSet.stream().filter(today).collect(Collectors.toList());
    }

    //Self explanatory
    public void deleteTask(Task task){
        taskSet.removeIf(t -> t.equals(task));
    }

    //removes task from set, and adds it again as the same task but completed
    public void completeTask(Task task){
        taskSet.removeIf(t -> t.equals(task));
        task.complete();
        taskSet.add(task);
    }

   //returns a list of tasks with matching description
    public List<Task> search(String desc){
        Predicate<Task> matchDesc = t -> (t.description.toLowerCase().contains(desc.toLowerCase()));
        return taskSet.stream().filter(matchDesc).collect(Collectors.toList());
    }

    //Removes completed tasks from taskSet
    public void archive(){
        taskSet.removeIf(t -> t.completed);
    }

    public void printTasks(){
        for(Task t : taskSet)
            System.out.println(t);
    }

}
