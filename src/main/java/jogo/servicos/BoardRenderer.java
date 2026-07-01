package jogo.servicos;

import java.util.Objects;
import jogo.modelo.Board;
import jogo.modelo.Player;
import jogo.modelo.Position;

/**
 * Renderiza o tabuleiro no console, respeitando a visão limitada ou o modo DEBUG.
 */
public class BoardRenderer {
    private final VisibilityService visibilityService;

    public BoardRenderer() {
        this(new VisibilityService());
    }

    public BoardRenderer(VisibilityService visibilityService) {
        this.visibilityService = Objects.requireNonNull(
                visibilityService,
                "O servico de visibilidade e obrigatorio."
        );
    }

    // Escolhe entre a renderização completa e a limitada pela visibilidade.
    public void print(Board board, Player player, boolean debugMode) {
        // No modo DEBUG, todos os elementos são exibidos para facilitar a inspeção da partida.
        if (debugMode) {
            printFullMap(board);
            return;
        }

        printLimitedMap(board, player);
    }

    // Monta e imprime o mapa completo usado pelo modo DEBUG.
    public void printFullMap(Board board) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        String[][] map = new String[board.getSize()][board.getSize()];

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                map[row][column] = board.getCell(new Position(row, column)).getSymbol();
            }
        }

        printMap(map);
    }

    // Solicita ao serviço de visibilidade o mapa que o jogador pode enxergar.
    public void printLimitedMap(Board board, Player player) {
        printMap(visibilityService.createVisibleMap(board, player));
    }

    // Imprime a matriz de símbolos com índices de linha e coluna.
    private void printMap(String[][] map) {
        printColumnIndexes(map.length);

        for (int row = 0; row < map.length; row++) {
            System.out.printf("%2d ", row);

            for (int column = 0; column < map[row].length; column++) {
                System.out.printf("%2s ", map[row][column]);
            }

            System.out.println();
        }
    }

    private void printColumnIndexes(int size) {
        System.out.print("   ");

        for (int column = 0; column < size; column++) {
            System.out.printf("%2d ", column);
        }

        System.out.println();
    }
}
