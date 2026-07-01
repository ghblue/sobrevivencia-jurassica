package jogo.dinossauros;

import jogo.modelo.Position;

/**
 * Dinossauro comum que utiliza as regras padrão de ataque e movimento.
 */
public class Troodon extends Dinosaur {
    public static final int INITIAL_HEALTH = 2;
    public static final String VISUAL_SYMBOL = "T";

    public Troodon(Position currentPosition) {
        super("Troodon", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "O tipo comum de dinossauro. Uma criatura sem características especiais. É possível feri-lo com as mãos nuas apenas acertando golpes críticos, mas é recomendado utilizar uma arma para facilitar o trabalho.";
    }

    @Override
    public Dinosaur copy() {
        Troodon copy = new Troodon(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
