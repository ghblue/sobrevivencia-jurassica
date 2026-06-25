package game;

public class Compsognathus extends Dinosaur {
    public static final int INITIAL_HEALTH = 1;
    public static final String VISUAL_SYMBOL = "C";

    public Compsognathus(Position currentPosition) {
        super("Compsognathus", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Dinossauro pequeno, rapido e perigoso em grupo.";
    }
}
