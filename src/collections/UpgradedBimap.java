package collections;

public interface UpgradedBimap extends Bimap {
    BimapLeftIterator leftIterator();
    default BimapRightIterator rightIterator() {
        return leftIterator().flip();
    }
}
