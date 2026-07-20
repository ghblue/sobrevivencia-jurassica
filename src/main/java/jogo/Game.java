package jogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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
import jogo.interfaceusuario.InterfaceUsuario;
import jogo.interfaceusuario.console.InterfaceConsole;
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
import jogo.resultado.ResultadoAcao;
import jogo.resultado.TipoResultadoAcao;
import jogo.servicos.CombatService;
import jogo.servicos.DinosaurMovementService;
import jogo.util.Dice;

/**
 * Controla o ciclo completo da aplicação e da partida.
 * Cria o cenário, processa menus e turnos e verifica vitória ou derrota.
 */
public class Game {

    // esses valores poderiam estar em enums
    private static final int COMPSOGNATHUS_COUNT = 2;
    private static final int VELOCIRAPTOR_COUNT = 2;
    private static final int TROODON_COUNT = 5;
    private static final int MEDICAL_KIT_HEALING = 1;

    private final InterfaceUsuario interfaceUsuario;
    private final Random random;
    private final CombatService combatService;
    private final DinosaurMovementService dinosaurMovementService;
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
        this(new InterfaceConsole());
    }

    public Game(InterfaceUsuario interfaceUsuario) {
        this.interfaceUsuario = Objects.requireNonNull(interfaceUsuario, "A interface de usuario e obrigatoria.");
        this.random = new Random();
        this.combatService = new CombatService(interfaceUsuario, new Dice(random));
        this.dinosaurMovementService = new DinosaurMovementService(random, combatService);
        this.dinosaurs = new ArrayList<>();
        this.supplyBoxes = new ArrayList<>();
        this.gameStatus = GameStatus.EXITED;
        this.debugMode = false;
    }

    // Mantem o ponto de entrada antigo para quem ainda iniciar o jogo pelo
    // controlador.
    public void start() {
        if (interfaceUsuario instanceof InterfaceConsole) {
            ((InterfaceConsole) interfaceUsuario).iniciar(this);
            return;
        }

        new InterfaceConsole().iniciar(this);
    }

    // Cria uma partida com nova dificuldade e novos sorteios de cenário.
    public ResultadoAcao iniciarNovaPartida(Difficulty dificuldade) {
        difficulty = Objects.requireNonNull(dificuldade, "A dificuldade e obrigatoria.");
        initializeBoard();
        initialGameState = new InitialGameState(difficulty, board, player, dinosaurs, supplyBoxes);
        prepareGameToRun();
        return criarResultado(TipoResultadoAcao.PARTIDA_INICIADA, true, false, new ArrayList<String>());
    }

    // Restaura a partida exatamente a partir das cópias do estado inicial.
    public ResultadoAcao reiniciarPartida() {
        if (initialGameState == null) {
            return criarResultado(
                    TipoResultadoAcao.SEM_PARTIDA,
                    false,
                    false,
                    "Nao ha partida para reiniciar.");
        }

        // Reiniciar restaura cópias do estado original, sem realizar novos sorteios.
        difficulty = initialGameState.getDifficulty();
        board = initialGameState.restoreBoard();
        player = initialGameState.restorePlayer();
        dinosaurs = initialGameState.restoreDinosaurs();
        supplyBoxes = initialGameState.restoreSupplyBoxes();
        prepareGameToRun();
        return criarResultado(TipoResultadoAcao.PARTIDA_REINICIADA, true, false, new ArrayList<String>());
    }

    // Executa uma tentativa de movimento sem depender de uma opcao digitada no
    // menu.
    public ResultadoAcao moverJogador(MovementDirection direction) {
        if (!isGameRunning()) {
            return criarResultado(
                    TipoResultadoAcao.SEM_PARTIDA,
                    false,
                    false,
                    "Nao ha partida em andamento.");
        }

        Objects.requireNonNull(direction, "A direcao e obrigatoria.");
        Position previousPosition = player.getCurrentPosition();
        Position targetPosition = direction.getNextPosition(previousPosition);
        List<String> mensagens = new ArrayList<>();

        // Board valida o destino e devolve o evento que o controlador deve tratar.
        MoveResult result = board.movePlayer(player, direction);

        switch (result) {
            case SUCCESS:
                finishPlayerTurn(mensagens);
                return criarResultado(TipoResultadoAcao.MOVIMENTO_REALIZADO, true, false, mensagens);
            case OUT_OF_BOUNDS:
                mensagens.add("Movimento invalido: voce sairia dos limites do tabuleiro.");
                return criarResultado(TipoResultadoAcao.MOVIMENTO_BLOQUEADO, false, false, mensagens);
            case WALL:
                mensagens.add("Movimento invalido: ha uma parede nessa posicao.");
                return criarResultado(TipoResultadoAcao.MOVIMENTO_BLOQUEADO, false, false, mensagens);
            case DINOSAUR:
                return handleCombat(targetPosition, mensagens);
            case SUPPLY_BOX:
                return handleSupplyBox(targetPosition, previousPosition, mensagens);
            default:
                mensagens.add("Movimento invalido.");
                return criarResultado(TipoResultadoAcao.MOVIMENTO_BLOQUEADO, false, false, mensagens);
        }
    }

    // Consome um kit médico e encerra o turno quando ele está disponível.
    public ResultadoAcao usarKitMedico() {
        if (!isGameRunning()) {
            return criarResultado(
                    TipoResultadoAcao.SEM_PARTIDA,
                    false,
                    false,
                    "Nao ha partida em andamento.");
        }

        List<String> mensagens = new ArrayList<>();

        if (!player.hasMedicalKit()) {
            mensagens.add("Voce nao possui kit medico.");
            return criarResultado(TipoResultadoAcao.JOGADOR_SEM_KIT, false, true, mensagens);
        }

        int recoveredHealth = player.useMedicalKit(MEDICAL_KIT_HEALING);
        mensagens.add("Voce usou um kit medico.");

        if (recoveredHealth > 0) {
            mensagens.add("Saude recuperada: " + recoveredHealth);
        } else {
            mensagens.add("Sua saude ja estava no maximo.");
        }

        finishPlayerTurn(mensagens);
        return criarResultado(TipoResultadoAcao.JOGADOR_CURADO, true, false, mensagens);
    }

    // Alterna entre a visão limitada e a exibição completa do tabuleiro.
    public ResultadoAcao alternarModoDebug() {
        if (!temPartidaPreparada()) {
            return criarResultado(
                    TipoResultadoAcao.SEM_PARTIDA,
                    false,
                    false,
                    "Nao ha partida em andamento.");
        }

        debugMode = !debugMode;
        // O modo DEBUG altera somente a renderização; nenhuma regra da partida é
        // modificada.
        return criarResultado(
                TipoResultadoAcao.DEBUG_ALTERNADO,
                true,
                false,
                debugMode ? "Modo DEBUG ativado." : "Modo DEBUG desativado.");
    }

    public ResultadoAcao encerrarPartida() {
        if (!isGameRunning()) {
            return criarResultado(
                    TipoResultadoAcao.SEM_PARTIDA,
                    false,
                    false,
                    "Nao ha partida em andamento.");
        }

        List<String> mensagens = new ArrayList<>();
        endGame(GameStatus.EXITED, mensagens);
        return criarResultado(TipoResultadoAcao.PARTIDA_ENCERRADA, false, false, mensagens);
    }

    public boolean isGameRunning() {
        return gameStatus == GameStatus.RUNNING;
    }

    public boolean temPartidaPreparada() {
        return board != null && player != null;
    }

    public Board getBoard() {
        return board;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Dinosaur> getDinosaurs() {
        return Collections.unmodifiableList(dinosaurs);
    }

    public List<SupplyBox> getSupplyBoxes() {
        return Collections.unmodifiableList(supplyBoxes);
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    // Monta o tabuleiro e posiciona jogador, dinossauros, paredes e caixas.
    private void initializeBoard() {
        board = new Board();
        Position initialPosition = board.getInitialPlayerPosition();
        player = new Player(Player.INITIAL_HEALTH, difficulty.getPerception(), initialPosition);

        // A ordem evita sobreposição: jogador, dinossauros, paredes e, por último,
        // caixas.
        board.placePlayer(player);
        dinosaurs = createDinosaurs(board);
        board.generateRandomWalls(random);
        supplyBoxes = createSupplyBoxes(board);
    }

    // Redefine os controles necessários antes de executar uma partida.
    private void prepareGameToRun() {
        gameStatus = GameStatus.RUNNING;
        debugMode = false;
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
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new ElectricBaton()),
                board);
        addSupplyBox(createdSupplyBoxes, new SupplyBox(board.getRandomFreePosition(random), new TranquilizerGun()),
                board);
        addSupplyBox(createdSupplyBoxes,
                new SupplyBox(board.getRandomFreePosition(random), new SurpriseCompsognathus()), board);

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

    // Localiza o dinossauro encontrado e aplica o resultado do combate.
    private ResultadoAcao handleCombat(Position targetPosition, List<String> mensagens) {
        Dinosaur dinosaur = findDinosaurAt(targetPosition);

        if (dinosaur == null) {
            mensagens.add("Voce encontrou um dinossauro, mas ele nao foi localizado na lista ativa.");
            return criarResultado(TipoResultadoAcao.DINOSSAURO_ENCONTRADO, false, false, mensagens);
        }

        CombatResult result = combatService.startCombat(player, dinosaur);

        switch (result) {
            case PLAYER_WON:
                dinosaurs.remove(dinosaur);
                board.movePlayerToDefeatedDinosaurPosition(player, dinosaur);
                mensagens.add("Vitoria no combate.");
                finishPlayerTurn(mensagens);
                return criarResultado(TipoResultadoAcao.DINOSSAURO_ENCONTRADO, true, false, mensagens);
            case PLAYER_DEFEATED:
                updateGameStatus(mensagens);
                return criarResultado(TipoResultadoAcao.DERROTA, false, true, mensagens);
            case FLED:
                finishPlayerTurn(mensagens);
                return criarResultado(TipoResultadoAcao.DINOSSAURO_ENCONTRADO, true, false, mensagens);
            default:
                return criarResultado(TipoResultadoAcao.DINOSSAURO_ENCONTRADO, false, false, mensagens);
        }
    }

    // Finaliza a ação do jogador e executa o turno dos dinossauros.
    private void finishPlayerTurn(List<String> mensagens) {
        updateGameStatus(mensagens);

        if (!isGameRunning()) {
            return;
        }

        mensagens.add("Turno dos dinossauros.");
        mensagens.addAll(dinosaurMovementService.moveDinosaurs(board, player, dinosaurs));
        updateGameStatus(mensagens);
    }

    // Verifica derrota por falta de vida e vitória sem dinossauros ativos ou escondidos.
    private void updateGameStatus(List<String> mensagens) {
        if (!isGameRunning()) {
            return;
        }

        // A partida termina em derrota sem saúde ou em vitória sem dinossauros ativos.
        if (!player.isAlive()) {
            endGame(GameStatus.DEFEAT, mensagens);
            return;
        }

        if (dinosaurs.isEmpty() && !existeCompsognathusEscondido()) {
            endGame(GameStatus.VICTORY, mensagens);
        }
    }

    // Encerra a partida e registra a mensagem correspondente ao resultado.
    private void endGame(GameStatus finalStatus, List<String> mensagens) {
        if (!isGameRunning()) {
            return;
        }

        gameStatus = finalStatus;

        switch (finalStatus) {
            case VICTORY:
                mensagens.add("Vitoria! Todos os dinossauros foram derrotados.");
                mensagens.add("Partida finalizada com vitoria.");
                break;
            case DEFEAT:
                mensagens.add("Derrota. Sua saude chegou a 0.");
                mensagens.add("Partida finalizada com derrota.");
                break;
            case EXITED:
                mensagens.add("Jogo encerrado pelo jogador.");
                mensagens.add("Partida finalizada.");
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

    // Coleta a caixa encontrada ou revela o Compsognathus escondido.
    private ResultadoAcao handleSupplyBox(Position position, Position previousPosition, List<String> mensagens) {
        for (int index = 0; index < supplyBoxes.size(); index++) {
            SupplyBox supplyBox = supplyBoxes.get(index);

            if (supplyBox.getPosition().equals(position)) {
                Item item = supplyBox.getContent();

                if (item instanceof SurpriseCompsognathus) {
                    return handleSurpriseCompsognathus(index, position, previousPosition, mensagens);
                }

                mensagens.add("Voce encontrou uma caixa de suprimentos!");
                mensagens.add("Conteudo: " + item.getName());
                mensagens.add(item.applyTo(player));
                supplyBoxes.remove(index);
                board.removeSupplyBoxAt(position);
                finishPlayerTurn(mensagens);
                return criarResultado(TipoResultadoAcao.CAIXA_COLETADA, true, false, mensagens);
            }
        }

        mensagens.add("Voce encontrou uma caixa de suprimentos, mas o conteudo nao foi localizado.");
        return criarResultado(TipoResultadoAcao.CAIXA_COLETADA, false, false, mensagens);
    }

    // Revela o Compsognathus escondido e inicia o combate com o dinossauro atacando primeiro.
    private ResultadoAcao handleSurpriseCompsognathus(
            int supplyBoxIndex,
            Position position,
            Position previousPosition,
            List<String> mensagens
    ) {
        supplyBoxes.remove(supplyBoxIndex);
        board.removeSupplyBoxAt(position);
        board.movePlayerTo(player, previousPosition);

        Compsognathus compsognathus = new Compsognathus(position);
        board.placeDinosaur(compsognathus);
        dinosaurs.add(compsognathus);
        mensagens.add("Um Compsognato estava escondido na caixa!");

        CombatResult result = combatService.startCombat(player, compsognathus, true);

        switch (result) {
            case PLAYER_WON:
                dinosaurs.remove(compsognathus);
                board.movePlayerToDefeatedDinosaurPosition(player, compsognathus);
                mensagens.add("Vitoria no combate.");
                finishPlayerTurn(mensagens);
                return criarResultado(TipoResultadoAcao.CAIXA_COLETADA, true, false, mensagens);
            case PLAYER_DEFEATED:
                updateGameStatus(mensagens);
                return criarResultado(TipoResultadoAcao.DERROTA, false, true, mensagens);
            case FLED:
                updateGameStatus(mensagens);
                return criarResultado(TipoResultadoAcao.CAIXA_COLETADA, true, false, mensagens);
            default:
                return criarResultado(TipoResultadoAcao.CAIXA_COLETADA, true, false, mensagens);
        }
    }

    private boolean existeCompsognathusEscondido() {
        for (SupplyBox supplyBox : supplyBoxes) {
            if (supplyBox.getContent() instanceof SurpriseCompsognathus) {
                return true;
            }
        }

        return false;
    }

    private ResultadoAcao criarResultado(
            TipoResultadoAcao tipo,
            boolean exibirEstado,
            boolean exibirStatusJogador,
            String mensagem) {
        List<String> mensagens = new ArrayList<>();
        mensagens.add(mensagem);
        return criarResultado(tipo, exibirEstado, exibirStatusJogador, mensagens);
    }

    private ResultadoAcao criarResultado(
            TipoResultadoAcao tipo,
            boolean exibirEstado,
            boolean exibirStatusJogador,
            List<String> mensagens) {
        return new ResultadoAcao(
                ajustarTipoPeloEstado(tipo),
                gameStatus,
                exibirEstado,
                exibirStatusJogador,
                mensagens);
    }

    private TipoResultadoAcao ajustarTipoPeloEstado(TipoResultadoAcao tipoOriginal) {
        if (gameStatus == GameStatus.VICTORY) {
            return TipoResultadoAcao.VITORIA;
        }

        if (gameStatus == GameStatus.DEFEAT) {
            return TipoResultadoAcao.DERROTA;
        }

        return tipoOriginal;
    }
}
