package game;

import java.util.Objects;

public abstract class Dinosaur implements Movable {
    private static final int DEFAULT_ATTACK_DAMAGE = 1;
    private static final int DEFAULT_MOVEMENT_STEP_COUNT = 1;

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

    public final void setHealth(int health) {
        if (health < 0) {
            throw new IllegalArgumentException("A saude nao pode ser negativa.");
        }

        this.health = health;
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    private void setCurrentPosition(Position currentPosition) {
        this.currentPosition = Objects.requireNonNull(currentPosition, "A posicao atual e obrigatoria.");
    }

    @Override
    public void moveTo(Position newPosition) {
        setCurrentPosition(newPosition);
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

    public int getAttackDamage() {
        return DEFAULT_ATTACK_DAMAGE;
    }

    public int getMovementStepCount() {
        return DEFAULT_MOVEMENT_STEP_COUNT;
    }

    public boolean canTakeUnarmedDamage() {
        return true;
    }

    public boolean canBeHitByTranquilizer() {
        return true;
    }

    public abstract String getDescription();

    public abstract Dinosaur copy();
}
