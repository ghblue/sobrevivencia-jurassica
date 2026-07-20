package jogo.interfacegrafica;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import jogo.Game;
import jogo.enums.Difficulty;
import jogo.interfaceusuario.InterfaceUsuario;
import jogo.modelo.Player;
import jogo.modelo.Position;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JanelaPrincipal extends JFrame implements InterfaceUsuario {
    private static final String CAMINHO_CAPA_RECURSO = "/jogo/util/images/capa-do-jogo.png";
    private static final String CAMINHO_CAPA_ARQUIVO = "src/main/java/jogo/util/images/capa-do-jogo.png";

    private final Game game;
    private JLabel informacoesPartida;
    private PainelTabuleiro painelTabuleiro;

    public JanelaPrincipal() {
        this.game = new Game(this);
        configurarJanela();
        montarMenuInicial();
        setVisible(true);
    }

    // Configura a janela principal da versao grafica.
    private void configurarJanela() {
        setTitle("Sobrevivência Jurássica");
        setSize(720, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Cria os componentes visuais do menu inicial.
    private void montarMenuInicial() {
        PainelComImagem painelPrincipal = new PainelComImagem(carregarImagemCapa());
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JLabel titulo = new JLabel("SOBREVIVÊNCIA JURÁSSICA", JLabel.CENTER);
        JButton botaoJogar = new JButton("Jogar");
        JButton botaoSair = new JButton("Sair");

        painelPrincipal.setLayout(new BorderLayout());
        painelBotoes.setOpaque(false);

        botaoJogar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                selecionarDificuldade();
            }
        });

        botaoSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                confirmarSaida();
            }
        });

        painelBotoes.add(botaoJogar);
        painelBotoes.add(botaoSair);

        painelPrincipal.add(titulo, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        add(painelPrincipal);
    }

    private ImageIcon carregarImagemCapa() {
        URL recurso = getClass().getResource(CAMINHO_CAPA_RECURSO);

        if (recurso != null) {
            return new ImageIcon(recurso);
        }

        File arquivo = new File(CAMINHO_CAPA_ARQUIVO);

        if (arquivo.exists()) {
            return new ImageIcon(arquivo.getPath());
        }

        return null;
    }

    private static class PainelComImagem extends JPanel {
        private final Image imagem;

        private PainelComImagem(ImageIcon imagemCapa) {
            this.imagem = imagemCapa != null ? imagemCapa.getImage() : null;
        }

        // Desenha a imagem como fundo do menu inicial.
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            if (imagem != null) {
                graphics.drawImage(imagem, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    // Solicita a dificuldade antes de inicializar uma nova partida.
    private void selecionarDificuldade() {
        Difficulty dificuldade = escolherDificuldade();

        if (dificuldade == null) {
            return;
        }

        game.iniciarNovaPartida(dificuldade);
        mostrarTelaDoJogo();
    }

    // Substitui o menu inicial pela tela da partida.
    private void mostrarTelaDoJogo() {
        JPanel telaJogo = new JPanel(new BorderLayout());
        informacoesPartida = new JLabel("", JLabel.CENTER);
        painelTabuleiro = new PainelTabuleiro(game);

        atualizarInformacoesPartida();
        telaJogo.add(informacoesPartida, BorderLayout.NORTH);
        telaJogo.add(painelTabuleiro, BorderLayout.CENTER);

        setContentPane(telaJogo);
        setSize(760, 800);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private void atualizarInformacoesPartida() {
        Player player = game.getPlayer();
        Position position = player.getCurrentPosition();

        informacoesPartida.setText(String.format(
                "Saúde: %d/%d | Percepção: %d | Dificuldade: %s | Posição: (%d, %d)",
                player.getHealth(),
                player.getMaxHealth(),
                player.getPerception(),
                obterNomeDificuldade(game.getDifficulty()),
                position.getRow(),
                position.getColumn()));
    }

    private Difficulty escolherDificuldade() {
        String[] opcoes = { "Fácil", "Médio", "Difícil" };
        int escolha = JOptionPane.showOptionDialog(
                this,
                "Escolha a dificuldade:",
                "Dificuldade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        switch (escolha) {
            case 0:
                return Difficulty.EASY;
            case 1:
                return Difficulty.MEDIUM;
            case 2:
                return Difficulty.HARD;
            default:
                return null;
        }
    }

    private String obterNomeDificuldade(Difficulty dificuldade) {
        switch (dificuldade) {
            case EASY:
                return "Fácil";
            case MEDIUM:
                return "Médio";
            case HARD:
                return "Difícil";
            default:
                return dificuldade.name();
        }
    }

    // Confirma a escolha do usuario antes de encerrar o programa.
    private void confirmarSaida() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente sair do jogo?",
                "Sair",
                JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }

    @Override
    public String solicitarEntrada(String mensagem) {
        return JOptionPane.showInputDialog(this, mensagem);
    }

    @Override
    public int solicitarOpcao(String titulo, String[] opcoes) {
        int escolha = JOptionPane.showOptionDialog(
                this,
                titulo,
                titulo,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        return escolha >= 0 ? escolha + 1 : 0;
    }

    @Override
    public boolean solicitarConfirmacao(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                mensagem,
                "Confirmação",
                JOptionPane.YES_NO_OPTION);
        return resposta == JOptionPane.YES_OPTION;
    }
}
