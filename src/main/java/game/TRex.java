package game;

public class TRex extends Dinosaur {
    public static final int INITIAL_HEALTH = 3;
    public static final String VISUAL_SYMBOL = "R";

    public TRex(Position currentPosition) {
        super("TRex", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Grande predador dominante, resistente e extremamente perigoso.";
    }
}
