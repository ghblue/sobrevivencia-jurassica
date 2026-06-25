package game;

public enum MovementDirection {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1);

    private final int rowChange;
    private final int columnChange;

    MovementDirection(int rowChange, int columnChange) {
        this.rowChange = rowChange;
        this.columnChange = columnChange;
    }

    public Position getNextPosition(Position currentPosition) {
        return new Position(
                currentPosition.getRow() + rowChange,
                currentPosition.getColumn() + columnChange
        );
    }
}
