package jogo.dinossauros;

import jogo.modelo.Movable;
import jogo.modelo.Position;

// Base dos dinossauros que realmente podem mudar de posição no tabuleiro.
public abstract class MovableDinosaur extends Dinosaur implements Movable {
    private static final int DEFAULT_MOVEMENT_STEP_COUNT = 1;

    protected MovableDinosaur(String name, int health, Position currentPosition, String visualSymbol) {
        super(name, health, currentPosition, visualSymbol);
    }

    @Override
    public void moveTo(Position newPosition) {
        setCurrentPosition(newPosition);
    }

    public int getMovementStepCount() {
        return DEFAULT_MOVEMENT_STEP_COUNT;
    }
}
