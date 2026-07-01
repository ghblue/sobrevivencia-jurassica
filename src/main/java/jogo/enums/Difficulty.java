package jogo.enums;

/**
 * Define a percepção inicial do jogador em cada nível de dificuldade.
 * Quanto maior a percepção, maior a chance de desviar dos ataques.
 */
public enum Difficulty {
    EASY(3),
    MEDIUM(2),
    HARD(1);

    private final int perception;

    Difficulty(int perception) {
        this.perception = perception;
    }

    public int getPerception() {
        return perception;
    }
}
