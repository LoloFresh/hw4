package collections;

import java.util.*;

public class TreeBimap implements Bimap {
    private static final class Half {
        String key;
        Node left;
        Node right;
        Node parent;
        int height;

        public Half(final String key, final Node parent) {
            this.key = key;
            this.left = null;
            this.right = null;
            this.parent = parent;
            this.height = 1;
        }
    }

    private static final class Node {
        Half leftHalf;
        Half rightHalf;

        public Node(String leftK, String rightK, final Node leftP, final Node rightP) {
            leftHalf = new Half(leftK, leftP);
            rightHalf = new Half(rightK, rightP);
        }

        private static int leftHeight(final Node node) {
            return node == null ? 0 : node.leftHalf.height;
        }

        private static int rightHeight(final Node node) {
            return node == null ? 0 : node.rightHalf.height;
        }

        private void leftFixHeight() {
            leftHalf.height = Math.max(leftHeight(leftHalf.left), leftHeight(leftHalf.right)) + 1;
        }

        private void rightFixHeight() {
            rightHalf.height = Math.max(rightHeight(rightHalf.left), rightHeight(rightHalf.right)) + 1;
        }

        public boolean checkInvariant(final Comparator<? super String> leftCmp, final Comparator<? super String> rightCmp) {
            return (leftHalf.left == null || leftHalf.left.leftHalf.parent == this && leftCmp.compare(leftHalf.left.leftHalf.key, leftHalf.key) < 0 && leftHalf.left.checkInvariant(leftCmp, rightCmp))
                    && (leftHalf.right == null || leftHalf.right.leftHalf.parent == this && leftCmp.compare(leftHalf.key, leftHalf.right.leftHalf.key) < 0 && leftHalf.right.checkInvariant(leftCmp, rightCmp))
                    && (rightHalf.left == null || rightHalf.left.rightHalf.parent == this && rightCmp.compare(rightHalf.left.rightHalf.key, rightHalf.key) < 0)
                    && (rightHalf.right == null || rightHalf.right.rightHalf.parent == this && rightCmp.compare(rightHalf.key, rightHalf.right.rightHalf.key) < 0)
                    && leftHalf.height == Math.max(leftHeight(leftHalf.left), leftHeight(leftHalf.right)) + 1
                    && Math.abs(leftHeight(leftHalf.left) - leftHeight(leftHalf.right)) <= 1
                    && rightHalf.height == Math.max(rightHeight(rightHalf.left), rightHeight(rightHalf.right)) + 1
                    && Math.abs(rightHeight(rightHalf.left) - rightHeight(rightHalf.right)) <= 1;
        }

        public Node leftMin() {
            Node that = this;
            while (that.leftHalf.left != null) {
                that = that.leftHalf.left;
            }
            return that;
        }

        public Node rightMin() {
            Node that = this;
            while (that.rightHalf.left != null) {
                that = that.rightHalf.left;
            }
            return that;
        }

        public Node leftMax() {
            Node that = this;
            while (that.leftHalf.right != null) {
                that = that.leftHalf.right;
            }
            return that;
        }

        public Node rightMax() {
            Node that = this;
            while (that.rightHalf.right != null) {
                that = that.rightHalf.right;
            }
            return that;
        }

        public Node leftNext() {
            if (leftHalf.right != null) {
                return leftHalf.right.leftMin();
            } else {
                Node that = this;
                while (that == that.leftHalf.parent.leftHalf.right) {
                    that = that.leftHalf.parent;
                }
                return that.leftHalf.parent;
            }
        }

        public Node rightNext() {
            if (rightHalf.right != null) {
                return rightHalf.right.rightMin();
            } else {
                Node that = this;
                while (that == that.rightHalf.parent.rightHalf.right) {
                    that = that.rightHalf.parent;
                }
                return that.rightHalf.parent;
            }
        }

        public Node leftPrev() {
            if (leftHalf.left != null) {
                return leftHalf.left.leftMax();
            } else {
                Node that = this;
                while (that == that.leftHalf.parent.leftHalf.left) {
                    that = that.leftHalf.parent;
                }
                return that.leftHalf.parent;
            }
        }

        public Node rightPrev() {
            if (rightHalf.left != null) {
                return rightHalf.left.rightMax();
            } else {
                Node that = this;
                while (that == that.rightHalf.parent.rightHalf.left) {
                    that = that.rightHalf.parent;
                }
                return that.rightHalf.parent;
            }
        }

        private void leftReplaceWith(final Node other) {
            assert leftHalf.parent != null;
            if (this == leftHalf.parent.leftHalf.left) {
                leftHalf.parent.leftHalf.left = other;
            } else {
                assert this == leftHalf.parent.leftHalf.right;
                leftHalf.parent.leftHalf.right = other;
            }
            other.leftHalf.parent = leftHalf.parent;
        }

        private void rightReplaceWith(final Node other) {
            assert rightHalf.parent != null;
            if (this == rightHalf.parent.rightHalf.left) {
                rightHalf.parent.rightHalf.left = other;
            } else {
                assert this == rightHalf.parent.rightHalf.right;
                rightHalf.parent.rightHalf.right = other;
            }
            other.rightHalf.parent = rightHalf.parent;
        }

        private void leftSetLeft(final Node newLeft) {
            leftHalf.left = newLeft;
            if (newLeft != null) {
                newLeft.leftHalf.parent = this;
            }
        }

        private void rightSetLeft(final Node newLeft) {
            rightHalf.left = newLeft;
            if (newLeft != null) {
                newLeft.rightHalf.parent = this;
            }
        }

        private void leftSetRight(final Node newRight) {
            leftHalf.right = newRight;
            if (newRight != null) {
                newRight.leftHalf.parent = this;
            }
        }

        private void rightSetRight(final Node newRight) {
            rightHalf.right = newRight;
            if (newRight != null) {
                newRight.rightHalf.parent = this;
            }
        }

        private void leftRotateLeft() {
            final Node r = leftHalf.right;
            leftReplaceWith(r);
            leftSetRight(r.leftHalf.left);
            r.leftSetLeft(this);
            leftFixHeight();
            r.leftFixHeight();
        }

        private void rightRotateLeft() {
            final Node r = rightHalf.right;
            rightReplaceWith(r);
            rightSetRight(r.rightHalf.left);
            r.rightSetLeft(this);
            rightFixHeight();
            r.rightFixHeight();
        }

        private void leftRotateRight() {
            final Node l = leftHalf.left;
            leftReplaceWith(l);
            leftSetLeft(l.leftHalf.right);
            l.leftSetRight(this);
            leftFixHeight();
            l.leftFixHeight();
        }

        private void rightRotateRight() {
            final Node l = rightHalf.left;
            rightReplaceWith(l);
            rightSetLeft(l.rightHalf.right);
            l.rightSetRight(this);
            rightFixHeight();
            l.rightFixHeight();
        }

        public void leftFix() {
            Node that = this;
            while (that.leftHalf.parent != null) {
                final int disbalance = leftHeight(that.leftHalf.left) - leftHeight(that.leftHalf.right);
                switch (disbalance) {
                    case -1, 0, 1:
                        that.leftFixHeight();
                        break;
                    case -2:
                        if (leftHeight(that.leftHalf.right.leftHalf.right) == leftHeight(that.leftHalf.right) - 1) {
                            that.leftRotateLeft();
                        } else {
                            that.leftHalf.right.leftRotateRight();
                            that.leftRotateLeft();
                        }
                        break;
                    case 2:
                        if (leftHeight(that.leftHalf.left.leftHalf.left) == leftHeight(that.leftHalf.left) - 1) {
                            that.leftRotateRight();
                        } else {
                            that.leftHalf.left.leftRotateLeft();
                            that.leftRotateRight();
                        }
                        break;
                    default:
                        throw new AssertionError("left balance of " + that + " is equal to " + disbalance);
                }
                that = that.leftHalf.parent;
            }
        }

        public void rightFix() {
            Node that = this;
            while (that.rightHalf.parent != null) {
                final int disbalance = rightHeight(that.rightHalf.left) - rightHeight(that.rightHalf.right);
                switch (disbalance) {
                    case -1, 0, 1:
                        that.rightFixHeight();
                        break;
                    case -2:
                        if (rightHeight(that.rightHalf.right.rightHalf.right) == rightHeight(that.rightHalf.right) - 1) {
                            that.rightRotateLeft();
                        } else {
                            that.rightHalf.right.rightRotateRight();
                            that.rightRotateLeft();
                        }
                        break;
                    case 2:
                        if (rightHeight(that.rightHalf.left.rightHalf.left) == rightHeight(that.rightHalf.left) - 1) {
                            that.rightRotateRight();
                        } else {
                            that.rightHalf.left.rightRotateLeft();
                            that.rightRotateRight();
                        }
                        break;
                    default:
                        throw new AssertionError("right balance of " + that + " is equal to " + disbalance);
                }
                that = that.rightHalf.parent;
            }
        }
    }

    private final Comparator<? super String> leftCmp;
    private final Comparator<? super String> rightCmp;
    private Node rootParent = new Node(null, null, null, null); // roots are rootParent.*.right
    private int size = 0;

    public TreeBimap() {
        this(new NaturalOrderingComparator<>(), new NaturalOrderingComparator<>());
    }

    public TreeBimap(final Comparator<? super String> leftCmp, final Comparator<? super String> rightCmp) {
        this.leftCmp = leftCmp;
        this.rightCmp = rightCmp;
    }

    public boolean checkInvariant() {
        return (rootParent.leftHalf.right == null && rootParent.rightHalf.right == null)
                || (rootParent.leftHalf.right != null && rootParent.rightHalf.right != null && rootParent.leftHalf.right.checkInvariant(leftCmp, rightCmp));
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    private Node leftFind(final String key) {
        Node node = rootParent.leftHalf.right;
        while (node != null) {
            int res = leftCmp.compare(key, node.leftHalf.key);
            if (res < 0) {
                node = node.leftHalf.left;
            } else if (res > 0) {
                node = node.leftHalf.right;
            } else {
                return node;
            }
        }
        return null;
    }

    private Node rightFind(final String key) {
        Node node = rootParent.rightHalf.right;
        while (node != null) {
            int res = rightCmp.compare(key, node.rightHalf.key);
            if (res < 0) {
                node = node.rightHalf.left;
            } else if (res > 0) {
                node = node.rightHalf.right;
            } else {
                return node;
            }
        }
        return null;
    }

    private void remove(final Node node) {
        final Node leftToUnlink;
        final Node leftToFix;
        if (node.leftHalf.right != null && node.leftHalf.left != null) {
            leftToUnlink = node.leftHalf.right.leftMin();
            assert leftToUnlink.leftHalf.left == null;
            if (leftToUnlink.leftHalf.parent != node) {
                leftToFix = leftToUnlink.leftHalf.parent;
            } else {
                leftToFix = leftToUnlink;
            }
        } else {
            leftToUnlink = node;
            leftToFix = node.leftHalf.parent;
        }
        if (leftToUnlink.leftHalf.right == null) {
            if (leftToUnlink.leftHalf.parent.leftHalf.left == leftToUnlink) {
                leftToUnlink.leftHalf.parent.leftHalf.left = leftToUnlink.leftHalf.left;
            } else {
                leftToUnlink.leftHalf.parent.leftHalf.right = leftToUnlink.leftHalf.left;
            }
            if (leftToUnlink.leftHalf.left != null) {
                leftToUnlink.leftHalf.left.leftHalf.parent = leftToUnlink.leftHalf.parent;
            }
        } else {
            if (leftToUnlink.leftHalf.parent.leftHalf.left == leftToUnlink) {
                leftToUnlink.leftHalf.parent.leftHalf.left = leftToUnlink.leftHalf.right;
            } else {
                leftToUnlink.leftHalf.parent.leftHalf.right = leftToUnlink.leftHalf.right;
            }
            leftToUnlink.leftHalf.right.leftHalf.parent = leftToUnlink.leftHalf.parent;
        }
        if (leftToUnlink != node) {
            leftToUnlink.leftHalf.left = node.leftHalf.left;
            if (leftToUnlink.leftHalf.left != null) {
                leftToUnlink.leftHalf.left.leftHalf.parent = leftToUnlink;
            }
            leftToUnlink.leftHalf.right = node.leftHalf.right;
            if (leftToUnlink.leftHalf.right != null) {
                leftToUnlink.leftHalf.right.leftHalf.parent = leftToUnlink;
            }
            leftToUnlink.leftHalf.parent = node.leftHalf.parent;
            if (node == node.leftHalf.parent.leftHalf.left) {
                leftToUnlink.leftHalf.parent.leftHalf.left = leftToUnlink;
            } else {
                assert node == node.leftHalf.parent.leftHalf.right;
                leftToUnlink.leftHalf.parent.leftHalf.right = leftToUnlink;
            }
            node.leftHalf.left = node.leftHalf.right = node.leftHalf.parent = null;
        }

        final Node rightToUnlink;
        final Node rightToFix;
        if (node.rightHalf.right != null && node.rightHalf.left != null) {
            rightToUnlink = node.rightHalf.right.rightMin();
            assert rightToUnlink.rightHalf.left == null;
            if (rightToUnlink.rightHalf.parent != node) {
                rightToFix = rightToUnlink.rightHalf.parent;
            } else {
                rightToFix = rightToUnlink;
            }
        } else {
            rightToUnlink = node;
            rightToFix = rightToUnlink.rightHalf.parent;
        }
        if (rightToUnlink.rightHalf.right == null) {
            if (rightToUnlink.rightHalf.parent.rightHalf.left == rightToUnlink) {
                rightToUnlink.rightHalf.parent.rightHalf.left = rightToUnlink.rightHalf.left;
            } else {
                rightToUnlink.rightHalf.parent.rightHalf.right = rightToUnlink.rightHalf.left;
            }
            if (rightToUnlink.rightHalf.left != null) {
                rightToUnlink.rightHalf.left.rightHalf.parent = rightToUnlink.rightHalf.parent;
            }
        } else {
            if (rightToUnlink.rightHalf.parent.rightHalf.left == rightToUnlink) {
                rightToUnlink.rightHalf.parent.rightHalf.left = rightToUnlink.rightHalf.right;
            } else {
                rightToUnlink.rightHalf.parent.rightHalf.right = rightToUnlink.rightHalf.right;
            }
            rightToUnlink.rightHalf.right.rightHalf.parent = rightToUnlink.rightHalf.parent;
        }
        if (rightToUnlink != node) {
            rightToUnlink.rightHalf.left = node.rightHalf.left;
            if (rightToUnlink.rightHalf.left != null) {
                rightToUnlink.rightHalf.left.rightHalf.parent = rightToUnlink;
            }
            rightToUnlink.rightHalf.right = node.rightHalf.right;
            if (rightToUnlink.rightHalf.right != null) {
                rightToUnlink.rightHalf.right.rightHalf.parent = rightToUnlink;
            }
            rightToUnlink.rightHalf.parent = node.rightHalf.parent;
            if (node == node.rightHalf.parent.rightHalf.left) {
                rightToUnlink.rightHalf.parent.rightHalf.left = rightToUnlink;
            } else {
                assert node == node.rightHalf.parent.rightHalf.right;
                rightToUnlink.rightHalf.parent.rightHalf.right = rightToUnlink;
            }
            node.rightHalf.left = node.rightHalf.right = node.rightHalf.parent = null;
        }

        leftToFix.leftFix();
        rightToFix.rightFix();

        size--;
    }

    @Override
    public void clear() {
        rootParent.leftHalf.right = null;
        rootParent.rightHalf.right = null;
        size = 0;
    }

    @Override
    public void put(final String left, final String right) {
        if (rootParent.leftHalf.right == null) {
            assert rootParent.rightHalf.right == null;
            rootParent.leftHalf.right = rootParent.rightHalf.right = new Node(left, right, rootParent, rootParent);
            size++;
            return;
        }
        {
            final Node currentRight = leftFind(left);
            if (currentRight != null) {
                remove(currentRight);
            }
            final Node currentLeft = rightFind(right);
            if (currentLeft != null) {
                remove(currentLeft);
            }
        }

        Node leftNode = rootParent.leftHalf.right;
        Node rightNode = rootParent.rightHalf.right;
        enum SearchResult {
            TO_LEFT, TO_RIGHT
        }
        final SearchResult leftResult;
        final SearchResult rightResult;
        while (true) {
            final int res = leftCmp.compare(left, leftNode.leftHalf.key);
            if (res < 0) {
                if (leftNode.leftHalf.left == null) {
                    leftResult = SearchResult.TO_LEFT;
                    break;
                }
                leftNode = leftNode.leftHalf.left;
            } else {
                assert res > 0;
                if (leftNode.leftHalf.right == null) {
                    leftResult = SearchResult.TO_RIGHT;
                    break;
                }
                leftNode = leftNode.leftHalf.right;
            }
        }
        while (true) {
            final int res = rightCmp.compare(right, rightNode.rightHalf.key);
            if (res < 0) {
                if (rightNode.rightHalf.left == null) {
                    rightResult = SearchResult.TO_LEFT;
                    break;
                }
                rightNode = rightNode.rightHalf.left;
            } else {
                assert res > 0;
                if (rightNode.rightHalf.right == null) {
                    rightResult = SearchResult.TO_RIGHT;
                    break;
                }
                rightNode = rightNode.rightHalf.right;
            }
        }

        final Node newNode = new Node(left, right, leftNode, rightNode);
        if (leftResult == SearchResult.TO_LEFT) {
            leftNode.leftHalf.left = newNode;
        } else {
            leftNode.leftHalf.right = newNode;
        }
        if (rightResult == SearchResult.TO_LEFT) {
            rightNode.rightHalf.left = newNode;
        } else {
            rightNode.rightHalf.right = newNode;
        }

        leftNode.leftFix();
        rightNode.rightFix();
        size++;
    }

    @Override
    public String leftRemove(final String left) {
        final Node node = leftFind(left);
        if (node != null) {
            remove(node);
            return node.rightHalf.key;
        } else {
            return null;
        }
    }

    @Override
    public String rightRemove(final String right) {
        final Node node = rightFind(right);
        if (node != null) {
            remove(node);
            return node.leftHalf.key;
        } else {
            return null;
        }
    }

    @Override
    public void putAll(final Bimap other) {
        other.left().forEach(this::put);
    }

    private record Entry(String key, String value) implements Map.Entry<String, String> {
        @Override
        public String getKey() {
            return key();
        }

        @Override
        public String getValue() {
            return value();
        }

        @Override
        public String setValue(final String value) {
            throw new UnsupportedOperationException("Bimap.Entry setValue");
        }
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
                            private Node node = rootParent;
                            private boolean removeValid = false;

                            @Override
                            public boolean hasNext() {
                                if (node.leftHalf.right != null) {
                                    return true;
                                } else {
                                    Node that = node;
                                    while (that.leftHalf.parent != null && that == that.leftHalf.parent.leftHalf.right) {
                                        that = that.leftHalf.parent;
                                    }
                                    return that.leftHalf.parent != null;
                                }
                            }

                            @Override
                            public Entry<String, String> next() {
                                if (!hasNext()) {
                                    throw new NoSuchElementException();
                                }
                                node = node.leftNext();
                                removeValid = true;
                                return new TreeBimap.Entry(node.leftHalf.key, node.rightHalf.key);
                            }

                            @Override
                            public void remove() {
                                if (!removeValid) {
                                    throw new IllegalStateException();
                                }
                                final Node prev = node.leftPrev();
                                TreeBimap.this.remove(node);
                                removeValid = false;
                                node = prev;
                            }
                        };
                    }

                    @Override
                    public boolean contains(final Object o) {
                        @SuppressWarnings("unchecked") final Entry<String, String> entry = (Entry<String, String>) o;
                        final Node node = TreeBimap.this.leftFind(entry.getKey());
                        return node != null && rightCmp.compare(node.rightHalf.key, entry.getValue()) == 0;
                    }

                    @Override
                    public int size() {
                        return TreeBimap.this.size();
                    }

                    @Override
                    public void clear() {
                        TreeBimap.this.clear();
                    }

                    @Override
                    public boolean remove(final Object o) {
                        @SuppressWarnings("unchecked") final Entry<String, String> entry = (Entry<String, String>) o;
                        final Node node = TreeBimap.this.leftFind(entry.getKey());
                        if (node != null && rightCmp.compare(node.rightHalf.key, entry.getValue()) == 0) {
                            TreeBimap.this.remove(node);
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }

            @Override
            public boolean containsKey(final Object key) {
                return TreeBimap.this.leftFind((String) key) != null;
            }

            @Override
            public String get(final Object key) {
                final Node node = TreeBimap.this.leftFind((String) key);
                return node == null ? null : node.rightHalf.key;
            }

            @Override
            public String remove(final Object key) {
                return TreeBimap.this.leftRemove((String) key);
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
                            private Node node = rootParent;
                            private boolean removeValid = false;

                            @Override
                            public boolean hasNext() {
                                if (node.rightHalf.right != null) {
                                    return true;
                                } else {
                                    Node that = node;
                                    while (that.rightHalf.parent != null && that == that.rightHalf.parent.rightHalf.right) {
                                        that = that.rightHalf.parent;
                                    }
                                    return that.rightHalf.parent != null;
                                }
                            }

                            @Override
                            public Entry<String, String> next() {
                                if (!hasNext()) {
                                    throw new NoSuchElementException();
                                }
                                node = node.rightNext();
                                removeValid = true;
                                return new TreeBimap.Entry(node.rightHalf.key, node.leftHalf.key);
                            }

                            @Override
                            public void remove() {
                                if (!removeValid) {
                                    throw new IllegalStateException();
                                }
                                final Node prev = node.rightPrev();
                                TreeBimap.this.remove(node);
                                removeValid = false;
                                node = prev;
                            }
                        };
                    }

                    @Override
                    public boolean contains(final Object o) {
                        @SuppressWarnings("unchecked") final Entry<String, String> entry = (Entry<String, String>) o;
                        final Node node = TreeBimap.this.rightFind(entry.getValue());
                        return node != null && leftCmp.compare(node.leftHalf.key, entry.getKey()) == 0;
                    }

                    @Override
                    public int size() {
                        return TreeBimap.this.size();
                    }

                    @Override
                    public void clear() {
                        TreeBimap.this.clear();
                    }

                    @Override
                    public boolean remove(final Object o) {
                        @SuppressWarnings("unchecked") final Entry<String, String> entry = (Entry<String, String>) o;
                        final Node node = TreeBimap.this.rightFind(entry.getValue());
                        if (node != null && leftCmp.compare(node.leftHalf.key, entry.getKey()) == 0) {
                            TreeBimap.this.remove(node);
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }

            @Override
            public boolean containsKey(final Object key) {
                return TreeBimap.this.rightFind((String) key) != null;
            }

            @Override
            public String get(final Object key) {
                final Node node = TreeBimap.this.rightFind((String) key);
                return node == null ? null : node.leftHalf.key;
            }

            @Override
            public String remove(final Object key) {
                return TreeBimap.this.rightRemove((String) key);
            }
        };
    }

    @Override
    public String toString() {
        return "TreeBimap{" + "left=" + left() + ", right=" + right() + '}';
    }
}
