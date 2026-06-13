public class Card {
    public String name;
    public int Ainit, Abase, Acur;
    public int Hinit, Hbase, Hcur;
    public int entryOrder;
    // entryOrder is the position of this card in the ArrayList it is stored in.

    // card constructor, used in draw_card method of Main
    public Card(String name, int attack, int health) {
        this.name = name;
        this.Ainit = attack;
        this.Hinit = health;
        Abase = Ainit;
        Hbase = Hinit;
        Acur = Abase;
        Hcur = Hbase;
    }
}
