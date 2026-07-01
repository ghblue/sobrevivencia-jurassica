package jogo.dinossauros;

import jogo.modelo.Position;

/**
 * Dinossauro especial, mais resistente, imóvel e com ataque mais forte.
 */
public class TRex extends Dinosaur {
    public static final int INITIAL_HEALTH = 3;
    public static final String VISUAL_SYMBOL = "R";

    public TRex(Position currentPosition) {
        super("TRex", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Um tipo especial e colossal. Não é possível matá-lo com as mãos nuas, e por ser muito grande ele não se move pelo cenário. É aconselhável que o jogador procure por armas antes de enfrentá-lo, ou estará em um beco sem saída!";
    }

    // Faz o ataque do T-Rex retirar dois pontos de saúde.
    @Override
    public int getAttackDamage() {
        // O T-Rex causa dois pontos de dano quando o jogador nao consegue desviar.
        return 2;
    }

    // Mantém o T-Rex parado durante o turno de movimentação.
    @Override
    public int getMovementStepCount() {
        // O T-Rex permanece parado durante o turno de movimentacao.
        return 0;
    }

    // Bloqueia qualquer dano causado por ataques com as mãos.
    @Override
    public boolean canTakeUnarmedDamage() {
        // O T-Rex não pode ser derrotado com as mãos, conforme a regra do jogo.
        return false;
    }

    @Override
    public Dinosaur copy() {
        TRex copy = new TRex(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
