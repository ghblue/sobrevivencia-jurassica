package jogo.dinossauros;

import jogo.modelo.Position;

/**
 * Dinossauro de menor resistência, com um ponto de saúde.
 */
public class Compsognathus extends Dinosaur {
    public static final int INITIAL_HEALTH = 1;
    public static final String VISUAL_SYMBOL = "C";

    public Compsognathus(Position currentPosition) {
        super("Compsognathus", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "O tipo mais simples de inimigo. São pequenos e podem ser facilmente eliminados até mesmo sem armamentos. Entretanto, é necessário tomar cuidado, pois eles costumam esgueirar-se por lugares pequenos, e podem pegar o jogador de surpresa.";
    }

    @Override
    public Dinosaur copy() {
        Compsognathus copy = new Compsognathus(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
