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

    private final ConsoleUI consoleUI;
    private final Random random;
    private final CombatService combatService;
    private final DinosaurMovementService dinosaurMovementService;
    private final BoardRenderer boardRenderer;
    private Board board;
    private Player player;
    private Difficulty difficulty;
    private List<Dinosaur> dinosaurs;
    private List<SupplyBox> supplyBoxes;
    private InitialGameState initialGameState;
    private GameStatus gameStatus;
    private boolean debugMode;

    public Game() {
        Scanner scanner = new Scanner(System.in);
        this.consoleUI = new ConsoleUI(scanner);
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
        consoleUI.showWelcomeMessage();

        if (consoleUI.shouldStartGame()) {
            startNewGame();
            runApplicationLoop();
        }

        System.out.println("Saindo do jogo.");
    }

    private void runApplicationLoop() {
        boolean applicationRunning = true;

        while (applicationRunning) {
            runCurrentGame();

            switch (consoleUI.readEndGameMenuOption()) {
                case "1":
                    startNewGame();
                    break;
                case "2":
                    restartCurrentGame();
                    break;
                case "0":
                    applicationRunning = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void startNewGame() {
        difficulty = consoleUI.chooseDifficulty();
        initializeBoard();
        initialGameState = new InitialGameState(difficulty, board, player, dinosaurs, supplyBoxes);
        prepareGameToRun();
    }

    private void initializeBoard() {
        board = new Board();
        Position initialPosition = board.getInitialPlayerPosition();
        player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        board.placePlayer(player);
        dinosaurs = createDinosaurs(board);
        board.generateRandomWalls(random);
        supplyBoxes = createSupplyBoxes(board);
    }

    private void restartCurrentGame() {
        // O reinicio restaura copias do estado original, sem realizar novos sorteios.
        difficulty = initialGameState.getDifficulty();
        board = initialGameState.restoreBoard();
        player = initialGameState.restorePlayer();
        dinosaurs = initialGameState.restoreDinosaurs();
        supplyBoxes = initialGameState.restoreSupplyBoxes();
        prepareGameToRun();
    }

    private void prepareGameToRun() {
        gameStatus = GameStatus.RUNNING;
        debugMode = false;
    }

    private void runCurrentGame() {
        showGameState();
        runGameMenu();
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

    private void runGameMenu() {
        while (isGameRunning()) {
            String option = consoleUI.readGameMenuOption();

            switch (option) {
                case "1":
                    handlePlayerMove(MovementDirection.UP);
                    break;
                case "2":
                    handlePlayerMove(MovementDirection.DOWN);
                    break;
                case "3":
                    handlePlayerMove(MovementDirection.LEFT);
                    break;
                case "4":
                    handlePlayerMove(MovementDirection.RIGHT);
                    break;
                case "5":
                    showGameState();
                    break;
                case "6":
                    handleMedicalKitUse();
                    break;
                case "7":
                    toggleDebugMode();
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

    private void handlePlayerMove(MovementDirection direction) {
        Position targetPosition = direction.getNextPosition(player.getCurrentPosition());
        MoveResult result = board.movePlayer(player, direction);

        switch (result) {
            case SUCCESS:
                showGameState();
                finishPlayerTurn();
                break;
            case OUT_OF_BOUNDS:
                System.out.println("Movimento invalido: voce sairia dos limites do tabuleiro.");
                break;
            case WALL:
                System.out.println("Movimento invalido: ha uma parede nessa posicao.");
                break;
            case DINOSAUR:
                handleCombat(targetPosition);
                break;
            case SUPPLY_BOX:
                collectSupplyBoxAt(player.getCurrentPosition());
                showGameState();
                finishPlayerTurn();
                break;
            default:
                System.out.println("Movimento invalido.");
                break;
        }
    }

    private void handleCombat(Position targetPosition) {
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
                showGameState();
                finishPlayerTurn();
                break;
            case PLAYER_DEFEATED:
                consoleUI.showPlayerStatus(player);
                updateGameStatus();
                break;
            case FLED:
                showGameState();
                finishPlayerTurn();
                break;
            default:
                break;
        }
    }

    private void handleMedicalKitUse() {
        if (!player.hasMedicalKit()) {
            System.out.println("Voce nao possui kit medico.");
            consoleUI.showPlayerStatus(player);
            return;
        }

        int recoveredHealth = player.useMedicalKit(MEDICAL_KIT_HEALING);
        System.out.println("Voce usou um kit medico.");

        if (recoveredHealth > 0) {
            System.out.println("Saude recuperada: " + recoveredHealth);
        } else {
            System.out.println("Sua saude ja estava no maximo.");
        }

        consoleUI.showPlayerStatus(player);
        finishPlayerTurn();
    }

    private void finishPlayerTurn() {
        updateGameStatus();

        if (!isGameRunning()) {
            return;
        }

        System.out.println("Turno dos dinossauros.");
        dinosaurMovementService.moveDinosaurs(board, player, dinosaurs);
        showGameState();
        updateGameStatus();
    }

    private void toggleDebugMode() {
        debugMode = !debugMode;
        System.out.println(debugMode ? "Modo DEBUG ativado." : "Modo DEBUG desativado.");
        showGameState();
    }

    private void showGameState() {
        boardRenderer.print(board, player, debugMode);
        consoleUI.showPlayerStatus(player);
    }

    private void updateGameStatus() {
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

    private void collectSupplyBoxAt(Position position) {
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
