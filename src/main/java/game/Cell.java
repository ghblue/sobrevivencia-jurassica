package game;

import java.util.Objects;

public class Cell {
    private String symbol;

    public Cell(String symbol) {
        setSymbol(symbol);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = Objects.requireNonNull(symbol, "O simbolo e obrigatorio.");
    }
}
