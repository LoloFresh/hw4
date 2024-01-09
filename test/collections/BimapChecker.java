package collections;

import org.junit.Assert;

import java.util.Iterator;
import java.util.Map;

public class BimapChecker implements Bimap {
    private final Bimap expected;
    private final Bimap actual;
    private Runnable invariantChecker = () -> {
    };

    public BimapChecker(final Bimap expected, final Bimap actual) {
        this.expected = expected;
        this.actual = actual;
    }

    public BimapChecker invariantChecker(final Runnable invariantChecker) {
        this.invariantChecker = invariantChecker;
        return this;
    }

    @Override
    public String toString() {
        return "BimapChecker: " + expectedAndActual();
    }

    private void assertEqualsOrdered(final String message) {
        Assert.assertEquals(message, expected.left(), actual.left());
        Assert.assertEquals(message, expected.right(), actual.right());
        for (final Iterator<Map.Entry<String, String>> expectedIter = expected.left().entrySet().iterator(), actualIter = actual.left().entrySet().iterator(); expectedIter.hasNext(); ) {
            Assert.assertEquals(
                    message + " [expected order to be the same at left]",
                    expectedIter.next(),
                    actualIter.next()
            );
        }
        for (final Iterator<Map.Entry<String, String>> expectedIter = expected.right().entrySet().iterator(), actualIter = actual.right().entrySet().iterator(); expectedIter.hasNext(); ) {
            Assert.assertEquals(
                    message + " [expected order to be the same at right]",
                    expectedIter.next(),
                    actualIter.next()
            );
        }
    }

    private String expectedAndActual() {
        return "expected [" + expected + "] and actual [" + actual + "]";
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
    public void put(final String left, final String right) {
        final String expectedAndActual = expectedAndActual();
        expected.put(left, right);
        actual.put(left, right);
        assertEqualsOrdered(expectedAndActual + " are not equal after put(" + left + ", " + right + ")");
        invariantChecker.run();
    }

    @Override
    public String leftRemove(final String left) {
        final String expectedAndActual = expectedAndActual();
        final String result = expected.leftRemove(left);
        Assert.assertEquals(
                "leftRemove(" + left + ") of " + expectedAndActual + " are not equal",
                result,
                actual.leftRemove(left));
        assertEqualsOrdered(expectedAndActual + " are not equal after leftRemove(" + left + ")");
        invariantChecker.run();
        return result;
    }

    @Override
    public String rightRemove(final String right) {
        final String expectedAndActual = expectedAndActual();
        final String result = expected.rightRemove(right);
        Assert.assertEquals(
                "rightRemove(" + right + ") of " + expectedAndActual + " are not equal",
                result,
                actual.rightRemove(right));
        assertEqualsOrdered(expectedAndActual + " are not equal after rightRemove(" + right + ")");
        invariantChecker.run();
        return result;
    }

    @Override
    public void putAll(final Bimap other) {
        final String expectedAndActual = expectedAndActual();
        expected.putAll(other);
        actual.putAll(other);
        assertEqualsOrdered(expectedAndActual + " are not equal after putAll(" + other + ")");
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
    public Map<String, String> left() {
        return new MapChecker(expected.left(), actual.left()).invariantChecker(() -> assertEqualsOrdered(expectedAndActual() + " are not equal after operation with left()"));
    }

    @Override
    public Map<String, String> right() {
        return new MapChecker(expected.right(), actual.right()).invariantChecker(() -> assertEqualsOrdered(expectedAndActual() + " are not equal after operation with right()"));
    }
}
