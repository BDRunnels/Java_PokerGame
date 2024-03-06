import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class PokerGame {
    
    private final List<Card> deck = Card.getStandardDeck();
    private int playerCount;
    private int cardsInHand;
    private List<PokerHand> pokerHands;
    private List<Card> remainingCards;

    public PokerGame(int playerCount, int cardsInHand) {
        this.playerCount = playerCount;
        this.cardsInHand = cardsInHand;
        pokerHands = new ArrayList<>(cardsInHand);
    }

    public void startPlay() {
        Collections.shuffle(deck);
        System.out.println("\nDECK SHUFFLED");
        Card.printDeck(deck);
        int randomMiddle = new Random().nextInt(15, 35);
        Collections.rotate(deck, randomMiddle);
        System.out.println("\nDECK SHUFFLED AND CUT");
        Card.printDeck(deck);

        deal();
        System.out.println("-".repeat(30));
        Consumer<PokerHand> checkHand = PokerHand::evalHand;
        pokerHands.forEach(checkHand.andThen(System.out::println));

        // determining cards left in deck after deal()
        int cardsDealt = playerCount * cardsInHand;
        int cardsRemaining = deck.size() - cardsDealt;

        // fills remainingCards with null references using cardsRemaining as the total size.
        remainingCards = new ArrayList<>(Collections.nCopies(cardsRemaining, null));
        // replaceAll null values in remainingCards with the appropriate card from the deck, starting from cardsDealt + 0 
            // the deck dealt 20 cards (4 players * 5 cards), so first card in remainingCards is card at index 20 (deck dealt indexes 0-19).
        remainingCards.replaceAll(c -> deck.get(cardsDealt + remainingCards.indexOf(c)));
        Card.printDeck(remainingCards, "Remaining Cards:", 2);
    }

    private void deal() {
        Card[][] hands = new Card[playerCount][cardsInHand];
        for (int deckIndex = 0, i = 0; i < cardsInHand; i++) {
            for (int j = 0; j < playerCount; j++) {
                hands[j][i] = deck.get(deckIndex++);
            }
        }

        int playerNumber = 1;
        for (Card[] hand : hands) {
            pokerHands.add(new PokerHand(playerNumber++, Arrays.asList(hand)));
        }
    }
}
