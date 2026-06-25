package game;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class Board {
    public static final int DEFAULT_SIZE = 20;
    private static final String EMPTY_CELL_SYMBOL = ".";
    private static final String PLAYER_SYMBOL = "P";
    private static final String WALL_SYMBOL = "#";

    private final int size;
    private final Cell[][] cells;
    private final Set<Position> occupiedPositions;

    public Board() {
        this(DEFAULT_SIZE);
    }

    public Board(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("O tamanho do tabuleiro deve ser positivo.");
        }

        this.size = size;
        this.cells = new Cell[size][size];
        this.occupiedPositions = new HashSet<>();
        initializeCells();
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(Position position) {
        validatePosition(position);
        return cells[position.getRow()][position.getColumn()];
    }

    public Position getInitialPlayerPosition() {
        return new Position(0, 0);
    }

    public Position getOppositeCornerPosition() {
        return new Position(size - 1, size - 1);
    }

    public boolean isPositionAvailable(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");
        return isInsideBoard(position) && !occupiedPositions.contains(position);
    }

    public void placePlayer(Player player) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        placeElement(player.getCurrentPosition(), PLAYER_SYMBOL);
    }

    public void placeDinosaur(Dinosaur dinosaur) {
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
        placeElement(dinosaur.getCurrentPosition(), dinosaur.getVisualSymbol());
    }

    public void placeWall(Position position) {
        placeElement(position, WALL_SYMBOL);
    }

    public Position getRandomFreePosition(Random random) {
        Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");

        if (!hasFreePosition()) {
            throw new IllegalStateException("Nao ha posicoes livres no tabuleiro.");
        }

        int totalCells = size * size;

        for (int attempt = 0; attempt < totalCells; attempt++) {
            Position position = new Position(random.nextInt(size), random.nextInt(size));

            if (isPositionAvailable(position)) {
                return position;
            }
        }

        return getFirstFreePosition();
    }

    public void generateRandomWalls(Random random) {
        Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
        int wallCount = size + random.nextInt(size + 1);

        for (int count = 0; count < wallCount && hasFreePosition(); count++) {
            placeWall(getRandomFreePosition(random));
        }
    }

    public void print() {
        printColumnIndexes();

        for (int row = 0; row < size; row++) {
            System.out.printf("%2d ", row);

            for (int column = 0; column < size; column++) {
                System.out.printf("%2s ", cells[row][column].getSymbol());
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

    private void placeElement(Position position, String symbol) {
        validatePosition(position);
        Objects.requireNonNull(symbol, "O simbolo e obrigatorio.");

        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("O simbolo nao pode ser vazio.");
        }

        if (occupiedPositions.contains(position)) {
            throw new IllegalArgumentException("A posicao ja esta ocupada.");
        }

        getCell(position).setSymbol(symbol);
        occupiedPositions.add(position);
    }

    private void validatePosition(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");

        if (!isInsideBoard(position)) {
            throw new IllegalArgumentException("A posicao esta fora do tabuleiro.");
        }
    }

    private boolean isInsideBoard(Position position) {
        return position.getRow() >= 0
                && position.getRow() < size
                && position.getColumn() >= 0
                && position.getColumn() < size;
    }

    private boolean hasFreePosition() {
        return occupiedPositions.size() < size * size;
    }

    private Position getFirstFreePosition() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Position position = new Position(row, column);

                if (isPositionAvailable(position)) {
                    return position;
                }
            }
        }

        throw new IllegalStateException("Nao ha posicoes livres no tabuleiro.");
    }
}
