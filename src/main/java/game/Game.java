package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static final int COMPSOGNATHUS_COUNT = 2;
    private static final int VELOCIRAPTOR_COUNT = 2;
    private static final int TROODON_COUNT = 5;
    private static final int MEDICAL_KIT_HEALING = 1;

    private final Scanner scanner;
    private final Random random;
    private final CombatService combatService;
    private final DinosaurMovementService dinosaurMovementService;
    private final BoardRenderer boardRenderer;
    private List<Dinosaur> dinosaurs;
    private List<SupplyBox> supplyBoxes;
    private GameStatus gameStatus;
    private boolean debugMode;

    public Game() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.combatService = new CombatService(scanner, new Dice(random));
        this.dinosaurMovementService = new DinosaurMovementService(random, combatService);
        this.boardRenderer = new BoardRenderer();
        this.dinosaurs = new ArrayList<>();
        this.supplyBoxes = new ArrayList<>();
        this.gameStatus = GameStatus.EXITED;
        this.debugMode = false;
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
                    if (!play()) {
                        running = false;
                    }
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

    private boolean play() {
        Difficulty difficulty = chooseDifficulty();
        Board board = new Board();
        Position initialPosition = board.getInitialPlayerPosition();
        Player player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        gameStatus = GameStatus.RUNNING;
        debugMode = false;
        board.placePlayer(player);
        dinosaurs = createDinosaurs(board);
        board.generateRandomWalls(random);
        supplyBoxes = createSupplyBoxes(board);

        showGameState(board, player);
        showMovementMenu(board, player);
        return gameStatus == GameStatus.EXITED;
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
        System.out.println("Saude atual: " + player.getHealth());
        System.out.println("Saude maxima: " + player.getMaxHealth());
        System.out.println("Percepcao: " + player.getPerception());
        System.out.printf("Posicao atual: linha %d, coluna %d%n", position.getRow(), position.getColumn());
        System.out.println(player.getInventoryStatus());
    }

    private void showMovementMenu(Board board, Player player) {
        while (isGameRunning()) {
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
                    showGameState(board, player);
                    break;
                case "6":
                    handleMedicalKitUse(board, player);
                    break;
                case "7":
                    toggleDebugMode(board, player);
                    break;
                case "0":
                    endGame(GameStatus.EXITED);
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private void handlePlayerMove(Board board, Player player, MovementDirection direction) {
        Position targetPosition = direction.getNextPosition(player.getCurrentPosition());
        MoveResult result = board.movePlayer(player, direction);

        switch (result) {
            case SUCCESS:
                showGameState(board, player);
                finishPlayerTurn(board, player);
                break;
            case OUT_OF_BOUNDS:
                System.out.println("Movimento invalido: voce sairia dos limites do tabuleiro.");
                break;
            case WALL:
                System.out.println("Movimento invalido: ha uma parede nessa posicao.");
                break;
            case DINOSAUR:
                handleCombat(board, player, targetPosition);
                break;
            case SUPPLY_BOX:
                collectSupplyBoxAt(player.getCurrentPosition(), player, board);
                showGameState(board, player);
                finishPlayerTurn(board, player);
                break;
            default:
                System.out.println("Movimento invalido.");
                break;
        }
    }

    private void handleCombat(Board board, Player player, Position targetPosition) {
        Dinosaur dinosaur = findDinosaurAt(targetPosition);

        if (dinosaur == null) {
            System.out.println("Voce encontrou um dinossauro, mas ele nao foi localizado na lista ativa.");
            return;
        }

        CombatResult result = combatService.startCombat(player, dinosaur);

        switch (result) {
            case PLAYER_WON:
                dinosaurs.remove(dinosaur);
                board.movePlayerToDefeatedDinosaurPosition(player, dinosaur);
                System.out.println("Vitoria no combate.");
                showGameState(board, player);
                finishPlayerTurn(board, player);
                break;
            case PLAYER_DEFEATED:
                showPlayerStatus(player);
                updateGameStatus(player);
                break;
            case FLED:
                showGameState(board, player);
                finishPlayerTurn(board, player);
                break;
            default:
                break;
        }
    }

    private void handleMedicalKitUse(Board board, Player player) {
        if (!player.hasMedicalKit()) {
            System.out.println("Voce nao possui kit medico.");
            showPlayerStatus(player);
            return;
        }

        int recoveredHealth = player.useMedicalKit(MEDICAL_KIT_HEALING);
        System.out.println("Voce usou um kit medico.");

        if (recoveredHealth > 0) {
            System.out.println("Saude recuperada: " + recoveredHealth);
        } else {
            System.out.println("Sua saude ja estava no maximo.");
        }

        showPlayerStatus(player);
        finishPlayerTurn(board, player);
    }

    private void finishPlayerTurn(Board board, Player player) {
        updateGameStatus(player);

        if (!isGameRunning()) {
            return;
        }

        System.out.println("Turno dos dinossauros.");
        dinosaurMovementService.moveDinosaurs(board, player, dinosaurs);
        showGameState(board, player);
        updateGameStatus(player);
    }

    private void toggleDebugMode(Board board, Player player) {
        debugMode = !debugMode;
        System.out.println(debugMode ? "Modo DEBUG ativado." : "Modo DEBUG desativado.");
        showGameState(board, player);
    }

    private void showGameState(Board board, Player player) {
        boardRenderer.print(board, player, debugMode);
        showPlayerStatus(player);
    }

    private void updateGameStatus(Player player) {
        if (!isGameRunning()) {
            return;
        }

        if (!player.isAlive()) {
            endGame(GameStatus.DEFEAT);
            return;
        }

        if (dinosaurs.isEmpty()) {
            endGame(GameStatus.VICTORY);
        }
    }

    private boolean isGameRunning() {
        return gameStatus == GameStatus.RUNNING;
    }

    private void endGame(GameStatus finalStatus) {
        if (!isGameRunning()) {
            return;
        }

        gameStatus = finalStatus;

        switch (finalStatus) {
            case VICTORY:
                System.out.println("Vitoria! Todos os dinossauros foram derrotados.");
                System.out.println("Partida finalizada com vitoria.");
                break;
            case DEFEAT:
                System.out.println("Derrota. Sua saude chegou a 0.");
                System.out.println("Partida finalizada com derrota.");
                break;
            case EXITED:
                System.out.println("Jogo encerrado pelo jogador.");
                System.out.println("Partida finalizada.");
                break;
            default:
                break;
        }
    }

    private Dinosaur findDinosaurAt(Position position) {
        for (Dinosaur dinosaur : dinosaurs) {
            if (dinosaur.getCurrentPosition().equals(position)) {
                return dinosaur;
            }
        }

        return null;
    }

    private void collectSupplyBoxAt(Position position, Player player, Board board) {
        for (int index = 0; index < supplyBoxes.size(); index++) {
            SupplyBox supplyBox = supplyBoxes.get(index);

            if (supplyBox.getPosition().equals(position)) {
                Item item = supplyBox.getContent();
                System.out.println("Voce encontrou uma caixa de suprimentos!");
                System.out.println("Conteudo: " + item.getName());
                System.out.println(item.applyTo(player));
                supplyBoxes.remove(index);
                board.removeSupplyBoxAt(position);
                return;
            }
        }

        System.out.println("Voce encontrou uma caixa de suprimentos, mas o conteudo nao foi localizado.");
    }

}
