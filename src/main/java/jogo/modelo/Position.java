package jogo.modelo;

import java.util.Objects;

/**
 * Coordenada imutável formada por linha e coluna.
 */
public class Position {
    private final int row;
    private final int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object object) {
        // Posições com a mesma linha e coluna representam a mesma casa do tabuleiro.
        if (this == object) {
            return true;
        }

        if (!(object instanceof Position)) {
            return false;
        }

        Position position = (Position) object;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        // O hash permite usar Position corretamente em conjuntos como occupiedPositions.
        return Objects.hash(row, column);
    }
}
