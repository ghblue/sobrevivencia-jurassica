package jogo.util;

import java.util.Objects;
import java.util.Random;

/**
 * Encapsula as rolagens aleatórias usadas nos ataques e testes de percepção.
 */
public class Dice {
    private final Random random;

    public Dice(Random random) {
        this.random = Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
    }

    // Simula o dado de seis faces usado nos ataques do jogador.
    public int rollD6() {
        // Produz valores inclusivos de 1 a 6 para os ataques do jogador.
        return random.nextInt(6) + 1;
    }

    // Simula o dado de três faces usado no teste de percepção.
    public int rollD3() {
        // Produz valores inclusivos de 1 a 3 para o teste de esquiva.
        return random.nextInt(3) + 1;
    }
}
