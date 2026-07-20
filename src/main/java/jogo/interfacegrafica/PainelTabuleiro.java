package jogo.interfacegrafica;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JPanel;
import jogo.Game;
import jogo.modelo.Board;
import jogo.modelo.Position;
import jogo.servicos.VisibilityService;

public class PainelTabuleiro extends JPanel {
    private final Game game;
    private final VisibilityService visibilityService;
    private final JButton[][] botoesCelulas;

    public PainelTabuleiro(Game game) {
        this.game = Objects.requireNonNull(game, "O jogo e obrigatorio.");
        this.visibilityService = new VisibilityService();
        int tamanho = game.getBoard().getSize();
        this.botoesCelulas = new JButton[tamanho][tamanho];
        setLayout(new GridLayout(tamanho, tamanho));
        criarCelulas();
        atualizarTabuleiro();
    }

    // Cria os botões que representam as células do mapa.
    private void criarCelulas() {
        for (int linha = 0; linha < botoesCelulas.length; linha++) {
            for (int coluna = 0; coluna < botoesCelulas[linha].length; coluna++) {
                JButton botao = new JButton();
                botao.setFocusable(false);
                botoesCelulas[linha][coluna] = botao;
                add(botao);
            }
        }
    }

    // Atualiza cada botão conforme o estado atual do tabuleiro.
    public void atualizarTabuleiro() {
        String[][] mapa = criarMapaVisivel();

        for (int linha = 0; linha < botoesCelulas.length; linha++) {
            for (int coluna = 0; coluna < botoesCelulas[linha].length; coluna++) {
                botoesCelulas[linha][coluna].setText(mapa[linha][coluna]);
            }
        }
    }

    private String[][] criarMapaVisivel() {
        if (!game.isDebugMode()) {
            return visibilityService.createVisibleMap(game.getBoard(), game.getPlayer());
        }

        Board board = game.getBoard();
        String[][] mapaCompleto = new String[board.getSize()][board.getSize()];

        for (int linha = 0; linha < board.getSize(); linha++) {
            for (int coluna = 0; coluna < board.getSize(); coluna++) {
                mapaCompleto[linha][coluna] = board.getCell(new Position(linha, coluna)).getSymbol();
            }
        }

        return mapaCompleto;
    }
}
