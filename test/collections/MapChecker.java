package collections;

import org.junit.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MapChecker implements Map<String, String> {
    private final Map<String, String> expected;
    private final Map<String, String> actual;
    private Runnable invariantChecker = () -> {
    };

    public MapChecker(final Map<String, String> expected, final Map<String, String> actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public MapChecker invariantChecker(final Runnable invariantChecker) {
        this.invariantChecker = invariantChecker;
        return this;
    }

    private String expectedAndActual() {
        return "expected [" + expected + "] and actual [" + actual + "]";
    }

    @Override
    public String toString() {
        return "MapChecker: " + expectedAndActual();
    }

    private void assertEqualsOrdered(final String message) {
        Assert.assertEquals(message, expected, actual);
        for (final Iterator<Entry<String, String>> expectedIter = expected.entrySet().iterator(), actualIter = actual.entrySet().iterator(); expectedIter.hasNext(); ) {
            Assert.assertEquals(
                    message + " [expected order to be the same]",
                    expectedIter.next(),
                    actualIter.next()
            );
        }
    }

    public void check(final String message) {
        assertEqualsOrdered(message);
        invariantChecker.run();
    }

    @Override
    public int size() {
        final int result = expected.size();
        Assert.assertEquals("size() of " + expectedAndActual() + " are not equal", result, actual.size());
        invariantChecker.run();
        return result;
    }

    @Override
    public boolean isEmpty() {
        final boolean result = expected.isEmpty();
        Assert.assertEquals("isEmpty() of " + expectedAndActual() + " are not equal", result, actual.isEmpty());
        invariantChecker.run();
        return result;
    }

    @Override
    public boolean containsKey(final Object key) {
        final boolean result = expected.containsKey(key);
        Assert.assertEquals("containsKey(" + key + ") of " + expectedAndActual() + " are not equal", result, actual.containsKey(key));
        invariantChecker.run();
        return result;
    }

    @Override
    public boolean containsValue(final Object value) {
        final boolean result = expected.containsValue(value);
        Assert.assertEquals("containsValue(" + value + ") of " + expectedAndActual() + " are not equal", result, actual.containsValue(value));
        invariantChecker.run();
        return result;
    }

    @Override
    public String get(final Object key) {
        final String result = expected.get(key);
        Assert.assertEquals("get(" + key + ") of " + expectedAndActual() + " are not equal", result, actual.get(key));
        invariantChecker.run();
        return result;
    }

    @Override
    public String put(final String key, final String value) {
        final String expectedAndActual = expectedAndActual();
        final String result = expected.put(key, value);
        Assert.assertEquals("put(" + key + ", " + value + ") of " + expectedAndActual() + " are not equal", result, actual.put(key, value));
        assertEqualsOrdered(expectedAndActual + " are not equal after put(" + key + ", " + value + ")");
        invariantChecker.run();
        return result;
    }

    @Override
    public String remove(final Object key) {
        final String expectedAndActual = expectedAndActual();
        final String result = expected.remove(key);
        Assert.assertEquals("remove(" + key + ") of " + expectedAndActual() + " are not equal", result, actual.remove(key));
        assertEqualsOrdered(expectedAndActual + " are not equal after remove(" + key + ")");
        invariantChecker.run();
        return result;
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> m) {
        final String expectedAndActual = expectedAndActual();
        expected.putAll(m);
        actual.putAll(m);
        assertEqualsOrdered(expectedAndActual + " are not equal after putAll(" + m + ")");
        invariantChecker.run();
    }

    @Override
    public void clear() {
        final String expectedAndActual = expectedAndActual();
        expected.clear();
        actual.clear();
        assertEqualsOrdered(expectedAndActual + " are not equal after clear()");
        invariantChecker.run();
    }

    @Override
    public Set<String> keySet() {
        return null;
    }

    @Override
    public Collection<String> values() {
        return null;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return null;
    }
}
