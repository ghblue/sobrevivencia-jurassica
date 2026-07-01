package jogo;

import java.util.Objects;
import java.util.Scanner;
import jogo.enums.Difficulty;
import jogo.modelo.Player;
import jogo.modelo.Position;

/**
 * Centraliza menus, leituras e informações apresentadas no console.
 */
public class ConsoleUI {
    private final Scanner scanner;

    public ConsoleUI(Scanner scanner) {
        this.scanner = Objects.requireNonNull(scanner, "O leitor de entrada e obrigatorio.");
    }

    public void showWelcomeMessage() {
        System.out.println("Bem-vindo ao Sobrevivencia Jurassica!");
    }

    // Exibe o menu principal até o usuário escolher jogar ou sair.
    public boolean shouldStartGame() {
        while (true) {
            System.out.println("Menu principal");
            System.out.println("1 - Jogar");
            System.out.println("2 - Sair");
            System.out.print("Escolha uma opcao: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    return true;
                case "2":
                    return false;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    // Lê a dificuldade que definirá a percepção inicial do jogador.
    public Difficulty chooseDifficulty() {
        while (true) {
            // A dificuldade altera somente a percepção inicial usada nas esquivas.
            System.out.println("Escolha a dificuldade");
            System.out.printf("1 - EASY (percepcao %d)%n", Difficulty.EASY.getPerception());
            System.out.printf("2 - MEDIUM (percepcao %d)%n", Difficulty.MEDIUM.getPerception());
            System.out.printf("3 - HARD (percepcao %d)%n", Difficulty.HARD.getPerception());
            System.out.print("Escolha uma opcao: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    return Difficulty.EASY;
                case "2":
                    return Difficulty.MEDIUM;
                case "3":
                    return Difficulty.HARD;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    // Apresenta as ações disponíveis durante um turno da partida.
    public String readGameMenuOption() {
        System.out.println("=== Menu ===");
        System.out.println("1 - Mover para cima");
        System.out.println("2 - Mover para baixo");
        System.out.println("3 - Mover para esquerda");
        System.out.println("4 - Mover para direita");
        System.out.println("5 - Exibir mapa novamente");
        System.out.println("6 - Usar kit medico");
        System.out.println("7 - Alternar modo DEBUG");
        System.out.println("0 - Encerrar jogo");
        System.out.print("Escolha uma opcao: ");
        return scanner.nextLine();
    }

    // Solicita novo jogo, reinício ou saída depois que uma partida termina.
    public String readEndGameMenuOption() {
        while (true) {
            System.out.println("=== Fim de jogo ===");
            System.out.println();
            System.out.println("1 - Novo Jogo");
            System.out.println("2 - Reiniciar Jogo");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opcao: ");

            String option = scanner.nextLine();

            if ("1".equals(option) || "2".equals(option) || "0".equals(option)) {
                return option;
            }

            System.out.println("Opcao invalida.");
        }
    }

    // Mostra saúde, percepção, posição e inventário atuais do jogador.
    public void showPlayerStatus(Player player) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Position position = player.getCurrentPosition();

        System.out.println("Status do jogador");
        System.out.println("Saude atual: " + player.getHealth());
        System.out.println("Saude maxima: " + player.getMaxHealth());
        System.out.println("Percepcao: " + player.getPerception());
        System.out.printf("Posicao atual: linha %d, coluna %d%n", position.getRow(), position.getColumn());
        System.out.println(player.getInventoryStatus());
    }
}
