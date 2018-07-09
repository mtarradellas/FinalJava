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

        Predicate<Task> overdue = e -> ((e.date != null) && e.date.compareTo(LocalDate.now()) < 0);

        return taskSet.stream().filter(overdue).collect(Collectors.toList());
    }


    public List<Task> getTodayList(){

        Predicate<Task> today = e -> ((e.date != null) && e.date.compareTo(LocalDate.now()) == 0);

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


        Predicate<Task> matchDesc = e -> (e.description.toLowerCase().contains(desc.toLowerCase()));

        return taskSet.stream().filter(matchDesc).collect(Collectors.toList());

    }

    //Removes completed tasks from taskSet
    public void archive(){
        Iterator<Task> it = taskSet.iterator();

        //me estoy adelantando uno?? chequear el primero

        while (it.hasNext()){
            Task t = it.next();
            if(t.completed)
                it.remove();
        }
    }


    public void editTask(Task t, LocalDate date, String desc, boolean completed){


    }

    public void printTasks(){
        for(Task t : taskSet)
            System.out.println(t);
    }

}
