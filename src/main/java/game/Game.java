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
    private List<SupplyBox> supplyBoxes;

    public Game() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.dinosaurs = new ArrayList<>();
        this.supplyBoxes = new ArrayList<>();
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
        supplyBoxes = createSupplyBoxes(board);

        board.print();
        showPlayerStatus(player);
        showDinosaurStatus();
        showMovementMenu(board, player);
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

    private List<SupplyBox> createSupplyBoxes(Board board) {
        List<SupplyBox> createdSupplyBoxes = new ArrayList<>();

        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new MedicalKit()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new ElectricBaton()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new TranquilizerGun()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new SurpriseCompsognathus()), board);

        return createdSupplyBoxes;
    }

    private void addDinosaur(List<Dinosaur> createdDinosaurs, Dinosaur dinosaur, Board board) {
        board.placeDinosaur(dinosaur);
        createdDinosaurs.add(dinosaur);
    }

    private void addSupplyBox(List<SupplyBox> createdSupplyBoxes, SupplyBox supplyBox, Board board) {
        board.placeSupplyBox(supplyBox);
        createdSupplyBoxes.add(supplyBox);
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
        System.out.println(player.getInventoryStatus());
    }

    private void showMovementMenu(Board board, Player player) {
        boolean playing = true;

        while (playing) {
            System.out.println("=== Menu ===");
            System.out.println("1 - Mover para cima");
            System.out.println("2 - Mover para baixo");
            System.out.println("3 - Mover para esquerda");
            System.out.println("4 - Mover para direita");
            System.out.println("5 - Exibir mapa novamente");
            System.out.println("0 - Encerrar jogo");
            System.out.print("Escolha uma opcao: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    handlePlayerMove(board, player, MovementDirection.UP);
                    break;
                case "2":
                    handlePlayerMove(board, player, MovementDirection.DOWN);
                    break;
                case "3":
                    handlePlayerMove(board, player, MovementDirection.LEFT);
                    break;
                case "4":
                    handlePlayerMove(board, player, MovementDirection.RIGHT);
                    break;
                case "5":
                    board.print();
                    showPlayerStatus(player);
                    break;
                case "0":
                    System.out.println("Jogo encerrado.");
                    playing = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void handlePlayerMove(Board board, Player player, MovementDirection direction) {
        MoveResult result = board.movePlayer(player, direction);

        switch (result) {
            case SUCCESS:
                board.print();
                showPlayerStatus(player);
                break;
            case OUT_OF_BOUNDS:
                System.out.println("Movimento invalido: voce sairia dos limites do tabuleiro.");
                break;
            case WALL:
                System.out.println("Movimento invalido: ha uma parede nessa posicao.");
                break;
            case DINOSAUR:
                System.out.println("Voce encontrou um dinossauro!");
                System.out.println("(Combate sera implementado na proxima etapa.)");
                break;
            case SUPPLY_BOX:
                collectSupplyBoxAt(player.getCurrentPosition(), player);
                board.print();
                showPlayerStatus(player);
                break;
            default:
                System.out.println("Movimento invalido.");
                break;
        }
    }

    private void collectSupplyBoxAt(Position position, Player player) {
        for (int index = 0; index < supplyBoxes.size(); index++) {
            SupplyBox supplyBox = supplyBoxes.get(index);

            if (supplyBox.getPosition().equals(position)) {
                Item item = supplyBox.getContent();
                System.out.println("Voce encontrou uma caixa de suprimentos!");
                System.out.println("Conteudo: " + item.getName());
                System.out.println(item.applyTo(player));
                supplyBoxes.remove(index);
                return;
            }
        }

        System.out.println("Voce encontrou uma caixa de suprimentos, mas o conteudo nao foi localizado.");
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
