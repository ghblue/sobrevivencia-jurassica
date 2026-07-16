package jogo.interfaceusuario.console;

import java.util.Objects;
import java.util.Scanner;
import jogo.Game;
import jogo.enums.Difficulty;
import jogo.enums.MovementDirection;
import jogo.interfaceusuario.InterfaceUsuario;
import jogo.modelo.Player;
import jogo.modelo.Position;
import jogo.resultado.ResultadoAcao;
import jogo.servicos.BoardRenderer;

public class InterfaceConsole implements InterfaceUsuario {
    private final Scanner scanner;
    private final BoardRenderer boardRenderer;

    public InterfaceConsole() {
        this(new Scanner(System.in));
    }

    public InterfaceConsole(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "O leitor de entrada e obrigatorio.");
        this.boardRenderer = new BoardRenderer();
    }

    // Mantem o fluxo de terminal separado das regras para permitir outra interface no futuro.
    public void iniciar(Game game) {
        Objects.requireNonNull(game, "O jogo e obrigatorio.");
        mostrarMensagem("Bem-vindo ao Sobrevivencia Jurassica!");

        if (deveIniciarJogo()) {
            apresentarResultado(game, game.iniciarNovaPartida(escolherDificuldade()));
            executarAplicacao(game);
        }

        mostrarMensagem("Saindo do jogo.");
    }

    @Override
    public void mostrarMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    @Override
    public String solicitarEntrada(String mensagem) {
        if (mensagem != null && !mensagem.isEmpty()) {
            System.out.print(mensagem);
        }

        return scanner.nextLine();
    }

    @Override
    public int solicitarOpcao(String titulo, String[] opcoes) {
        Objects.requireNonNull(opcoes, "As opcoes sao obrigatorias.");

        while (true) {
            if (titulo != null && !titulo.isEmpty()) {
                mostrarMensagem(titulo);
            }

            for (int index = 0; index < opcoes.length; index++) {
                mostrarMensagem((index + 1) + " - " + opcoes[index]);
            }

            String entrada = solicitarEntrada("Escolha uma opcao: ");

            try {
                int opcao = Integer.parseInt(entrada);

                if (opcao >= 1 && opcao <= opcoes.length) {
                    return opcao;
                }
            } catch (NumberFormatException exception) {
                // A mensagem abaixo preserva a validacao simples da versao console.
            }

            mostrarMensagem("Opcao invalida.");
        }
    }

    @Override
    public boolean solicitarConfirmacao(String mensagem) {
        while (true) {
            mostrarMensagem(mensagem);
            mostrarMensagem("1 - Sim");
            mostrarMensagem("2 - Nao");
            String opcao = solicitarEntrada("Escolha uma opcao: ");

            switch (opcao) {
                case "1":
                    return true;
                case "2":
                    return false;
                default:
                    mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private void executarAplicacao(Game game) {
        boolean aplicacaoEmExecucao = true;

        while (aplicacaoEmExecucao) {
            executarMenuDaPartida(game);

            switch (lerOpcaoMenuFimDeJogo()) {
                case "1":
                    apresentarResultado(game, game.iniciarNovaPartida(escolherDificuldade()));
                    break;
                case "2":
                    apresentarResultado(game, game.reiniciarPartida());
                    break;
                case "0":
                    aplicacaoEmExecucao = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void executarMenuDaPartida(Game game) {
        while (game.isGameRunning()) {
            String opcao = lerOpcaoMenuJogo();

            switch (opcao) {
                case "1":
                    apresentarResultado(game, game.moverJogador(MovementDirection.UP));
                    break;
                case "2":
                    apresentarResultado(game, game.moverJogador(MovementDirection.DOWN));
                    break;
                case "3":
                    apresentarResultado(game, game.moverJogador(MovementDirection.LEFT));
                    break;
                case "4":
                    apresentarResultado(game, game.moverJogador(MovementDirection.RIGHT));
                    break;
                case "5":
                    mostrarEstadoDoJogo(game);
                    break;
                case "6":
                    apresentarResultado(game, game.usarKitMedico());
                    break;
                case "7":
                    apresentarResultado(game, game.alternarModoDebug());
                    break;
                case "0":
                    apresentarResultado(game, game.encerrarPartida());
                    break;
                default:
                    mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private boolean deveIniciarJogo() {
        while (true) {
            mostrarMensagem("Menu principal");
            mostrarMensagem("1 - Jogar");
            mostrarMensagem("2 - Sair");
            String opcao = solicitarEntrada("Escolha uma opcao: ");

            switch (opcao) {
                case "1":
                    return true;
                case "2":
                    return false;
                default:
                    mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private Difficulty escolherDificuldade() {
        while (true) {
            // A dificuldade altera somente a percepcao inicial usada nas esquivas.
            mostrarMensagem("Escolha a dificuldade");
            mostrarMensagem(String.format("1 - EASY (percepcao %d)", Difficulty.EASY.getPerception()));
            mostrarMensagem(String.format("2 - MEDIUM (percepcao %d)", Difficulty.MEDIUM.getPerception()));
            mostrarMensagem(String.format("3 - HARD (percepcao %d)", Difficulty.HARD.getPerception()));
            String opcao = solicitarEntrada("Escolha uma opcao: ");

            switch (opcao) {
                case "1":
                    return Difficulty.EASY;
                case "2":
                    return Difficulty.MEDIUM;
                case "3":
                    return Difficulty.HARD;
                default:
                    mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private String lerOpcaoMenuJogo() {
        mostrarMensagem("=== Menu ===");
        mostrarMensagem("1 - Mover para cima");
        mostrarMensagem("2 - Mover para baixo");
        mostrarMensagem("3 - Mover para esquerda");
        mostrarMensagem("4 - Mover para direita");
        mostrarMensagem("5 - Exibir mapa novamente");
        mostrarMensagem("6 - Usar kit medico");
        mostrarMensagem("7 - Alternar modo DEBUG");
        mostrarMensagem("0 - Encerrar jogo");
        return solicitarEntrada("Escolha uma opcao: ");
    }

    private String lerOpcaoMenuFimDeJogo() {
        while (true) {
            mostrarMensagem("=== Fim de jogo ===");
            mostrarMensagem("");
            mostrarMensagem("1 - Novo Jogo");
            mostrarMensagem("2 - Reiniciar Jogo");
            mostrarMensagem("0 - Sair");
            String opcao = solicitarEntrada("Escolha uma opcao: ");

            if ("1".equals(opcao) || "2".equals(opcao) || "0".equals(opcao)) {
                return opcao;
            }

            mostrarMensagem("Opcao invalida.");
        }
    }

    private void apresentarResultado(Game game, ResultadoAcao resultado) {
        for (String mensagem : resultado.getMensagens()) {
            mostrarMensagem(mensagem);
        }

        if (resultado.deveExibirEstado()) {
            mostrarEstadoDoJogo(game);
            return;
        }

        if (resultado.deveExibirStatusJogador() && game.getPlayer() != null) {
            mostrarStatusJogador(game.getPlayer());
        }
    }

    private void mostrarEstadoDoJogo(Game game) {
        if (!game.temPartidaPreparada()) {
            return;
        }

        mostrarMensagem(boardRenderer.render(game.getBoard(), game.getPlayer(), game.isDebugMode()));
        mostrarStatusJogador(game.getPlayer());
    }

    private void mostrarStatusJogador(Player player) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Position position = player.getCurrentPosition();

        mostrarMensagem("Status do jogador");
        mostrarMensagem("Saude atual: " + player.getHealth());
        mostrarMensagem("Saude maxima: " + player.getMaxHealth());
        mostrarMensagem("Percepcao: " + player.getPerception());
        mostrarMensagem(String.format("Posicao atual: linha %d, coluna %d", position.getRow(), position.getColumn()));
        mostrarMensagem(player.getInventoryStatus());
    }
}
