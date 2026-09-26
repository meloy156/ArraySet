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
    private int foundIndexForValue(Object value) {
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


    @Override
    public int size() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return arrayList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        int index = foundIndexForValue(o);
        return index < arrayList.size() && compare(arrayList.get(index), o) == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final Iterator<T> it = arrayList.iterator();

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("ArraySet id immutable");
            }
        };
    }

    @Override
    public Object[] toArray() {
        return arrayList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return arrayList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Set<?> other)) {
            return false;
        }

        if (other.size() != size()) {
            return false;
        }
        return containsAll(other);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (T element : arrayList) {
            hash += (element == null ? 0 : element.hashCode());
        }
        return hash;
    }
}