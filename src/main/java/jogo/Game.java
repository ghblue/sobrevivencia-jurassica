package jogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import jogo.dinossauros.Compsognathus;
import jogo.dinossauros.Dinosaur;
import jogo.dinossauros.TRex;
import jogo.dinossauros.Troodon;
import jogo.dinossauros.Velociraptor;
import jogo.enums.CombatResult;
import jogo.enums.Difficulty;
import jogo.enums.GameStatus;
import jogo.enums.MoveResult;
import jogo.enums.MovementDirection;
import jogo.itens.ElectricBaton;
import jogo.itens.Item;
import jogo.itens.MedicalKit;
import jogo.itens.SupplyBox;
import jogo.itens.SurpriseCompsognathus;
import jogo.itens.TranquilizerGun;
import jogo.modelo.Board;
import jogo.modelo.InitialGameState;
import jogo.modelo.Player;
import jogo.modelo.Position;
import jogo.servicos.BoardRenderer;
import jogo.servicos.CombatService;
import jogo.servicos.DinosaurMovementService;
import jogo.util.Dice;

/**
 * Controla o ciclo completo da aplicação e da partida.
 * Cria o cenário, processa menus e turnos e verifica vitória ou derrota.
 */
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

    // Prepara os serviços compartilhados e o estado vazio da aplicação.
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

    // Exibe a entrada da aplicação e inicia a primeira partida solicitada.
    public void start() {
        consoleUI.showWelcomeMessage();

        if (consoleUI.shouldStartGame()) {
            startNewGame();
            runApplicationLoop();
        }

        System.out.println("Saindo do jogo.");
    }

    // Controla a sequência de partidas até o usuário escolher sair.
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

    // Cria uma partida com nova dificuldade e novos sorteios de cenário.
    private void startNewGame() {
        // Um novo jogo escolhe a dificuldade e realiza todos os sorteios novamente.
        difficulty = consoleUI.chooseDifficulty();
        initializeBoard();
        initialGameState = new InitialGameState(difficulty, board, player, dinosaurs, supplyBoxes);
        prepareGameToRun();
    }

    // Monta o tabuleiro e posiciona jogador, dinossauros, paredes e caixas.
    private void initializeBoard() {
        board = new Board();
        Position initialPosition = board.getInitialPlayerPosition();
        player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        // A ordem evita sobreposição: jogador, dinossauros, paredes e, por último, caixas.
        board.placePlayer(player);
        dinosaurs = createDinosaurs(board);
        board.generateRandomWalls(random);
        supplyBoxes = createSupplyBoxes(board);
    }

    // Restaura a partida exatamente a partir das cópias do estado inicial.
    private void restartCurrentGame() {
        // Reiniciar restaura cópias do estado original, sem realizar novos sorteios.
        difficulty = initialGameState.getDifficulty();
        board = initialGameState.restoreBoard();
        player = initialGameState.restorePlayer();
        dinosaurs = initialGameState.restoreDinosaurs();
        supplyBoxes = initialGameState.restoreSupplyBoxes();
        prepareGameToRun();
    }

    // Redefine os controles necessários antes de executar uma partida.
    private void prepareGameToRun() {
        gameStatus = GameStatus.RUNNING;
        debugMode = false;
    }

    // Exibe o estado inicial e entra no menu de turnos da partida atual.
    private void runCurrentGame() {
        showGameState();
        runGameMenu();
    }

    // Cria todas as espécies e mantém o T-Rex na extremidade oposta.
    private List<Dinosaur> createDinosaurs(Board board) {
        List<Dinosaur> createdDinosaurs = new ArrayList<>();

        // O T-Rex sempre começa na extremidade oposta à posição inicial do jogador.
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

    // Cria uma caixa para cada conteúdo previsto nas regras do jogo.
    private List<SupplyBox> createSupplyBoxes(Board board) {
        List<SupplyBox> createdSupplyBoxes = new ArrayList<>();

        // Cada caixa recebe um conteúdo específico e ocupa uma posição livre sorteada.
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new MedicalKit()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new ElectricBaton()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new TranquilizerGun()), board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new SurpriseCompsognathus()), board);

        return createdSupplyBoxes;
    }

    // Registra um dinossauro tanto no tabuleiro quanto na lista ativa.
    private void addDinosaur(List<Dinosaur> createdDinosaurs, Dinosaur dinosaur, Board board) {
        board.placeDinosaur(dinosaur);
        createdDinosaurs.add(dinosaur);
    }

    // Registra uma caixa no tabuleiro e na lista usada para coleta.
    private void addSupplyBox(List<SupplyBox> createdSupplyBoxes, SupplyBox supplyBox, Board board) {
        board.placeSupplyBox(supplyBox);
        createdSupplyBoxes.add(supplyBox);
    }

    // Processa as escolhas do jogador enquanto a partida estiver em andamento.
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

    // Trata o resultado de uma tentativa de movimento do jogador.
    private void handlePlayerMove(MovementDirection direction) {
        Position targetPosition = direction.getNextPosition(player.getCurrentPosition());
        // Board valida o destino e devolve o evento que o controlador deve tratar.
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

    // Localiza o dinossauro encontrado e aplica o resultado do combate.
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

    // Consome um kit médico e encerra o turno quando ele está disponível.
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

    // Finaliza a ação do jogador e executa o turno dos dinossauros.
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

    // Alterna entre a visão limitada e a exibição completa do tabuleiro.
    private void toggleDebugMode() {
        debugMode = !debugMode;
        // O modo DEBUG altera somente a renderização; nenhuma regra da partida é modificada.
        System.out.println(debugMode ? "Modo DEBUG ativado." : "Modo DEBUG desativado.");
        showGameState();
    }

    // Renderiza o mapa e apresenta os dados atuais do jogador.
    private void showGameState() {
        boardRenderer.print(board, player, debugMode);
        consoleUI.showPlayerStatus(player);
    }

    // Verifica derrota por falta de vida e vitória sem dinossauros ativos.
    private void updateGameStatus() {
        if (!isGameRunning()) {
            return;
        }

        // A partida termina em derrota sem saúde ou em vitória sem dinossauros ativos.
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

    // Encerra a partida e apresenta a mensagem correspondente ao resultado.
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

    // Coleta a caixa da posição atual e aplica seu item ao jogador.
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
