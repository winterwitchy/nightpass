# nightpass, a card game engine

**nightpass** is a high-performance Java application designed to simulate a card game between two players — the Survivor and the Stranger. It efficiently manages a shared deck of cards and selects the optimal card for each battle using a priority-based strategy and custom-built data structures.

The engine handles dynamic card stat updates after combat, score tracking, and card stealing mechanics, all while maintaining O(log n) query performance across two dimensions simultaneously.

## Features

- **Nested AVL Tree Deck:** The deck is an `AttackTree` (outer AVL keyed by attack) where each node holds a `HealthTree` (inner AVL keyed by health), enabling fast range queries across both stats at once.
- **Priority-Based Card Selection:** Each battle evaluates up to 4 priorities — survive and kill, survive but not kill, die but kill, die and deal max damage — picking the optimal card accordingly.
- **Dynamic Stat Updates:** Cards that survive combat have their health and attack recalculated proportionally and are reinserted into the deck.
- **Card Stealing:** The Stranger can steal cards exceeding both attack and health thresholds from the Survivor's deck.
- **Score Tracking:** Points are awarded to each player depending on battle outcome and priority used.

## Technical Implementation

Custom implementation of core data structures without standard Java Collections (except `ArrayList`).

### Data Structures

- **Custom AVL Tree (`AttackTree.java`, `HealthTree.java`):**
  - Standard AVL balancing with rotations after insertion and deletion.
  - Supports `searchMinGT` (minimum key ≥ threshold) and `searchMaxLT` (maximum key < threshold) for range-based card lookups.
  - Each `AttackNode` holds its own `HealthTree`, forming a nested two-dimensional search structure.

- **Card Queue (`HealthNode.java`):**
  - Each `HealthNode` holds an `ArrayList` of cards with identical attack and health values.
  - Managed as a queue via `firstIndex` and `lastIndex` pointers for O(1) enqueue and dequeue.

### Object-Oriented Design

- **Encapsulation:** `Card`, `AttackNode`, `HealthNode`, `AttackTree`, `HealthTree` are cleanly separated by responsibility.
- **Command Pattern:** `Main.java` parses a stream of string commands and dispatches to the appropriate game logic method.

## Project Structure

- `Main.java` — Entry point. Parses input, processes commands (`draw_card`, `battle`, `steal_card`, etc.), manages scoring.
- `AttackTree.java` — Outer AVL tree keyed by current attack value.
- `AttackNode.java` — Holds attack key and an inner `HealthTree`.
- `HealthTree.java` — Inner AVL tree keyed by current health value.
- `HealthNode.java` — Holds health key and a queue of cards with matching stats.
- `Card.java` — Card model with initial, base, and current attack/health values.

## Installation & Usage

1. **Clone the repository:**
```
git clone https://github.com/winterwitchy/nightpass-survivor.git
cd nightpass-survivor
```

2. **Compile:**
```
cd src && javac *.java
```

3. **Run:**
```
java Main <input_file> <output_file>
```

### Example Input

```
draw_card Dragon 50 120
draw_card Goblin 10 30
battle 40 45 0
find_winning
deck_count
```
