package collections;

import java.util.Iterator;

public interface BimapLeftIterator extends Iterator<String> {
    BimapRightIterator flip();
}
