package jogo.modelo;

import java.util.Objects;

/**
 * Representa uma casa do tabuleiro.
 * O símbolo principal guarda personagens ou paredes, enquanto o secundário
 * permite manter uma caixa de suprimentos sob o jogador.
 */
public class Cell {
    private final String emptySymbol;
    private String primarySymbol;
    private String secondarySymbol;

    public Cell(String symbol) {
        this.emptySymbol = Objects.requireNonNull(symbol, "O simbolo vazio e obrigatorio.");
        // A camada principal guarda jogador, paredes e dinossauros.
        this.primarySymbol = symbol;
        // A camada secundária permite manter uma caixa sob outro elemento.
        this.secondarySymbol = symbol;
    }

    public String getSymbol() {
        // A camada principal tem prioridade visual sobre a caixa de suprimentos.
        if (!emptySymbol.equals(primarySymbol)) {
            return primarySymbol;
        }

        return secondarySymbol;
    }

    public void setSymbol(String symbol) {
        setPrimarySymbol(symbol);
    }

    public String getPrimarySymbol() {
        return primarySymbol;
    }

    public String getSecondarySymbol() {
        return secondarySymbol;
    }

    public void setPrimarySymbol(String symbol) {
        this.primarySymbol = Objects.requireNonNull(symbol, "O simbolo principal e obrigatorio.");
    }

    public void setSecondarySymbol(String symbol) {
        this.secondarySymbol = Objects.requireNonNull(symbol, "O simbolo secundario e obrigatorio.");
    }
}
