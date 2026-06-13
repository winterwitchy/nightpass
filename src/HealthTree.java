public class HealthTree {
    HealthNode root;


    // height, updateHeight and balance methods are mainstream blocks used for AVL trees after insertion and deletion.
    private int height(HealthNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    private void updateHeight(HealthNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private int balance(HealthNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }


    // traverses tree searching for the biggest key less than threshold
    public HealthNode searchMaxLT(int healthThreshold) {
        return searchRecursively(root, healthThreshold, null, false);
    }
    // traverses tree searching for the smallest key greater than threshold
    public HealthNode searchMinGT(int healthThreshold) {
        return searchRecursively(root, healthThreshold, null, true);
    }
    private HealthNode searchRecursively(HealthNode current, int healthThreshold, HealthNode result, boolean flag) {
        if (flag) {
            if (current == null) {
                return result;
            }
            if (current.healthKey > healthThreshold) {
                result = current;
                return searchRecursively(current.left, healthThreshold, result, flag);
            } else {
                return searchRecursively(current.right, healthThreshold, result, flag);
            }
        } else {
            if (current == null) {
                return result;
            }
            if (current.healthKey < healthThreshold) {
                result = current;
                return searchRecursively(current.right, healthThreshold, result, flag);
            } else {
                return searchRecursively(current.left, healthThreshold, result, flag);
            }
        }
    }


    // rotations are invoked after deletion to keep the tree balanced
    private HealthNode rightRotate(HealthNode y) {
        HealthNode x = y.left;
        HealthNode T2 = x.right;
        // perform rotation
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private HealthNode leftRotate(HealthNode x) {
        HealthNode y = x.right;
        HealthNode T2 = y.left;
        // perform rotation
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // traverses the tree, looking for the right place for key of the card
    public void insert(Card card) {
        root = insertRecursively(root, card);
    }
    private HealthNode insertRecursively(HealthNode current, Card card) {
        if (current == null) {
            if (current == root) {
                root = new HealthNode(card);
                return root;
            }
            return new HealthNode(card);
        }
        if (card.Hcur == current.healthKey) {
            current.addCard(card);
        }
        else if (card.Hcur < current.healthKey) {
            current.left = insertRecursively(current.left, card);
        } else {
            current.right = insertRecursively(current.right, card);
        }
        updateHeight(current);

        int balance = balance(current);

        // left left
        if (balance > 1 && card.Hcur < current.left.healthKey) {
            return rightRotate(current);
        }
        // right right
        if (balance < -1 && card.Hcur > current.right.healthKey) {
            return leftRotate(current);
        }
        // left right
        if (balance > 1 && card.Hcur > current.left.healthKey) {
            current.left = leftRotate(current.left);
            return rightRotate(current);
        }
        // right left
        if (balance < -1 && card.Hcur < current.right.healthKey) {
            current.right = rightRotate(current.right);
            return leftRotate(current);
        }

        return current;
    }


    // removes a node when it has no elements left. once the node is found through traversal, checks its children
    // if it has no children, it's nullified. it has one child it is switched into parent.
    // if it has two children, the right subtree's leftmost leaf is loaded into its place.
    public void removeNode(HealthNode node) {
        root = removeRecursively(root, node.healthKey);
    }
    private HealthNode removeRecursively(HealthNode current, int healthKey) {
        if (current == null) {
            return null;
        }
        if (healthKey < current.healthKey) {
            current.left = removeRecursively(current.left, healthKey);
        } else if (healthKey > current.healthKey) {
            current.right = removeRecursively(current.right, healthKey);
        } else {
            if ((current.left == null) || (current.right == null)) {
                HealthNode temp = null;
                if (current.left == null) {
                    temp = current.right;
                } else {
                    temp = current.left;
                }
                current = temp;
            } else {
                HealthNode temp = current.right;
                while (temp.left != null) {
                    temp = temp.left;
                }
                // Loading values into this node's place
                current.healthKey = temp.healthKey;
                current.cards = temp.cards;
                current.firstIndex = temp.firstIndex;
                current.lastIndex = temp.lastIndex;
                // Remove it: it has no children so it will likely be nullified
                current.right = removeRecursively(current.right, temp.healthKey);
            }
        }
        if (current == null) {
            return current;
        }

        updateHeight(current);
        int balance = balance(current);                     // we deleted, this edits are necessary to keep things balanced

        if (balance > 1 && balance(current.left) >= 0) {
            return rightRotate(current);
        }
        if (balance > 1 && balance(current.left) < 0) {
            current.left = leftRotate(current.left);
            return rightRotate(current);
        }
        if (balance < -1 && balance(current.right) <= 0) {
            return leftRotate(current);
        }
        if (balance < -1 && balance(current.right) > 0) {
            current.right = rightRotate(current.right);
            return leftRotate(current);
        }

        return current;
    }


}
