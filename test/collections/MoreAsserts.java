package collections;

import java.util.function.Supplier;

public class MoreAsserts {
    public static <T> T assertNoThrow(final String message, final Supplier<? extends T> supplier) {
        try {
            return supplier.get();
        } catch (final RuntimeException e) {
            throw new AssertionError(message, e);
        }
    }

    public static void assertNoThrow(final String message, final Runnable runnable) {
        try {
            runnable.run();
        } catch (final RuntimeException e) {
            throw new AssertionError(message, e);
        }
    }
}
