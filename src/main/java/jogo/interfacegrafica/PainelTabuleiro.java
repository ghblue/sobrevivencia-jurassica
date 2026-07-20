package jogo.interfacegrafica;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import jogo.Game;
import jogo.interfacegrafica.recursos.CarregadorImagens;
import jogo.modelo.Board;
import jogo.modelo.Position;
import jogo.servicos.VisibilityService;

public class PainelTabuleiro extends JPanel {
    private static final int TAMANHO_IMAGEM_CELULA = 42;
    private static final Dimension TAMANHO_CELULA = new Dimension(42, 42);

    private final Game game;
    private final VisibilityService visibilityService;
    private final JButton[][] botoesCelulas;

    public PainelTabuleiro(Game game) {
        this.game = Objects.requireNonNull(game, "O jogo e obrigatorio.");
        this.visibilityService = new VisibilityService();
        int tamanho = game.getBoard().getSize();
        this.botoesCelulas = new JButton[tamanho][tamanho];
        Dimension tamanhoTabuleiro = new Dimension(
                tamanho * TAMANHO_CELULA.width,
                tamanho * TAMANHO_CELULA.height);
        setPreferredSize(tamanhoTabuleiro);
        setMinimumSize(tamanhoTabuleiro);
        setMaximumSize(tamanhoTabuleiro);
        setLayout(new GridLayout(tamanho, tamanho, 0, 0));
        criarCelulas();
        atualizarTabuleiro();
    }

    // Cria os botões que representam as células do mapa.
    private void criarCelulas() {
        for (int linha = 0; linha < botoesCelulas.length; linha++) {
            for (int coluna = 0; coluna < botoesCelulas[linha].length; coluna++) {
                JButton botao = new JButton();
                botao.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                botao.setBorderPainted(false);
                botao.setContentAreaFilled(false);
                botao.setFocusable(false);
                botao.setFocusPainted(false);
                botao.setIconTextGap(0);
                botao.setMargin(new Insets(0, 0, 0, 0));
                botao.setOpaque(false);
                botao.setPreferredSize(TAMANHO_CELULA);
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
                atualizarCelula(botoesCelulas[linha][coluna], mapa[linha][coluna]);
            }
        }
    }

    private void atualizarCelula(JButton botao, String simbolo) {
        ImageIcon imagem = obterImagem(simbolo);

        botao.setIcon(imagem);
        botao.setText(imagem == null ? simbolo : "");
        botao.setToolTipText(obterDescricao(simbolo));
    }

    private ImageIcon obterImagem(String simbolo) {
        switch (simbolo) {
            case "P":
                return CarregadorImagens.carregar("jogador.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA, simbolo);
            case "#":
                return CarregadorImagens.carregar("parede.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA, simbolo);
            case "C":
                return CarregadorImagens.carregar("compsognato.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case "T":
                return CarregadorImagens.carregar("troodonte.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case "V":
                return CarregadorImagens.carregar("velociraptor.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case "R":
                return CarregadorImagens.carregar("tiranossauro_rex.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case "X":
                return CarregadorImagens.carregar("caixa_suprimentos.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case "?":
                return CarregadorImagens.carregar("desconhecido.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            case ".":
                return CarregadorImagens.carregar("chao.png", TAMANHO_IMAGEM_CELULA, TAMANHO_IMAGEM_CELULA,
                        simbolo);
            default:
                return null;
        }
    }

    private String obterDescricao(String simbolo) {
        switch (simbolo) {
            case "P":
                return "Jogador";
            case "#":
                return "Parede";
            case "C":
                return "Compsognato";
            case "T":
                return "Troodonte";
            case "V":
                return "Velociraptor";
            case "R":
                return "Tiranossauro Rex";
            case "X":
                return "Caixa de suprimentos";
            case "?":
                return "Posicao nao visivel";
            default:
                return "Espaco vazio";
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
