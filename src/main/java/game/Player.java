package game;

import java.util.Objects;

public class Player {
    public static final int INITIAL_HEALTH = 5;

    private int health;
    private int perception;
    private Position currentPosition;

    public Player(int health, int perception, Position currentPosition) {
        setHealth(health);
        setPerception(perception);
        setCurrentPosition(currentPosition);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) {
            throw new IllegalArgumentException("A saude nao pode ser negativa.");
        }

        this.health = health;
    }

    public int getPerception() {
        return perception;
    }

    public void setPerception(int perception) {
        if (perception < 0) {
            throw new IllegalArgumentException("A percepcao nao pode ser negativa.");
        }

        this.perception = perception;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = Objects.requireNonNull(currentPosition, "A posicao atual e obrigatoria.");
    }
}
