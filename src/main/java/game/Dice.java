package game;

import java.util.Objects;
import java.util.Random;

public class Dice {
    private final Random random;

    public Dice(Random random) {
        this.random = Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
    }

    public int rollD6() {
        return random.nextInt(6) + 1;
    }

    public int rollD3() {
        return random.nextInt(3) + 1;
    }
}
