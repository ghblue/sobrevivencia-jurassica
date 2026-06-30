package game;

public class Troodon extends Dinosaur {
    public static final int INITIAL_HEALTH = 2;
    public static final String VISUAL_SYMBOL = "T";

    public Troodon(Position currentPosition) {
        super("Troodon", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Dinossauro atento, inteligente e ameacador durante a exploracao.";
    }

    @Override
    public Dinosaur copy() {
        Troodon copy = new Troodon(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
