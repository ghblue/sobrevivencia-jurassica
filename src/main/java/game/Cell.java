package game;

import java.util.Objects;

public class Cell {
    private final String emptySymbol;
    private String primarySymbol;
    private String secondarySymbol;

    public Cell(String symbol) {
        this.emptySymbol = Objects.requireNonNull(symbol, "O simbolo vazio e obrigatorio.");
        this.primarySymbol = symbol;
        this.secondarySymbol = symbol;
    }

    public String getSymbol() {
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
