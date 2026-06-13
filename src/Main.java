
/**
 * This skeleton provides file I/O infrastructure. Implement your game logic
 * as you wish. There are some import that is suggested to use written below. 
 * You can use them freely and create as manys classes as you want. However, 
 * you cannot import any other java.util packages with data structures, you
 * need to implement them yourself. For other imports, ask through Moodle before 
 * using.
 * 
 * TESTING YOUR SOLUTION:
 * ======================
 * 
 * Use the Python test runner for automated testing:
 * 
 * python test_runner.py              # Test all cases
 * python test_runner.py --type type1 # Test only type1  
 * python test_runner.py --type type2 # Test only type2
 * python test_runner.py --verbose    # Show detailed diffs
 * python test_runner.py --benchmark  # Performance testing (no comparison)
 * 
 * Flags can be combined, e.g.:
 * python test_runner.py -bv              # benchmark + verbose
 * python test_runner.py -bv --type type1 # benchmark + verbose + type1
 * python test_runner.py -b --type type2  # benchmark + type2
 * 
 * MANUAL TESTING (For Individual Runs):
 * ======================================
 * 
 * 1. Compile: cd src/ && javac *.java
 * 2. Run: java Main ../testcase_inputs/test.txt ../output/test.txt
 * 3. Compare output with expected results
 * 
 * PROJECT STRUCTURE:
 * ==================
 * 
 * project_root/
 * ├── src/                     # Your Java files (Main.java, etc.)
 * ├── testcase_inputs/         # Input test files  
 * ├── testcase_outputs/        # Expected output files
 * ├── output/                  # Generated outputs (auto-created)
 * └── test_runner.py           # Automated test runner
 * 
 * REQUIREMENTS:
 * =============
 * - Java SDK 8+ (javac, java commands)
 * - Python 3.6+ (for test runner)
 * 
 */

import java.io.*;
import java.util.Scanner;


public class Main {

    // the approach adopted is as follows: two kinds of Tree and Node class are implemented: AttackNode/Tree and HealthNode/Tree
    // the deck is a static AttackTree that takes attackKey as key. the value it holds in its AttackNode is an AVL tree type HealthTree
    // each node of HealthTree holds an ArrayList of cards with identical Acur & Hcurs with respect to their order of entering to the ArrayList in a queue-manner

    static AttackTree deck = new AttackTree();
    static int strangerScore = 0;
    static int survivorScore = 0;
    static int deckCount = 0;

    public static String draw_card(String name, int att, int hp) {
        Card newCard = new Card(name, att, hp);                                         // initialize input card and insert in the deck
        deck.insert(newCard);
        deckCount++;
        String tempst = "Added " + name + " to the deck\n";
        return tempst;
    }

    public static String battle(int att, int hp, int heal) {
        // first priority:
        // find Hcur > att and Acur > hp for min Acur and then min Hcur
        String tempst;
        Card temporary = null;                                                          // these temporaries will hold the current optimal card we've encountered so far
        HealthNode temporaryHealthNode = null;
        AttackNode temporaryAttackNode = null;
        Card winningCard = null;                                                        // when we manage to find a temp matching our priority, we break out and assign to winningCard
        AttackNode attackNode = deck.searchMinGT(hp);                                   // start by searching the minimum attack <= enemy hp. this means you can kill.
        while (attackNode != null) {                                                    // unless you can't find it of course. then you cannot kill and skip the while loop.
            HealthNode healthNode = attackNode.healthTree.searchMinGT(att);             // skipping the loop takes you to non-nullness check which you won't pass and move on to priority 2.
            if (healthNode != null) {                                                   // look for the min health card that survives for our first preference attack value.
                if (healthNode.hasAvailableCards()) {                                   // if you find one, take min health. if you don't, means there is no suitable one for that attack value.
                    int firstIndex = healthNode.getFirstIndex();                        // to survive, you need to up your attack value.
                    temporary = healthNode.cards.get(firstIndex);
                    temporaryAttackNode = attackNode;
                    temporaryHealthNode = healthNode;
                    break;
                // found a winning card
                }
            } 
            attackNode = deck.searchMinGT(attackNode.attackKey + 1);                    // if your healthNode is null a.k.a no suitable health for said attack:
        }                                                                               // find minimum attack card that is greater than but not equal to the attack value we were checking for
        // We must have the optimal card for first priority if exists
        if (temporary != null) {
            winningCard = temporary;
            deck.remove(winningCard, temporaryAttackNode, temporaryHealthNode);         // remove from deck since it is used.
            tempst = "Found with priority " + 1 + ", Survivor plays " + winningCard.name + ", the played card returned to deck, 0 cards revived\n";
            winningCard.Hcur -= att;                                                    // update stats
            winningCard.Acur = Math.max(1, ((winningCard.Abase * winningCard.Hcur)/winningCard.Hbase));
            deck.insert(winningCard);                                                   // return the updated card back to the deck from the end.
            survivorScore+=2;                                                           // score points
            strangerScore++;
            return tempst;
        }
        else {
            // second priority:
            // we want Hcur > att and Acur < hp for max Acur and then min Hcur
            attackNode = deck.searchMaxLT(hp);                                          // similar computations to the explanation above
            while (attackNode != null) {
                HealthNode healthNode = attackNode.healthTree.searchMinGT(att);
                if (healthNode != null) {
                    if (healthNode.hasAvailableCards()) {
                        int firstIndex = healthNode.getFirstIndex();
                        temporary = healthNode.cards.get(firstIndex);
                        temporaryAttackNode = attackNode;
                        temporaryHealthNode = healthNode;
                        break;
                    // found a winning card
                    }
                }
                attackNode = deck.searchMaxLT(attackNode.attackKey);
            }
            // optimal card for second priority if exists
            if (temporary != null) {
                winningCard = temporary;
                deck.remove(winningCard, temporaryAttackNode, temporaryHealthNode);
                tempst = "Found with priority " + 2 + ", Survivor plays " + winningCard.name + ", the played card returned to deck, 0 cards revived\n";
                winningCard.Hcur -= att;
                winningCard.Acur = Math.max(1, ((winningCard.Abase * winningCard.Hcur)/winningCard.Hbase));
                deck.insert(winningCard);
                survivorScore++;
                strangerScore++; 
                return tempst;
            }
            else {
                // third priority:
                // we want Hcur <= att and Acur >= hp for min Acur and then min Hcur
                // death is inevitable, focus on the smallest attack that'll kill, then smallest Hcur
                attackNode = deck.searchMinGT(hp);
                while (attackNode != null) {
                    HealthNode healthNode = attackNode.healthTree.searchMinGT(0);   // this is because we will die anyway and we prefer to waste less Hcur over more Hcur as long as we kill
                    if (healthNode != null) {
                        if (healthNode.hasAvailableCards()) {
                            int firstIndex = healthNode.getFirstIndex();
                            temporary = healthNode.cards.get(firstIndex);
                            temporaryAttackNode = attackNode;
                            temporaryHealthNode = healthNode;
                            break;
                        // found a winning card
                        }
                } 
                    attackNode = deck.searchMinGT(attackNode.attackKey+1);
                }
                // optimal card for priority three, if exists
                if (temporary != null) {
                    winningCard = temporary;
                    deck.remove(winningCard, temporaryAttackNode, temporaryHealthNode);     // this time around, we will not be returning the card to the deck since it dies. it is discarded
                    tempst = "Found with priority " + 3 + ", Survivor plays " + winningCard.name + ", the played card is discarded, 0 cards revived\n";
                    winningCard.Hcur = 0;
                    survivorScore+=2;
                    strangerScore+=2;
                    deckCount--;
                    return tempst;
                }


                else {
                    // fourth priority:
                    // we want largest Acur possible to maximize damage, then min Hcur
                    attackNode = deck.searchMaxLT(Integer.MAX_VALUE);                                   // we have no bounds, all we want is the maximum damage
                    while (attackNode != null) {
                        HealthNode healthNode = attackNode.healthTree.searchMinGT(0);   // it's already settled that we're dying, out of the highest damaging cards we want least Hcur to be wasted
                        if (healthNode != null) {
                            if (healthNode.hasAvailableCards()) {
                                int firstIndex = healthNode.getFirstIndex();
                                temporary = healthNode.cards.get(firstIndex);
                                temporaryAttackNode = attackNode;
                                temporaryHealthNode = healthNode;
                                break;
                            // found a winning (if you call it winning) card
                            }
                    } 
                        attackNode = deck.searchMaxLT(attackNode.attackKey);
                    }
                    // optimal card for priority four, if exists
                    if (temporary != null) {
                        winningCard = temporary;
                        deck.remove(winningCard, temporaryAttackNode, temporaryHealthNode);             // obviously discarded, don't insert again
                        tempst = "Found with priority " + 4 + ", Survivor plays " + winningCard.name + ", the played card is discarded, 0 cards revived\n";
                        winningCard.Hcur = 0;
                        survivorScore++;
                        strangerScore+=2;
                        deckCount--;
                        return tempst;

                    }
                    // if even that doesn't exist, we have no cards to play
                    else { tempst = "No cards to play, 0 cards revived\n";
                        return tempst;}

                }

            }
                        
        }
    
    }

    public static String findWinning() {
        String tempst;
        if (strangerScore > survivorScore) {
            tempst = "The Stranger, Score: " + strangerScore + "\n";
        }
        else {
            tempst = "The Survivor, Score: " + survivorScore + "\n";
        }
        return tempst;
    }

    public static String deckCount() {
        String tempst = Integer.toString(deckCount);
        return "Number of cards in the deck: " + tempst + "\n";
    }

    public static String steal_card(int att, int hp) {
        // for this, we will use a similar search algorithm to that in battle method.
        String tempst = "No card to steal\n";
        Card temporary = null;
        HealthNode temporaryHealthNode = null;
        AttackNode temporaryAttackNode = null;
        Card stolenCard = null;
        AttackNode attackNode = deck.searchMinGT(att+1);                                        // our attack minimum greater than threshold search is killing motivated, so it accepts equal cases
        while (attackNode != null) {                                                            // we should look for the values greter than equal to its version incremented by one
            HealthNode healthNode = attackNode.healthTree.searchMinGT(hp);                      // but health searcher works literally. no need for incrementation.
            if (healthNode != null) {
                if (healthNode.hasAvailableCards()) {
                    int firstIndex = healthNode.getFirstIndex();
                    temporary = healthNode.cards.get(firstIndex);
                    temporaryHealthNode = healthNode;
                    temporaryAttackNode = attackNode;
                    break;
                }
            }
            attackNode = deck.searchMinGT(attackNode.attackKey + 1);                            // invoke this if the smallest Acur Stranger can steal from Survivor has no suitable Hcur value
        }                                                                                       // traverse health values in the tree of next AttackNode    
        if (temporary != null) {
            stolenCard = temporary;
            deck.remove(stolenCard, temporaryAttackNode, temporaryHealthNode);
            tempst = "The Stranger stole the card: " + stolenCard.name + "\n";
            deckCount--; }
        return tempst;                                                                          // temporary == null -> no suitable cards to steal. return base case
    }




    public static void main(String[] args) {
        // Check command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            System.out.println("Example: java Main ../testcase_inputs/test.txt ../output/test.txt");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        // Initialize file reader
        Scanner reader = null;
        try {
            reader = new Scanner(new File(inFile));
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + inFile);
            e.printStackTrace();
            return;
        }

        // Initialize file writer
        FileWriter writer = null;
        try {
            writer = new FileWriter(outFile);
        } catch (IOException e) {
            System.out.println("Writing error: " + outFile);
            e.printStackTrace();
            if (reader != null)
                reader.close();
            return;
        }


        

        // Process commands line by line
        try {
            while (reader.hasNext()) {
                String line = reader.nextLine();
                Scanner scanner = new Scanner(line);
                String command = scanner.next();
                String out = "";

                switch (command) {
                    case "draw_card": {
                        String name = "";
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            name = scanner.next();
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = draw_card(name, att, hp); // suggested method for draw_card command
                        break;
                    }
                    case "battle": {
                        int att = 0;
                        int hp = 0;
                        int heal = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        if (scanner.hasNext())
                            heal = scanner.nextInt();
                        out = battle(att, hp, heal); // suggested method for battle command
                        break;
                    }
                    case "find_winning": {
                        out = findWinning(); // suggested method for find_winning command
                        break;
                    }
                    case "deck_count": {
                        out = deckCount(); // suggested method for deck_count command
                        break;
                    }

                    /*
                     * Comment this out if you are going to implement type-2 commands
                     * case "discard_pile_count": {
                     * out = discardPileCount(); // suggested method for discard_pile_count command
                     * break;
                     * }
                     */
                    case "steal_card": {
                        int att = 0;
                        int hp = 0;
                        if (scanner.hasNext())
                            att = scanner.nextInt();
                        if (scanner.hasNext())
                            hp = scanner.nextInt();
                        out = steal_card(att, hp); // suggested method for steal_card command
                        break;
                    }
                    default: {
                        System.out.println("Invalid command: " + command);
                        scanner.close();
                        writer.close();
                        reader.close();
                        return;
                    }
                }

                scanner.close();

                try {
                    writer.write(out);
                    // writer.write("\n"); // uncomment if each output needs to be in a new line and
                    // you did not implement that inside the functions.
                } catch (IOException e2) {
                    System.out.println("Writing error");
                    e2.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Error processing commands: " + e.getMessage());
            e.printStackTrace();
        }

        // Clean up resources
        try {
            writer.close();
        } catch (IOException e2) {
            System.out.println("Writing error");
            e2.printStackTrace();
        }

        if (reader != null) {
            reader.close();
        }

        System.out.println("end");
        return;
    }
}
