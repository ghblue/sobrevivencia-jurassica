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

    // Escolhe entre a renderizacao completa e a limitada pela visibilidade.
    public String render(Board board, Player player, boolean debugMode) {
        // No modo DEBUG, todos os elementos são exibidos para facilitar a inspeção da partida.
        if (debugMode) {
            return renderFullMap(board);
        }

        return renderLimitedMap(board, player);
    }

    // Monta o mapa completo usado pelo modo DEBUG.
    public String renderFullMap(Board board) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        String[][] map = new String[board.getSize()][board.getSize()];

        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                map[row][column] = board.getCell(new Position(row, column)).getSymbol();
            }
        }

        return renderMap(map);
    }

    // Solicita ao serviço de visibilidade o mapa que o jogador pode enxergar.
    public String renderLimitedMap(Board board, Player player) {
        return renderMap(visibilityService.createVisibleMap(board, player));
    }

    // Formata a matriz de simbolos com indices de linha e coluna para a interface console.
    private String renderMap(String[][] map) {
        StringBuilder builder = new StringBuilder();
        appendColumnIndexes(builder, map.length);

        for (int row = 0; row < map.length; row++) {
            builder.append(System.lineSeparator());
            builder.append(String.format("%2d ", row));

            for (int column = 0; column < map[row].length; column++) {
                builder.append(String.format("%2s ", map[row][column]));
            }
        }

        return builder.toString();
    }

    private void appendColumnIndexes(StringBuilder builder, int size) {
        builder.append("   ");

        for (int column = 0; column < size; column++) {
            builder.append(String.format("%2d ", column));
        }
    }
}
