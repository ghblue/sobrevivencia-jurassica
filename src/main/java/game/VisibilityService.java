package game;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VisibilityService {
    public static final String HIDDEN_POSITION_SYMBOL = "?";
    private static final String PLAYER_SYMBOL = "P";

    public Set<Position> calculateVisiblePositions(Board board, Position playerPosition) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        Objects.requireNonNull(playerPosition, "A posicao do jogador e obrigatoria.");
        validatePlayerPosition(board, playerPosition);

        Set<Position> visiblePositions = new HashSet<>();
        visiblePositions.add(playerPosition);

        for (MovementDirection direction : MovementDirection.values()) {
            addVisiblePositionsInDirection(board, playerPosition, direction, visiblePositions);
        }

        return visiblePositions;
    }

    public String[][] createVisibleMap(Board board, Player player) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        Objects.requireNonNull(player, "O jogador e obrigatorio.");

        Position playerPosition = player.getCurrentPosition();
        Set<Position> visiblePositions = calculateVisiblePositions(board, playerPosition);
        String[][] visibleMap = new String[board.getSize()][board.getSize()];

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                Position position = new Position(row, column);
                visibleMap[row][column] = visiblePositions.contains(position)
                        ? board.getCell(position).getSymbol()
                        : HIDDEN_POSITION_SYMBOL;
            }
        }

        visibleMap[playerPosition.getRow()][playerPosition.getColumn()] = PLAYER_SYMBOL;
        return visibleMap;
    }

    private void addVisiblePositionsInDirection(
            Board board,
            Position playerPosition,
            MovementDirection direction,
            Set<Position> visiblePositions
    ) {
        Position currentPosition = direction.getNextPosition(playerPosition);

        while (isInsideBoard(board, currentPosition)) {
            visiblePositions.add(currentPosition);

            // O primeiro obstaculo aparece no mapa, mas bloqueia as casas seguintes.
            if (board.isVisionBlocker(currentPosition)) {
                return;
            }

            currentPosition = direction.getNextPosition(currentPosition);
        }
    }

    private boolean isInsideBoard(Board board, Position position) {
        return position.getRow() >= 0
                && position.getRow() < board.getSize()
                && position.getColumn() >= 0
                && position.getColumn() < board.getSize();
    }

    private void validatePlayerPosition(Board board, Position playerPosition) {
        if (!isInsideBoard(board, playerPosition)) {
            throw new IllegalArgumentException("A posicao do jogador esta fora do tabuleiro.");
        }
    }
}
