import java.io.Serializable;
import java.util.Comparator;

public class TaskDateComparator implements Comparator<Task>, Serializable {

    //Order first by date, then id, and completed tasks always last
    @Override
    public int compare(Task t1, Task t2) {
        if (Boolean.compare(t1.completed, t2.completed) == 0) {
            if (t1.date != null && t2.date != null) {
                if (t1.date.compareTo(t2.date) == 0)
                    return t1.id - t2.id;
                return t1.date.compareTo(t2.date);
            } else {
                if (t1.date == null) return 1;
                return -1;
            }
        }
        return Boolean.compare(t1.completed, t2.completed);
    }
}
