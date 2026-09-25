import java.util.*;

/**
 * Множество на массиве
 * Неизменяемое упорядоченное множество, реализующее интерфейс SortedSet
 */
public class ArraySet<T> implements SortedSet<T> {

    private final Comparator<? super T> comparator;
    private final ArrayList<T> arrayList;

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> comparator) {
        this.comparator = comparator;
        this.arrayList = new ArrayList<>(collection);
        this.arrayList.sort(comparator);
        removeDuplicates();
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Comparator<? super T> comparator) {
        this(Collections.emptyList(), comparator);
    }

    public ArraySet(Collection<? extends T> collection) {
        this(collection, null);
    }

    public ArraySet(ArraySet<T> other) {
        this.arrayList = new ArrayList<>(other.arrayList);
        this.comparator = other.comparator;
    }

    private void removeDuplicates() {
        if (arrayList.size() < 2) return;

        int write = 1;
        for (int read = 1; read < arrayList.size(); read ++) {
            if (compare(arrayList.get(read - 1), arrayList.get(read)) != 0) {
                arrayList.set(write, arrayList.get(read));
                write++;
            }
        }
        arrayList.subList(write, arrayList.size()).clear();
    }

    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b) {
        if (comparator != null) {
            return comparator.compare((T) a, (T) b);
        }

        return ((Comparable<? super T>) a).compareTo((T) b);
    }


    /**
     * Поиск индекса нужного значения
     * @param value - Объект который ищем
     * @return Index - индекс этого элемента
     */
    private int foundIndexForValue(T value) {
        int lo = 0;
        int hi = arrayList.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (compare(arrayList.get(mid), value) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }



    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public T first() {
        if (arrayList.isEmpty()) {
            throw new NoSuchElementException("ArraySet is empty");
        }
        return arrayList.getFirst();
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        Objects.requireNonNull(toElement, "Element must not be null");
        int index_end = foundIndexForValue(toElement);
        return new ArraySet<>(arrayList.subList(0, index_end), comparator);
    }

    @Override
    public T last() {
        if (arrayList.isEmpty()) {
            throw new NoSuchElementException("ArraySet is empty");
        }
        return arrayList.getLast();
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        Objects.requireNonNull(fromElement, "fromElement must not be null");
        Objects.requireNonNull(toElement, "toElement must not be null");
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement must not be greater that toElement");
        }
        int index_start = foundIndexForValue(fromElement);
        int index_end = foundIndexForValue(toElement);
        return new ArraySet<>(arrayList.subList(index_start, index_end), comparator);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        Objects.requireNonNull(fromElement, "fromElement must not be null");
        int index_start = foundIndexForValue(fromElement);
        return new ArraySet<>(arrayList.subList(index_start, arrayList.size()), comparator);
    }




}
