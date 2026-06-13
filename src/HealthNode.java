import java.util.ArrayList;

public class HealthNode {
    int healthKey;
    ArrayList<Card> cards;                                  // ArrayList of cards of same Acur and Hcur values.
    int height;
    HealthNode left;
    HealthNode right;
    int firstIndex = -1;
    int lastIndex = -1;

    // HealthNode constructor is invoked when there is not a node with healthKey equal to card.Hcur
    public HealthNode(Card card) {
        this.healthKey = card.Hcur;
        this.cards = new ArrayList<>();                     // cards with this value will be held in cards WRT their entry order
        this.cards.add(card); 
        this.height = 1;                                    // new node is initially added at leaf
        this.left = null;
        this.right = null;
        this.firstIndex = 0;                                // index of earliest entering && still in use card.
        this.lastIndex = 0;                                 // index of last entering card
        card.entryOrder = 0;                                // constructing card is obviously the first entered && still in use card at this point.
    }

    public void addCard(Card card) {
        this.cards.add(card);                               // the last step of inserting a card in the deck. Invoked when node already exists.
        this.lastIndex++;
        card.entryOrder = lastIndex;                        // added to cards as last entered
    }

    public int getFirstIndex() {
        return this.firstIndex;
    }

    public boolean hasAvailableCards() {
        return this.firstIndex <= this.lastIndex;
    }

    public void removeCard() {
        if (this.firstIndex <= this.lastIndex) {
            this.firstIndex++;
            
        }

    }
}
