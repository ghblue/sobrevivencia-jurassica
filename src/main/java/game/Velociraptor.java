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

    @Override
    public int getMovementStepCount() {
        // O Velociraptor tenta dois movimentos em cada turno dos dinossauros.
        return 2;
    }

    @Override
    public boolean canBeHitByTranquilizer() {
        // Sua agilidade permite desviar do dardo tranquilizante.
        return false;
    }

    @Override
    public Dinosaur copy() {
        Velociraptor copy = new Velociraptor(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
