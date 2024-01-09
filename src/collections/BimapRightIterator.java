package collections;

import java.util.Iterator;

public interface BimapRightIterator extends Iterator<String> {
    BimapLeftIterator flip();
}
