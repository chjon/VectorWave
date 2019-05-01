import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AsyncPriorityQueue<T> extends PriorityQueue<T> {
    private volatile boolean locked;

    AsyncPriorityQueue(Comparator<? super T> comparator) {
        super(comparator);
    }

    @Override
    public boolean add(T t) {
        while (locked);
        locked = true;
        final boolean toReturn =  super.add(t);
        locked = false;
        return toReturn;
    }

    @Override
    public T peek() {
        while (locked);
        locked = true;
        final T toReturn = super.peek();
        locked = false;
        return toReturn;
    }

    @Override
    public boolean remove(Object o) {
        while (locked);
        locked = true;
        final boolean toReturn = super.remove(o);
        locked = false;
        return toReturn;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        while (locked);
        locked = true;
        final boolean toReturn = super.addAll(c);
        locked = false;
        return toReturn;
    }

    public boolean addAllTo(Collection<? super T> c) {
        while (locked);
        locked = true;
        final boolean toReturn = c.addAll(this);
        locked = false;
        return toReturn;
    }

    @Override
    public void clear() {
        while (locked);
        locked = true;
        super.clear();
        locked = false;
    }
}
