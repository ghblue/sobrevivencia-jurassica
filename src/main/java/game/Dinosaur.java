package game;

import java.util.Objects;

public abstract class Dinosaur {
    private final String name;
    private int health;
    private Position currentPosition;
    private final String visualSymbol;

    protected Dinosaur(String name, int health, Position currentPosition, String visualSymbol) {
        this.name = Objects.requireNonNull(name, "O nome e obrigatorio.");
        setHealth(health);
        setCurrentPosition(currentPosition);
        this.visualSymbol = Objects.requireNonNull(visualSymbol, "O simbolo visual e obrigatorio.");
    }

    public String getName() {
        return name;
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

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = Objects.requireNonNull(currentPosition, "A posicao atual e obrigatoria.");
    }

    public String getVisualSymbol() {
        return visualSymbol;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("O dano nao pode ser negativo.");
        }

        setHealth(Math.max(0, health - damage));
    }

    public abstract String getDescription();
}
