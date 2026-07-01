package jogo.modelo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import jogo.dinossauros.Compsognathus;
import jogo.dinossauros.Dinosaur;
import jogo.dinossauros.TRex;
import jogo.dinossauros.Troodon;
import jogo.dinossauros.Velociraptor;
import jogo.enums.MoveResult;
import jogo.enums.MovementDirection;
import jogo.itens.SupplyBox;

/**
 * Mantém as células e as posições ocupadas do mapa.
 * Também centraliza as validações de posicionamento e movimentação.
 */
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

        // Todas as células pertencem ao tabuleiro e começam com o símbolo de espaço vazio.
        this.size = size;
        this.cells = new Cell[size][size];
        this.occupiedPositions = new HashSet<>();
        initializeCells();
    }

    private Board(Board source) {
        this.size = source.size;
        this.cells = new Cell[size][size];
        this.occupiedPositions = new HashSet<>(source.occupiedPositions);

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Cell sourceCell = source.cells[row][column];
                Cell copiedCell = new Cell(EMPTY_CELL_SYMBOL);
                copiedCell.setPrimarySymbol(sourceCell.getPrimarySymbol());
                copiedCell.setSecondarySymbol(sourceCell.getSecondarySymbol());
                cells[row][column] = copiedCell;
            }
        }
    }

    // Cria uma cópia independente do tabuleiro usada ao reiniciar a partida.
    Board copy() {
        return new Board(this);
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

    // Confirma que a posição está dentro do mapa e não possui outro elemento.
    public boolean isPositionAvailable(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");
        return isInsideBoard(position) && !occupiedPositions.contains(position) && !isSupplyBox(position);
    }

    // Coloca o jogador em sua posição inicial no tabuleiro.
    public void placePlayer(Player player) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        placeElement(player.getCurrentPosition(), PLAYER_SYMBOL);
    }

    // Coloca um dinossauro usando o símbolo definido por sua espécie.
    public void placeDinosaur(Dinosaur dinosaur) {
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
        placeElement(dinosaur.getCurrentPosition(), dinosaur.getVisualSymbol());
    }

    public void placeWall(Position position) {
        placeElement(position, WALL_SYMBOL);
    }

    // Registra uma caixa na camada secundária para permitir sua coleta.
    public void placeSupplyBox(SupplyBox supplyBox) {
        Objects.requireNonNull(supplyBox, "A caixa de suprimentos e obrigatoria.");
        Position position = supplyBox.getPosition();
        validatePosition(position);

        if (occupiedPositions.contains(position) || isSupplyBox(position)) {
            throw new IllegalArgumentException("A posicao ja esta ocupada.");
        }

        getCell(position).setSecondarySymbol(SupplyBox.VISUAL_SYMBOL);
    }

    // Valida o destino e informa qual evento foi encontrado pelo jogador.
    public MoveResult movePlayer(Player player, MovementDirection direction) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Objects.requireNonNull(direction, "A direcao e obrigatoria.");

        Position currentPosition = player.getCurrentPosition();
        Position nextPosition = direction.getNextPosition(currentPosition);

        // O movimento só é efetivado após verificar limites, paredes e dinossauros.
        if (!isInsideBoard(nextPosition)) {
            return MoveResult.OUT_OF_BOUNDS;
        }

        if (isWall(nextPosition)) {
            return MoveResult.WALL;
        }

        if (isDinosaur(nextPosition)) {
            return MoveResult.DINOSAUR;
        }

        if (isSupplyBox(nextPosition)) {
            moveElement(player, nextPosition, PLAYER_SYMBOL);
            return MoveResult.SUPPLY_BOX;
        }

        moveElement(player, nextPosition, PLAYER_SYMBOL);
        return MoveResult.SUCCESS;
    }

    // Ocupa com o jogador a posição liberada após uma vitória em combate.
    public void movePlayerToDefeatedDinosaurPosition(Player player, Dinosaur dinosaur) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");

        Position dinosaurPosition = dinosaur.getCurrentPosition();

        clearPosition(dinosaurPosition);
        moveElement(player, dinosaurPosition, PLAYER_SYMBOL);
    }

    // Permite destinos dentro do mapa que não tenham parede nem outro dinossauro.
    public boolean canDinosaurMoveTo(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");
        return isInsideBoard(position) && !isWall(position) && !isDinosaur(position);
    }

    public boolean isPlayerAt(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");
        return isInsideBoard(position) && PLAYER_SYMBOL.equals(getCell(position).getPrimarySymbol());
    }

    // Define paredes, dinossauros e caixas como bloqueadores da visão.
    public boolean isVisionBlocker(Position position) {
        Objects.requireNonNull(position, "A posicao e obrigatoria.");
        return isInsideBoard(position)
                && (isWall(position) || isDinosaur(position) || isSupplyBox(position));
    }

    // Atualiza de forma sincronizada a posição e o símbolo do dinossauro.
    public void moveDinosaurTo(Dinosaur dinosaur, Position newPosition) {
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
        Objects.requireNonNull(newPosition, "A nova posicao e obrigatoria.");

        if (!canDinosaurMoveTo(newPosition) || isPlayerAt(newPosition)) {
            throw new IllegalArgumentException("O dinossauro nao pode se mover para essa posicao.");
        }

        moveElement(dinosaur, newPosition, dinosaur.getVisualSymbol());
    }

    public void removeDinosaur(Dinosaur dinosaur) {
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
        clearPosition(dinosaur.getCurrentPosition());
    }

    public void removeSupplyBoxAt(Position position) {
        validatePosition(position);
        getCell(position).setSecondarySymbol(EMPTY_CELL_SYMBOL);
    }

    // Sorteia uma posição livre e usa uma busca completa como alternativa segura.
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

    // Gera uma quantidade aleatória de paredes somente em posições livres.
    public void generateRandomWalls(Random random) {
        Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
        // A quantidade varia entre o tamanho do lado e o dobro desse tamanho.
        int wallCount = size + random.nextInt(size + 1);

        for (int count = 0; count < wallCount && hasFreePosition(); count++) {
            placeWall(getRandomFreePosition(random));
        }
    }

    // Imprime todas as células do tabuleiro sem aplicar visão limitada.
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

    // Preenche a matriz com células vazias antes de posicionar os elementos.
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
        validateSymbol(symbol);

        if (occupiedPositions.contains(position)) {
            throw new IllegalArgumentException("A posicao ja esta ocupada.");
        }

        getCell(position).setSymbol(symbol);
        occupiedPositions.add(position);
    }

    // Move qualquer elemento que implemente o contrato Movable.
    private void moveElement(Movable element, Position newPosition, String symbol) {
        clearPosition(element.getCurrentPosition());
        element.moveTo(newPosition);
        replaceElement(newPosition, symbol);
    }

    private void replaceElement(Position position, String symbol) {
        validatePosition(position);
        validateSymbol(symbol);
        getCell(position).setSymbol(symbol);
        occupiedPositions.add(position);
    }

    private void clearPosition(Position position) {
        validatePosition(position);
        getCell(position).setPrimarySymbol(EMPTY_CELL_SYMBOL);
        occupiedPositions.remove(position);
    }

    private void validateSymbol(String symbol) {
        Objects.requireNonNull(symbol, "O simbolo e obrigatorio.");

        if (symbol.isEmpty()) {
            throw new IllegalArgumentException("O simbolo nao pode ser vazio.");
        }
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

    private boolean isWall(Position position) {
        return WALL_SYMBOL.equals(getCell(position).getPrimarySymbol());
    }

    private boolean isDinosaur(Position position) {
        String symbol = getCell(position).getPrimarySymbol();
        return Compsognathus.VISUAL_SYMBOL.equals(symbol)
                || Troodon.VISUAL_SYMBOL.equals(symbol)
                || Velociraptor.VISUAL_SYMBOL.equals(symbol)
                || TRex.VISUAL_SYMBOL.equals(symbol);
    }

    private boolean isSupplyBox(Position position) {
        return SupplyBox.VISUAL_SYMBOL.equals(getCell(position).getSecondarySymbol());
    }

    private boolean hasFreePosition() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (isPositionAvailable(new Position(row, column))) {
                    return true;
                }
            }
        }

        return false;
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
