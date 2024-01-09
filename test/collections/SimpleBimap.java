package collections;

import java.util.*;
import java.util.function.Supplier;

public class SimpleBimap implements Bimap {
    private final Map<String, String> left;
    private final Map<String, String> right;

    public SimpleBimap() {
        left = new TreeMap<>();
        right = new TreeMap<>();
    }

    public SimpleBimap(final Supplier<? extends Map<String, String>> mapGenerator) {
        left = mapGenerator.get();
        right = mapGenerator.get();
    }

    public SimpleBimap(final Comparator<? super String> leftCmp, final Comparator<? super String> rightCmp) {
        left = new TreeMap<>(leftCmp);
        right = new TreeMap<>(rightCmp);
    }

    @Override
    public int size() {
        assert left.size() == right.size();
        return left.size();
    }

    @Override
    public boolean isEmpty() {
        assert left.isEmpty() == right.isEmpty();
        return left.isEmpty();
    }

    @Override
    public void put(final String left, final String right) {
        leftRemove(left);
        rightRemove(right);
        this.left.put(left, right);
        this.right.put(right, left);
    }

    @Override
    public String leftRemove(final String left) {
        if (this.left.containsKey(left)) {
            final String right = this.left.remove(left);
            this.right.remove(right);
            return right;
        } else {
            return null;
        }
    }

    @Override
    public String rightRemove(final String right) {
        if (this.right.containsKey(right)) {
            final String left = this.right.remove(right);
            this.left.remove(left);
            return left;
        } else {
            return null;
        }
    }

    @Override
    public void putAll(final Bimap other) {
        other.left().forEach(this::put);
    }

    @Override
    public void clear() {
        left.clear();
        right.clear();
    }

    @Override
    public Map<String, String> left() {
        return new AbstractMap<>() {
            @Override
            public Set<Entry<String, String>> entrySet() {
                return new AbstractSet<>() {
                    @Override
                    public Iterator<Entry<String, String>> iterator() {
                        return new Iterator<>() {
                            private final Iterator<Entry<String, String>> leftIter = left.entrySet().iterator();
                            private Entry<String, String> lastRet = null;

                            @Override
                            public boolean hasNext() {
                                return leftIter.hasNext();
                            }

                            @Override
                            public Entry<String, String> next() {
                                return lastRet = leftIter.next();
                            }

                            @Override
                            public void remove() {
                                leftIter.remove();
                                right.remove(lastRet.getValue());
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return SimpleBimap.this.size();
                    }

                    @Override
                    public boolean contains(final Object o) {
                        @SuppressWarnings("unchecked")
                        final Entry<String, String> entry = (Entry<String, String>) o;
                        final Comparator<? super String> cmp = ((SortedMap<String, String>) right).comparator() == null ? new NaturalOrderingComparator<>() : ((SortedMap<String, String>) right).comparator();
                        return left.containsKey(entry.getKey()) && cmp.compare(left.get(entry.getKey()), entry.getValue()) == 0;
                    }

                    @Override
                    public void clear() {
                        SimpleBimap.this.clear();
                    }

                    @Override
                    public boolean remove(final Object o) {
                        if (contains(o)) {
                            @SuppressWarnings("unchecked")
                            final Entry<String, String> entry = (Entry<String, String>) o;
                            left.remove(entry.getKey());
                            right.remove(entry.getValue());
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }
        };
    }

    @Override
    public Map<String, String> right() {
        return new AbstractMap<>() {
            @Override
            public Set<Entry<String, String>> entrySet() {
                return new AbstractSet<>() {
                    @Override
                    public Iterator<Entry<String, String>> iterator() {
                        return new Iterator<>() {
                            private final Iterator<Entry<String, String>> rightIter = right.entrySet().iterator();
                            private Entry<String, String> lastRet = null;

                            @Override
                            public boolean hasNext() {
                                return rightIter.hasNext();
                            }

                            @Override
                            public Entry<String, String> next() {
                                return lastRet = rightIter.next();
                            }

                            @Override
                            public void remove() {
                                rightIter.remove();
                                left.remove(lastRet.getValue());
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return SimpleBimap.this.size();
                    }

                    @Override
                    public boolean contains(final Object o) {
                        @SuppressWarnings("unchecked")
                        final Entry<String, String> entry = (Entry<String, String>) o;
                        final Comparator<? super String> cmp = ((SortedMap<String, String>) left).comparator() == null ? new NaturalOrderingComparator<>() : ((SortedMap<String, String>) left).comparator();
                        return right.containsKey(entry.getKey()) && cmp.compare(right.get(entry.getKey()), entry.getValue()) == 0;
                    }

                    @Override
                    public void clear() {
                        SimpleBimap.this.clear();
                    }

                    @Override
                    public boolean remove(final Object o) {
                        if (contains(o)) {
                            @SuppressWarnings("unchecked")
                            final Entry<String, String> entry = (Entry<String, String>) o;
                            right.remove(entry.getKey());
                            left.remove(entry.getValue());
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }
        };
    }

    @Override
    public String toString() {
        return "SimpleBimap{" + "left=" + left + ", right=" + right + '}';
    }
}
