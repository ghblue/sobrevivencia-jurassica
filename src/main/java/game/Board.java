package game;

public class Board {
    public static final int DEFAULT_SIZE = 20;
    private static final String EMPTY_CELL_SYMBOL = ".";
    private static final String PLAYER_SYMBOL = "P";

    private final int size;
    private final Cell[][] cells;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        initializeCells();
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(Position position) {
        return cells[position.getRow()][position.getColumn()];
    }

    public void print() {
        print(null);
    }

    public void print(Player player) {
        printColumnIndexes();

        for (int row = 0; row < size; row++) {
            System.out.printf("%2d ", row);

            for (int column = 0; column < size; column++) {
                System.out.printf("%2s ", getSymbol(row, column, player));
            }

            System.out.println();
        }
    }

    private void initializeCells() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                cells[row][column] = new Cell(EMPTY_CELL_SYMBOL);
            }
        }
    }

    private void printColumnIndexes() {
        System.out.print("   ");

        for (int column = 0; column < size; column++) {
            System.out.printf("%2d ", column);
        }

        System.out.println();
    }

    private String getSymbol(int row, int column, Player player) {
        if (isPlayerAt(row, column, player)) {
            return PLAYER_SYMBOL;
        }

        return cells[row][column].getSymbol();
    }

    private boolean isPlayerAt(int row, int column, Player player) {
        if (player == null) {
            return false;
        }

        Position position = player.getCurrentPosition();
        return position.getRow() == row && position.getColumn() == column;
    }
}
