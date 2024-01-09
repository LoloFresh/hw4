package collections;

import java.util.Comparator;

/* package-private */ class NaturalOrderingComparator<T> implements Comparator<T> {
    @Override @SuppressWarnings("unchecked")
    public int compare(final T o1, final T o2) {
        return ((Comparable<? super T>) o1).compareTo(o2);
    }
}
