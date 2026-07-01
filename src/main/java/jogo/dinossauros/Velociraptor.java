package jogo.dinossauros;

import jogo.modelo.Position;

/**
 * Dinossauro ágil que se movimenta duas vezes por turno e desvia de dardos.
 */
public class Velociraptor extends Dinosaur {
    public static final int INITIAL_HEALTH = 2;
    public static final String VISUAL_SYMBOL = "V";

    public Velociraptor(Position currentPosition) {
        super("Velociraptor", INITIAL_HEALTH, currentPosition, VISUAL_SYMBOL);
    }

    @Override
    public String getDescription() {
        return "Esse dinossauro é semelhante ao tipo comum, com apenas duas diferenças: ele se move rapidamente pelo mapa, e por ser mais ágil, é muito difícil acertá-lo utilizando uma arma de disparo.";
    }

    // Faz o Velociraptor tentar dois movimentos em cada turno inimigo.
    @Override
    public int getMovementStepCount() {
        // A velocidade especial permite duas tentativas de movimento por turno.
        return 2;
    }

    // Impede que o Velociraptor seja atingido pelo dardo tranquilizante.
    @Override
    public boolean canBeHitByTranquilizer() {
        // A agilidade do Velociraptor sempre impede o acerto do dardo tranquilizante.
        return false;
    }

    @Override
    public Dinosaur copy() {
        Velociraptor copy = new Velociraptor(getCurrentPosition());
        copy.setHealth(getHealth());
        return copy;
    }
}
