import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Leaderboard<T> {
    private final List<T> items = new ArrayList<>();
    private final Comparator<? super T> comparator;

    public Leaderboard(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    public void add(T item) {
        items.add(item);
    }

    public void addAll(List<? extends T> source) {
        items.addAll(source);
    }

    public List<T> ranking() {
        List<T> copy = new ArrayList<>(items);
        copy.sort(comparator);
        return copy;
    }
}
