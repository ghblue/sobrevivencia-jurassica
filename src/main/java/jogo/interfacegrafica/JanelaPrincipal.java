package jogo.interfacegrafica;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jogo.Game;
import jogo.enums.Difficulty;
import jogo.interfaceusuario.InterfaceUsuario;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class JanelaPrincipal extends JFrame implements InterfaceUsuario {
    private final Game game;

    public JanelaPrincipal() {
        this.game = new Game(this);
        configurarJanela();
        montarMenuInicial();
        setVisible(true);
    }

    // Configura a janela principal da versao grafica.
    private void configurarJanela() {
        setTitle("Sobrevivência Jurássica");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Cria os componentes visuais do menu inicial.
    private void montarMenuInicial() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        JPanel painelCentral = new JPanel(new GridLayout(3, 1, 10, 10));
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JLabel titulo = new JLabel("SOBREVIVÊNCIA JURÁSSICA", JLabel.CENTER);
        JButton botaoJogar = new JButton("Jogar");
        JButton botaoSair = new JButton("Sair");

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

        painelCentral.add(new JLabel(""));
        painelCentral.add(titulo);
        painelCentral.add(painelBotoes);

        painelPrincipal.add(painelCentral, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    // Solicita a dificuldade antes de inicializar uma nova partida.
    private void selecionarDificuldade() {
        Difficulty dificuldade = escolherDificuldade();

        if (dificuldade == null) {
            return;
        }

        game.iniciarNovaPartida(dificuldade);
        JOptionPane.showMessageDialog(
                this,
                "Partida iniciada no modo " + obterNomeDificuldade(dificuldade) + ".\n\n"
                        + "O tabuleiro gráfico será exibido na próxima etapa.",
                "Partida iniciada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private Difficulty escolherDificuldade() {
        String[] opcoes = {"Fácil", "Médio", "Difícil"};
        int escolha = JOptionPane.showOptionDialog(
                this,
                "Escolha a dificuldade:",
                "Dificuldade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

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
                JOptionPane.YES_NO_OPTION
        );

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
                opcoes[0]
        );

        return escolha >= 0 ? escolha + 1 : 0;
    }

    @Override
    public boolean solicitarConfirmacao(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                mensagem,
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );
        return resposta == JOptionPane.YES_OPTION;
    }
}
