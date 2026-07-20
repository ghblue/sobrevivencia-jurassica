package jogo.interfacegrafica;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jogo.Game;
import jogo.enums.Difficulty;
import jogo.enums.GameStatus;
import jogo.enums.MovementDirection;
import jogo.interfacegrafica.recursos.CarregadorImagens;
import jogo.modelo.Player;
import jogo.resultado.ResultadoAcao;
import jogo.resultado.TipoResultadoAcao;

public class JanelaPrincipal extends JFrame implements KeyListener {
    private static final String MENSAGEM_TURNO_DINOSSAUROS = "Turno dos dinossauros.";
    private static final int LARGURA_LOGO = 1920;
    private static final int ALTURA_LOGO = 1080;
    private static final int TAMANHO_ICONE_INVENTARIO = 40;

    private final InterfaceGrafica interfaceGrafica;
    private final Game game;
    private final Set<Integer> teclasPressionadas;
    private JPanel painelAtual;
    private JLabel rotuloStatus;
    private JLabel rotuloKitVisual;
    private JLabel rotuloBastaoVisual;
    private JLabel rotuloDardoVisual;
    private PainelTabuleiro painelTabuleiro;
    private JButton botaoDebug;
    private boolean processandoAcao;

    public JanelaPrincipal() {
        this.interfaceGrafica = new InterfaceGrafica(this);
        this.game = new Game(interfaceGrafica);
        this.teclasPressionadas = new HashSet<>();
        configurarJanela();
        montarMenuInicial();
        setVisible(true);
        devolverFocoParaJanela();
    }

    // Configura a janela principal compartilhada por todas as telas graficas.
    private void configurarJanela() {
        setTitle("Sobrevivência Jurássica");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setFocusable(true);
        addKeyListener(this);
    }

    // Monta o menu inicial com a imagem de fundo ja vinculada ao projeto.
    private void montarMenuInicial() {
        processandoAcao = false;
        teclasPressionadas.clear();
        rotuloStatus = null;
        rotuloKitVisual = null;
        rotuloBastaoVisual = null;
        rotuloDardoVisual = null;
        painelTabuleiro = null;
        botaoDebug = null;

        PainelComImagem painelPrincipal = new PainelComImagem(carregarImagemCapa());
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JLabel titulo = new JLabel("SOBREVIVÊNCIA JURÁSSICA", JLabel.CENTER);
        JButton botaoJogar = criarBotao("Jogar", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                selecionarDificuldade();
            }
        });
        JButton botaoSair = criarBotao("Sair", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                confirmarSaidaDoPrograma();
            }
        });

        painelPrincipal.setLayout(new BorderLayout());
        painelBotoes.setOpaque(false);
        painelBotoes.add(botaoJogar);
        painelBotoes.add(botaoSair);
        painelPrincipal.add(titulo, BorderLayout.NORTH);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        setSize(720, 540);
        setLocationRelativeTo(null);
        trocarPainel(painelPrincipal);
    }

    private ImageIcon carregarImagemCapa() {
        return CarregadorImagens.carregarOpcional("logo.png", LARGURA_LOGO, ALTURA_LOGO);
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

    // Solicita a dificuldade e inicia uma partida nova com mapa recem-gerado.
    private void selecionarDificuldade() {
        Difficulty dificuldade = escolherDificuldade();

        if (dificuldade == null) {
            devolverFocoParaJanela();
            return;
        }

        ResultadoAcao resultado = game.iniciarNovaPartida(dificuldade);
        mostrarMensagens(resultado);
        montarTelaDaPartida();
    }

    // Monta a tela jogavel com status, tabuleiro e controles de turno.
    private void montarTelaDaPartida() {
        processandoAcao = false;
        teclasPressionadas.clear();

        JPanel telaPartida = new JPanel(new BorderLayout(4, 4));
        painelTabuleiro = new PainelTabuleiro(game);
        JPanel painelCentralTabuleiro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelCentralTabuleiro.add(painelTabuleiro);

        telaPartida.add(criarPainelStatus(), BorderLayout.NORTH);
        telaPartida.add(painelCentralTabuleiro, BorderLayout.CENTER);
        telaPartida.add(criarPainelControles(), BorderLayout.SOUTH);

        setSize(1280, 1000);
        setLocationRelativeTo(null);
        trocarPainel(telaPartida);
        atualizarTela();
    }

    private JPanel criarPainelStatus() {
        JPanel painelStatus = new JPanel(new BorderLayout());
        JPanel painelInventario = new JPanel(new FlowLayout());

        rotuloStatus = new JLabel("", JLabel.CENTER);
        rotuloKitVisual = criarRotuloInventario("kit_medico.png");
        rotuloBastaoVisual = criarRotuloInventario("bastao_eletrico.png");
        rotuloDardoVisual = criarRotuloInventario("arma_dardos.png");

        painelInventario.add(rotuloKitVisual);
        painelInventario.add(rotuloBastaoVisual);
        painelInventario.add(rotuloDardoVisual);
        painelStatus.add(rotuloStatus, BorderLayout.NORTH);
        painelStatus.add(painelInventario, BorderLayout.CENTER);
        return painelStatus;
    }

    private JLabel criarRotuloInventario(String nomeImagem) {
        JLabel rotulo = new JLabel();
        rotulo.setIcon(CarregadorImagens.carregarOpcional(
                nomeImagem,
                TAMANHO_ICONE_INVENTARIO,
                TAMANHO_ICONE_INVENTARIO));
        rotulo.setHorizontalTextPosition(JLabel.RIGHT);
        return rotulo;
    }

    private JPanel criarPainelControles() {
        JPanel painelControles = new JPanel(new FlowLayout());

        painelControles.add(criarBotao("Cima", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                executarMovimento(MovementDirection.UP);
            }
        }));
        painelControles.add(criarBotao("Baixo", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                executarMovimento(MovementDirection.DOWN);
            }
        }));
        painelControles.add(criarBotao("Esquerda", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                executarMovimento(MovementDirection.LEFT);
            }
        }));
        painelControles.add(criarBotao("Direita", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                executarMovimento(MovementDirection.RIGHT);
            }
        }));
        painelControles.add(criarBotao("Usar kit médico", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                usarKitMedico();
            }
        }));

        botaoDebug = criarBotao("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                alternarDebug();
            }
        });
        painelControles.add(botaoDebug);

        painelControles.add(criarBotao("Sair", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sairDaPartida();
            }
        }));

        return painelControles;
    }

    private JButton criarBotao(String texto, ActionListener listener) {
        JButton botao = new JButton(texto);
        botao.setFocusable(false);
        botao.addActionListener(listener);
        return botao;
    }

    // Executa movimento por botao ou teclado usando a logica real da partida.
    private void executarMovimento(MovementDirection direcao) {
        if (!podeExecutarAcaoDaPartida()) {
            return;
        }

        processandoAcao = true;

        try {
            ResultadoAcao resultado = game.moverJogador(direcao);

            if (resultado.getTipo() == TipoResultadoAcao.MOVIMENTO_BLOQUEADO) {
                // Movimentos bloqueados são ignorados para evitar pop-ups excessivos na interface.
                return;
            }

            mostrarMensagens(resultado);
            atualizarTela();
            tratarFimDeJogo(resultado);
        } finally {
            finalizarAcaoGrafica();
        }
    }

    // Usa o kit medico pela regra central do jogo e atualiza a partida.
    private void usarKitMedico() {
        if (!podeExecutarAcaoDaPartida()) {
            return;
        }

        processandoAcao = true;

        try {
            ResultadoAcao resultado = game.usarKitMedico();
            mostrarMensagens(resultado);
            atualizarTela();
            tratarFimDeJogo(resultado);
        } finally {
            finalizarAcaoGrafica();
        }
    }

    // Alterna o DEBUG real sem consumir turno nem mover dinossauros.
    private void alternarDebug() {
        if (!podeExecutarAcaoDaPartida()) {
            return;
        }

        processandoAcao = true;

        try {
            ResultadoAcao resultado = game.alternarModoDebug();
            mostrarMensagens(resultado);
            atualizarTela();
        } finally {
            finalizarAcaoGrafica();
        }
    }

    // Encerra somente a partida atual e abre o fluxo de fim de jogo.
    private void sairDaPartida() {
        if (!podeExecutarAcaoDaPartida()) {
            return;
        }

        if (!interfaceGrafica.solicitarConfirmacao("Deseja encerrar a partida atual?")) {
            devolverFocoParaJanela();
            return;
        }

        processandoAcao = true;

        try {
            ResultadoAcao resultado = game.encerrarPartida();
            mostrarMensagens(resultado);
            atualizarTela();
            tratarFimDeJogo(resultado);
        } finally {
            finalizarAcaoGrafica();
        }
    }

    // Atualiza status, tabuleiro e controles depois de qualquer acao valida ou
    // tentativa.
    private void atualizarTela() {
        if (!game.temPartidaPreparada() || rotuloStatus == null) {
            return;
        }

        Player player = game.getPlayer();
        rotuloStatus.setText(String.format(
                "Saúde: %d/%d | Percepção: %d | Dificuldade: %s | Kits: %d | Bastão: %s | Dardos: %d | Inimigos: %d | DEBUG: %s",
                player.getHealth(),
                player.getMaxHealth(),
                player.getPerception(),
                obterNomeDificuldade(game.getDifficulty()),
                player.getMedicalKitCount(),
                player.hasElectricBaton() ? "Sim" : "Não",
                player.getTranquilizerAmmo(),
                game.getDinosaurs().size(),
                game.isDebugMode() ? "Ativado" : "Desativado"));

        if (botaoDebug != null) {
            botaoDebug.setText(game.isDebugMode() ? "DEBUG: Ativado" : "DEBUG: Desativado");
        }

        atualizarInventarioVisual(player);

        if (painelTabuleiro != null) {
            painelTabuleiro.atualizarTabuleiro();
        }

        repaint();
    }

    private void atualizarInventarioVisual(Player player) {
        if (rotuloKitVisual == null || rotuloBastaoVisual == null || rotuloDardoVisual == null) {
            return;
        }

        rotuloKitVisual.setText("Kits: " + player.getMedicalKitCount());
        rotuloBastaoVisual.setText("Bastao: " + (player.hasElectricBaton() ? "Sim" : "Nao"));
        rotuloDardoVisual.setText("Dardos: " + player.getTranquilizerAmmo());
    }

    // Trata vitoria, derrota ou saida e oferece novo jogo, reinicio ou
    // encerramento.
    private void tratarFimDeJogo(ResultadoAcao resultado) {
        if (resultado == null || resultado.getEstadoPartida() == GameStatus.RUNNING) {
            return;
        }

        String[] opcoes = { "Novo jogo", "Reiniciar jogo", "Sair" };
        int escolha = JOptionPane.showOptionDialog(
                this,
                obterMensagemFimDeJogo(resultado.getEstadoPartida()),
                "Fim de jogo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        switch (escolha) {
            case 1:
                reiniciarJogoAtual();
                break;
            case 2:
                sairDoPrograma();
                break;
            case 0:
            default:
                retornarAoMenuInicial();
                break;
        }
    }

    // Retorna ao menu para permitir nova dificuldade e novo mapa.
    private void retornarAoMenuInicial() {
        montarMenuInicial();
    }

    private void reiniciarJogoAtual() {
        ResultadoAcao resultado = game.reiniciarPartida();
        mostrarMensagens(resultado);

        if (resultado.getEstadoPartida() == GameStatus.RUNNING) {
            montarTelaDaPartida();
            return;
        }

        montarMenuInicial();
    }

    private void confirmarSaidaDoPrograma() {
        if (interfaceGrafica.solicitarConfirmacao("Deseja realmente sair do jogo?")) {
            sairDoPrograma();
            return;
        }

        devolverFocoParaJanela();
    }

    private void sairDoPrograma() {
        dispose();
        System.exit(0);
    }

    private void trocarPainel(JPanel painel) {
        if (painelAtual != null) {
            painelAtual.removeKeyListener(this);
        }

        painelAtual = painel;
        painelAtual.setFocusable(true);
        painelAtual.addKeyListener(this);
        getContentPane().removeAll();
        setContentPane(painel);
        revalidate();
        repaint();
        devolverFocoParaJanela();
    }

    private boolean podeExecutarAcaoDaPartida() {
        return !processandoAcao && game.isGameRunning();
    }

    private void finalizarAcaoGrafica() {
        processandoAcao = false;
        teclasPressionadas.clear();
        devolverFocoParaJanela();
    }

    private void mostrarMensagens(ResultadoAcao resultado) {
        if (resultado == null || resultado.getMensagens().isEmpty()) {
            return;
        }

        StringBuilder mensagemCompleta = new StringBuilder();

        for (String mensagem : resultado.getMensagens()) {
            if (deveExibirMensagemGrafica(mensagem)) {
                if (mensagemCompleta.length() > 0) {
                    mensagemCompleta.append(System.lineSeparator());
                }

                mensagemCompleta.append(mensagem);
            }
        }

        if (mensagemCompleta.length() > 0) {
            interfaceGrafica.mostrarMensagem(mensagemCompleta.toString());
        }
    }

    private boolean deveExibirMensagemGrafica(String mensagem) {
        return mensagem != null
                && !mensagem.isEmpty()
                && !MENSAGEM_TURNO_DINOSSAUROS.equals(mensagem);
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
        if (dificuldade == null) {
            return "-";
        }

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

    private String obterMensagemFimDeJogo(GameStatus estado) {
        switch (estado) {
            case VICTORY:
                return "Você derrotou todos os dinossauros!";
            case DEFEAT:
                return "O jogador ficou sem saúde.";
            case EXITED:
                return "Partida encerrada.";
            default:
                return "Partida finalizada.";
        }
    }

    private void devolverFocoParaJanela() {
        if (painelAtual != null && painelAtual.requestFocusInWindow()) {
            return;
        }

        if (!requestFocusInWindow()) {
            requestFocus();
        }
    }

    private MovementDirection obterDirecaoPelaTecla(int codigoTecla) {
        switch (codigoTecla) {
            case KeyEvent.VK_W:
                return MovementDirection.UP;
            case KeyEvent.VK_S:
                return MovementDirection.DOWN;
            case KeyEvent.VK_A:
                return MovementDirection.LEFT;
            case KeyEvent.VK_D:
                return MovementDirection.RIGHT;
            default:
                return null;
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        MovementDirection direcao = obterDirecaoPelaTecla(event.getKeyCode());

        if (direcao == null
                || !podeExecutarAcaoDaPartida()
                || teclasPressionadas.contains(event.getKeyCode())) {
            return;
        }

        teclasPressionadas.add(event.getKeyCode());
        executarMovimento(direcao);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        teclasPressionadas.remove(event.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent event) {
        // O jogo usa keyPressed para evitar movimentos duplicados por caractere
        // digitado.
    }
}
