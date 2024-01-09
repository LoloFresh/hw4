package collections;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.*;
import java.util.function.Consumer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TreeBimapTest {
    private static Runnable assertInvariant(final TreeBimap set) {
        return () -> Assert.assertTrue("Set invariant is not satisfied " + set, set.checkInvariant());
    }

    private static void testCorrectness(final Bimap expected, final TreeBimap actual, final Consumer<? super BimapChecker> operation) {
        operation.accept(new BimapChecker(expected, actual).invariantChecker(assertInvariant(actual)));
    }

    private final Random random = new Random(3197515612765697362L);

    @Test
    public void test01Invariant() {
        testCorrectness(
                new SimpleBimap(),
                new TreeBimap(),
                b -> {
                    for (int i = 0; i < 20; i++) {
                        final SimpleBimap toAdd = new SimpleBimap(LinkedHashMap::new);
                        for (int j = 0; j < 100; j++) {
                            final String str = random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString();
                            toAdd.put(str, str);
                        }
                        b.putAll(toAdd);
                        System.out.println(i);
                    }
                }
        );
    }

    @Test
    public void test02Simple() {
        testCorrectness(
                new SimpleBimap(),
                new TreeBimap(),
                b -> {
                    b.put("hello", "hello");
                    b.leftRemove("hello");
                    b.put("world", "world");
                    b.rightRemove("world");
                }
        );
    }

    @Test
    public void test03CustomCmp() {
        testCorrectness(
                new SimpleBimap(Comparator.naturalOrder(), Comparator.nullsFirst(Comparator.reverseOrder())),
                new TreeBimap(Comparator.naturalOrder(), Comparator.nullsFirst(Comparator.reverseOrder())),
                b -> {
                    b.put("abc", "def");
                    b.put("null", "defff");
                    b.put("a", null);
                    b.put("", "de");
                }
        );
    }

    @Test
    public void test04Put() {
        testCorrectness(
                new SimpleBimap(),
                new TreeBimap(),
                b -> {
                    b.put("hello", "cruel");
                    b.put("cruel", "unhappy");
                    b.put("unhappy", "world");
                    b.put("world", "hello");

                    b.put("cruel", "cruel");
                    b.put("unhappy", "cruel");
                    b.put("unhappy", "cruel");
                }
        );
    }

    @Test
    public void test05Erase() {
        testCorrectness(
                new SimpleBimap(
                        Comparator.nullsLast(Comparator.comparing(s -> new StringBuilder(s).reverse().toString())),
                        Comparator.reverseOrder()
                ),
                new TreeBimap(
                        Comparator.nullsLast(Comparator.comparing(s -> new StringBuilder(s).reverse().toString())),
                        Comparator.reverseOrder()
                ),
                b -> {
                    b.put("cruel", "unhappy");
                    b.put("unhappy", "world");
                    b.put("hello", "cruel");
                    b.put(null, "null");
                    b.put("world", "hello");
                    b.put("null", "no nulls");


                    b.leftRemove("unhappy");
                    b.rightRemove("null");
                    b.leftRemove(null);
                    b.leftRemove("does not exist");
                    b.rightRemove("unhappy");
                    b.rightRemove("no nulls");
                    b.rightRemove("hello");
                    b.leftRemove("hello");

                    b.put("test", "lol");
                }
        );
    }

    @Test
    public void test06_Randomized() {
        testCorrectness(
                new SimpleBimap(
                        Comparator.nullsFirst(Comparator.comparingLong(String::length).thenComparing(Comparator.reverseOrder())),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ),
                new TreeBimap(
                        Comparator.nullsFirst(Comparator.comparingLong(String::length).thenComparing(Comparator.reverseOrder())),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ),
                b -> {
                    final SimpleBimap data = new SimpleBimap(LinkedHashMap::new);
                    for (int j = 0; j < 500; j++) {
                        final String str1 = random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                .collect(
                                        StringBuilder::new,
                                        StringBuilder::appendCodePoint,
                                        StringBuilder::append
                                ).toString();
                        final String str2 = random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                .collect(
                                        StringBuilder::new,
                                        StringBuilder::appendCodePoint,
                                        StringBuilder::append
                                ).toString();
                        data.put(str1, str2);
                    }
                    b.putAll(data);

                    final List<Map.Entry<String, String>> data1 = new ArrayList<>(data.left().entrySet());
                    Collections.shuffle(data1, random);

                    for (final Map.Entry<String, String> entry : data1) {
                        final int choice = random.nextInt(0, 3);
                        if (choice == 1) {
                            b.leftRemove(entry.getKey());
                        } else if (choice == 2) {
                            b.rightRemove(entry.getValue());
                        } else if (choice == 0) {
                            b.leftRemove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        } else {
                            b.rightRemove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        }
                    }
                }
        );
    }

    @Test
    public void test07_ViewsSimple() {
        testCorrectness(
                new SimpleBimap(),
                new TreeBimap(),
                b -> {
                    final MapChecker forward = b.left();
                    final MapChecker backward = b.right();

                    b.put("hello", "world");
                    forward.check("forward 7#1");
                    backward.check("backward 7#1");

                    b.leftRemove("hello");
                    forward.check("forward 7#2");
                    backward.check("backward 7#2");

                    b.put("hello", "cruel");
                    b.put("cruel", "world");
                    b.put("world", "hello");
                    forward.remove("hello");
                    backward.remove("hello");
                    forward.check("forward 7#3");
                }
        );
    }

    @Test
    public void test08_ViewsRandomized() {
        testCorrectness(
                new SimpleBimap(Comparator.reverseOrder(), Comparator.naturalOrder()),
                new TreeBimap(Comparator.reverseOrder(), Comparator.naturalOrder()),
                b -> {
                    final MapChecker forward = b.left();
                    final MapChecker backward = b.right();

                    final SimpleBimap data = new SimpleBimap(LinkedHashMap::new);

                    for (int i = 0; i < 500; i++) {
                        final String str1 = random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                .collect(
                                        StringBuilder::new,
                                        StringBuilder::appendCodePoint,
                                        StringBuilder::append
                                ).toString();
                        final String str2 = random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                .collect(
                                        StringBuilder::new,
                                        StringBuilder::appendCodePoint,
                                        StringBuilder::append
                                ).toString();
                        data.put(str1, str2);
                    }
                    b.putAll(data);

                    final List<Map.Entry<String, String>> data1 = new ArrayList<>(data.left().entrySet());
                    Collections.shuffle(data1, random);

                    for (final Map.Entry<String, String> entry : data1) {
                        final int choice = random.nextInt(0, 7);
                        if (choice == 0) {
                            forward.remove(entry.getKey());
                        } else if (choice == 1) {
                            backward.remove(entry.getValue());
                        } else if (choice == 2) {
                            b.leftRemove(entry.getKey());
                        } else if (choice == 3) {
                            b.rightRemove(entry.getValue());
                        } else if (choice == 4) {
                            forward.remove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        } else if (choice == 5) {
                            backward.remove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        } else if (choice == 6) {
                            b.leftRemove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        } else {
                            b.rightRemove(random.ints(random.nextInt(50, 100), ' ', 0xFFFF)
                                    .collect(
                                            StringBuilder::new,
                                            StringBuilder::appendCodePoint,
                                            StringBuilder::append
                                    ).toString());
                        }
                        forward.check("forward 8##");
                        backward.check("backward 8##");
                    }
                }
        );
    }
}
