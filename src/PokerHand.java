import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PokerHand {
    private List<Card> handDealt;
    private List<Card> keepers;
    private List<Card> discards;
    private Ranking score = Ranking.NONE;
    private int playerNumber;

    public PokerHand(int playerNumber, List<Card> handDealt) {
        handDealt.sort(Card.sortRankReversedSuit());
        this.handDealt = handDealt;
        this.playerNumber = playerNumber;
        keepers = new ArrayList<>(handDealt.size());
        discards = new ArrayList<>(handDealt.size());
    }

    @Override
    public String toString() {
        return "%d. %-16s Rank:%d %-40s Best:%-7s Worst:%-6s %s".formatted(
            playerNumber, 
            score, 
            score.ordinal(), 
            handDealt,
            Collections.max(handDealt, Comparator.comparing(Card::rank)),
            Collections.min(handDealt, Comparator.comparing(Card::rank)),
            (discards.size() > 0 ? "Discards: " + discards : ""));
    }

    private void setRank(int faceCount) {

        switch(faceCount) {
            case 4 -> score = Ranking.FOUR_OF_A_KIND;
            case 3 -> {
                if (score == Ranking.NONE) score = Ranking.THREE_OF_A_KIND;
                else score = Ranking.FULL_HOUSE;
            }
            case 2 -> {
                if (score == Ranking.NONE) score = Ranking.ONE_PAIR;
                else if (score == Ranking.THREE_OF_A_KIND) score = Ranking.FULL_HOUSE;
                else score = Ranking.TWO_PAIR;
            }
        }
    }
 
    public void evalHand() {
        List<String> faceList = new ArrayList<>(handDealt.size());
        handDealt.forEach(card -> faceList.add(card.face()));

        List<String> duplicateFaceCards = new ArrayList<>();
        faceList.forEach(face -> {
            if (!duplicateFaceCards.contains(face) && Collections.frequency(faceList, face) > 1) {
                duplicateFaceCards.add(face);
            }
        });

        for (String duplicate : duplicateFaceCards) {
            int start = faceList.indexOf(duplicate);
            int last = faceList.lastIndexOf(duplicate);
            setRank(last - start + 1);
            List<Card> sub = handDealt.subList(start, last + 1);
            keepers.addAll(sub);
        }

        pickDiscards();
    }

    private void pickDiscards() {
        List<Card> temp = new ArrayList<>(handDealt);
        temp.removeAll(keepers);

        int rankedCards = keepers.size();
        Collections.reverse(temp); // so lower ranked cards are first.
        int index = 0;
        for (Card c : temp) {
            if (index++ < 3 && (rankedCards > 2 || c.rank() < 9)) discards.add(c);
            else keepers.add(c);
        }
    }
}
