import java.util.*;

/**
 * Множество на массиве
 * Неизменяемое упорядоченное множество, реализующее интерфейс SortedSet
 */
public class ArraySet<T> implements NavigableSet<T> {

    private final Comparator<? super T> comparator;
    private final List<T> arrayList;

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

    private ArraySet(List<T> view, Comparator<? super T> comparator, boolean isView) {
        this.comparator = comparator;
        this.arrayList = view;
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
        return arrayList.get(0);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        int index_end = foundIndexForValue(toElement);
        return new ArraySet<>(arrayList.subList(0, index_end), comparator, true);
    }

    @Override
    public T last() {
        if (arrayList.isEmpty()) {
            throw new NoSuchElementException("ArraySet is empty");
        }
        return arrayList.get(arrayList.size() - 1);
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement must not be greater that toElement");
        }
        int index_start = foundIndexForValue(fromElement);
        int index_end = foundIndexForValue(toElement);
        return new ArraySet<>(arrayList.subList(index_start, index_end), comparator, true);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        int index_start = foundIndexForValue(fromElement);
        return new ArraySet<>(arrayList.subList(index_start, arrayList.size()), comparator, true);
    }

    @Override
    public T removeFirst() {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public T removeLast() {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public NavigableSet<T> reversed() {
        return descendingSet();
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
    public T lower(T t) {
        int index = foundIndexForValue(t);
        return index > 0 ? arrayList.get(index - 1) : null;
    }

    @Override
    public T floor(T t) {
        int index = foundIndexForValue(t);
        if (index < arrayList.size() && compare(arrayList.get(index), t) == 0) {
            return arrayList.get(index);
        }
        return index > 0 ? arrayList.get(index - 1) : null;
    }

    @Override
    public T ceiling(T t) {
        int index = foundIndexForValue(t);
        return index < arrayList.size() ? arrayList.get(index) : null;
    }

    @Override
    public T higher(T t) {
        int index = foundIndexForValue(t);
        if (index < arrayList.size() && compare(arrayList.get(index), t) == 0) {
            index++;
        }
        return index < arrayList.size() ? arrayList.get(index) : null;
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException("ArraySet is immutable");
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException("ArraySet is immutable");
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
                throw new UnsupportedOperationException("ArraySet is immutable");
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public NavigableSet<T> descendingSet() {
        Comparator<? super T> revers = (comparator == null
                ? (Comparator<? super T>) Comparator.reverseOrder()
                : comparator.reversed());
        return new ArraySet<>(arrayList.reversed(), revers, true);
    }

    @Override
    public Iterator<T> descendingIterator() {
        return new Iterator<>() {
            private int cursor = arrayList.size() - 1;

            @Override
            public boolean hasNext() {
                return cursor >= 0;
            }

            @Override
            public T next() {
                if (cursor < 0) {
                    throw new NoSuchElementException();
                }
                return arrayList.get(cursor--);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("ArraySet is immutable");
            }
        };
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        if (compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement must not be greater that toElement");
        }

        int index_start = foundIndexForValue(fromElement);
        int index_end = foundIndexForValue(toElement);

        if (!fromInclusive && index_start < arrayList.size() && compare(arrayList.get(index_start), fromElement) == 0) {
            index_start++;
        }

        if (toInclusive && index_end < arrayList.size() && compare(arrayList.get(index_end), toElement) == 0) {
            index_end++;
        }

        if (index_start > index_end) {
            return new ArraySet<>(Collections.emptyList(), comparator, true);
        }

        return new ArraySet<>(arrayList.subList(index_start, index_end), comparator, true);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        int index_end = foundIndexForValue(toElement);
        if (inclusive && index_end < arrayList.size() && compare(arrayList.get(index_end), toElement) == 0) {
            index_end++;
        }
        return new ArraySet<>(arrayList.subList(0, index_end), comparator, true);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {

        int index_start = foundIndexForValue(fromElement);
        if (!inclusive && index_start < arrayList.size() && compare(arrayList.get(index_start), fromElement) == 0) {
            index_start++;
        }
        return new ArraySet<>(arrayList.subList(index_start, arrayList.size()), comparator, true);
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