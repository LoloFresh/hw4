package collections;

import java.util.Map;
import java.util.Set;

/**
 * Represents <a href=https://en.wikipedia.org/wiki/Bidirectional_map>bimap</a> string<->string data structure.
 */
public interface Bimap {
    int size();
    boolean isEmpty();
    void put(String left, String right);
    String leftRemove(String left);
    String rightRemove(String right);
    void putAll(Bimap other);
    void clear();

    Map<String, String> left();
    Map<String, String> right();
}
