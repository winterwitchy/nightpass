public class AttackTree {
    AttackNode root;



    // methods used to keep the AVL tree balanced, as explained in HealthTree class.
    private int height(AttackNode node) {
        if (node == null)
            return 0;
        return node.height;
    }
    private void updateHeight(AttackNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }
    private int balance(AttackNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    // searches the maximum key less than the threshold
    public AttackNode searchMaxLT(int attackThreshold) {
        return searchRecursively(root, attackThreshold, null, false);
    }

    // searches the minimum key greater than OR EQUAL TO the threshold (means it can indeed kill, not implemented for health bc we don't want to die :)
    public AttackNode searchMinGT(int attackThreshold) {
        return searchRecursively(root, attackThreshold, null, true);
    }
    private AttackNode searchRecursively(AttackNode current, int attackThreshold, AttackNode result, boolean flag) {
        if (flag) {
            if (current == null) {
                return result;
            }
            if (current.attackKey == attackThreshold) {
                result = current;
                return result;
            }
            if (current.attackKey > attackThreshold) {
                result = current;
                return searchRecursively(current.left, attackThreshold, result, flag);
            } else {
                return searchRecursively(current.right, attackThreshold, result, flag);
            }
        } else {
            if (current == null) {
                return result;
            }
            if (current.attackKey < attackThreshold) {
                result = current;
                return searchRecursively(current.right, attackThreshold, result, flag);
            } else {
                return searchRecursively(current.left, attackThreshold, result, flag);
            }
        }
    }


    // first level of invocation when a card is inserted into the DECK. deck is an AttackTree. also works when stats are updated after removal and returning to deck.
    public void insert(Card card) {
        root = insertRecursively(root, card);
    }
    private AttackNode insertRecursively(AttackNode current, Card card) {
        if (current == null) {
            if (current == root) {
                root = new AttackNode(card);
                return root;
            }
            return new AttackNode(card);
        }
        if (card.Acur == current.attackKey) {
            current.healthTree.insert(card); //
            return current;
        } else if (card.Acur < current.attackKey) {
            current.left = insertRecursively(current.left, card);
        } else {
            current.right = insertRecursively(current.right, card);
        }
        updateHeight(current);
        int balance = balance(current);
        if (balance > 1 && card.Acur < current.left.attackKey) {
            return rightRotate(current);
        }
        if (balance < -1 && card.Acur > current.right.attackKey) {
            return leftRotate(current);
        }
        if (balance > 1 && card.Acur > current.left.attackKey) {
            current.left = leftRotate(current.left);
            return rightRotate(current);
        }
        if (balance < -1 && card.Acur < current.right.attackKey) {
            current.right = rightRotate(current.right);
            return leftRotate(current);
        }

        return current;
    }


    // rotations are explained in HealthTree
    private AttackNode rightRotate(AttackNode y) {
        AttackNode x = y.left;
        AttackNode T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private AttackNode leftRotate(AttackNode x) {
        AttackNode y = x.right;
        AttackNode T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // we invoke this when the battling card is found or a card is being stolen
    public void remove(Card card, AttackNode attackNode, HealthNode healthNode) {
        healthNode.removeCard();                            // this invokes the method in healthNode, firstIndex++
        if (!healthNode.hasAvailableCards()) {              // lack of available cards -> firstIndex==lastIndex
            attackNode.healthTree.removeNode(healthNode);   // no more cards with said attack&health value, remove HealthNode from the HealthTree associated with this AttackNode
            if (attackNode.healthTree.root == null) {       // if even the HealthTree is emptied, no more cards with said Acur, remove the node from AttackTree
                removeNode(attackNode);
            }
        }
    }

    // removing an AttackNode from the AttackTree a.k.a. the deck
    // checking subtrees rules apply also here.
    public void removeNode(AttackNode node) {
        root = removeRecursively(root, node.attackKey);
    }
    private AttackNode removeRecursively(AttackNode current, int attackKey) {
        if (current == null) {
            return null;
        }
        if (attackKey < current.attackKey) {
            current.left = removeRecursively(current.left, attackKey);
        } else if (attackKey > current.attackKey) {
            current.right = removeRecursively(current.right, attackKey);
        } else {
            if ((current.left == null) || (current.right == null)) {
                AttackNode temp = null;
                if (current.left == null) {
                    temp = current.right;
                } else {
                    temp = current.left;
                }
                    current = temp;
            } else {
                AttackNode temp = current.right;
                while (temp.left != null) {
                    temp = temp.left;
                }
                current.attackKey = temp.attackKey;
                current.healthTree = temp.healthTree;
                temp = null;
            }
        }
        if (current == null) {
            return current;
        }

        // balancing after the removal
        updateHeight(current);
        int balance = balance(current);
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
