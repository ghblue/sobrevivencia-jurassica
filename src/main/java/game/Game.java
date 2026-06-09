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
                    Board board = new Board();
                    board.print();
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
}
