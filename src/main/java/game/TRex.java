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

    @Override
    public int getAttackDamage() {
        // O T-Rex causa dois pontos de dano quando o jogador nao consegue desviar.
        return 2;
    }

    @Override
    public int getMovementStepCount() {
        // O T-Rex permanece parado durante o turno de movimentacao.
        return 0;
    }

    @Override
    public boolean canTakeUnarmedDamage() {
        // Ataques com as maos nao atravessam a resistencia do T-Rex.
        return false;
    }

    @Override
    public Dinosaur copy() {
        TRex copy = new TRex(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
