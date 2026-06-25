package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static final int COMPSOGNATHUS_COUNT = 2;
    private static final int VELOCIRAPTOR_COUNT = 2;
    private static final int TROODON_COUNT = 5;

    private final Scanner scanner;
    private final Random random;
    private List<Dinosaur> dinosaurs;

    public Game() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.dinosaurs = new ArrayList<>();
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
        Board board = new Board();
        Position initialPosition = board.getInitialPlayerPosition();
        Player player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        board.placePlayer(player);
        dinosaurs = createDinosaurs(board);
        board.generateRandomWalls(random);

        board.print();
        showPlayerStatus(player);
        showDinosaurStatus();
    }

    private List<Dinosaur> createDinosaurs(Board board) {
        List<Dinosaur> createdDinosaurs = new ArrayList<>();

        addDinosaur(createdDinosaurs, new TRex(board.getOppositeCornerPosition()), board);

        for (int count = 0; count < COMPSOGNATHUS_COUNT; count++) {
            addDinosaur(createdDinosaurs, new Compsognathus(board.getRandomFreePosition(random)), board);
        }

        for (int count = 0; count < VELOCIRAPTOR_COUNT; count++) {
            addDinosaur(createdDinosaurs, new Velociraptor(board.getRandomFreePosition(random)), board);
        }

        for (int count = 0; count < TROODON_COUNT; count++) {
            addDinosaur(createdDinosaurs, new Troodon(board.getRandomFreePosition(random)), board);
        }

        return createdDinosaurs;
    }

    private void addDinosaur(List<Dinosaur> createdDinosaurs, Dinosaur dinosaur, Board board) {
        board.placeDinosaur(dinosaur);
        createdDinosaurs.add(dinosaur);
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

    private void showDinosaurStatus() {
        System.out.println("Dinossauros posicionados");

        for (Dinosaur dinosaur : dinosaurs) {
            Position position = dinosaur.getCurrentPosition();

            System.out.printf(
                    "%s | simbolo: %s | saude: %d | posicao: linha %d, coluna %d | descricao: %s%n",
                    dinosaur.getName(),
                    dinosaur.getVisualSymbol(),
                    dinosaur.getHealth(),
                    position.getRow(),
                    position.getColumn(),
                    dinosaur.getDescription()
            );
        }
    }
}
