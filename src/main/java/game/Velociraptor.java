package game;

public class Velociraptor extends Dinosaur {
    public static final int INITIAL_HEALTH = 2;
    public static final String VISUAL_SYMBOL = "V";

    public Velociraptor(Position currentPosition) {
        super("Velociraptor", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Predador agil que representa perigo direto para o jogador.";
    }
}
