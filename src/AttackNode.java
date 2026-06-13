public class AttackNode {
    int attackKey;
    HealthTree healthTree;
    int height;
    AttackNode left;
    AttackNode right;

    // each node of the static AttackTree deck. every node holds an AVL tree, HealthTree type.
    // constructing the node means there is no node with this attackKey. initialize a new HealthTree within.
    // insert the card into that HealthTree, this will invoke insert(card) method in HealthTree class and insert recursively.
    public AttackNode(Card card) {
        this.attackKey = card.Acur;
        this.healthTree = new HealthTree();
        this.healthTree.insert(card);
        this.height = 1;
        this.left = null;
        this.right = null;
    }
    
}
