package game;

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
