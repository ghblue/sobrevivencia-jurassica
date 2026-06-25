package game;

import java.util.Scanner;

public class Game {
    private final Scanner scanner;

    public Game() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        showWelcomeMessage();
        showMainMenu();
    }

    private void showWelcomeMessage() {
        System.out.println("Bem-vindo ao Sobrevivencia Jurassica!");
    }

    private void showMainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("Menu principal");
            System.out.println("1 - Jogar");
            System.out.println("2 - Sair");
            System.out.print("Escolha uma opcao: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    play();
                    break;
                case "2":
                    System.out.println("Saindo do jogo.");
                    running = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void play() {
        Difficulty difficulty = chooseDifficulty();
        Position initialPosition = new Position(0, 0);
        Player player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        Board board = new Board();
        board.print(player);
        showPlayerStatus(player);
    }

    private Difficulty chooseDifficulty() {
        while (true) {
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

    private void showPlayerStatus(Player player) {
        Position position = player.getCurrentPosition();

        System.out.println("Status do jogador");
        System.out.println("Saude: " + player.getHealth());
        System.out.println("Percepcao: " + player.getPerception());
        System.out.printf("Posicao atual: linha %d, coluna %d%n", position.getRow(), position.getColumn());
    }
}
