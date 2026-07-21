package jogo.dinossauros;

import java.util.Objects;
import jogo.enums.TipoAtaque;
import jogo.modelo.Position;

/**
 * Base comum dos dinossauros presentes no jogo.
 * Define saúde, posição e comportamentos que podem variar por espécie.
 */
public abstract class Dinosaur {
    private static final int DEFAULT_ATTACK_DAMAGE = 1;

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

    public Position getCurrentPosition() {
        return currentPosition;
    }

    protected final void setCurrentPosition(Position currentPosition) {
        this.currentPosition = Objects.requireNonNull(currentPosition, "A posicao atual e obrigatoria.");
    }

    public String getVisualSymbol() {
        return visualSymbol;
    }

    public boolean isAlive() {
        return health > 0;
    }

    // Reduz a saúde do dinossauro sem permitir valores negativos.
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("O dano nao pode ser negativo.");
        }

        setHealth(Math.max(0, health - damage));
    }

    public int getAttackDamage() {
        return DEFAULT_ATTACK_DAMAGE;
    }

    // Por padrão, dinossauros podem receber qualquer tipo de ataque do jogador.
    public boolean podeReceberAtaque(TipoAtaque tipoAtaque) {
        Objects.requireNonNull(tipoAtaque, "O tipo de ataque e obrigatorio.");
        return true;
    }

    public abstract String getDescription();

    public abstract Dinosaur copy();
}
