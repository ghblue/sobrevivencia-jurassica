package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class InitialGameState {
    private final Difficulty difficulty;
    private final Board board;
    private final int playerInitialHealth;
    private final int playerPerception;
    private final Position playerPosition;
    private final List<Dinosaur> initialDinosaurs;
    private final List<SupplyBox> initialSupplyBoxes;

    public InitialGameState(
            Difficulty difficulty,
            Board board,
            Player player,
            List<Dinosaur> dinosaurs,
            List<SupplyBox> supplyBoxes
    ) {
        this.difficulty = Objects.requireNonNull(difficulty, "A dificuldade e obrigatoria.");
        this.board = Objects.requireNonNull(board, "O tabuleiro e obrigatorio.").copy();

        Player initialPlayer = Objects.requireNonNull(player, "O jogador e obrigatorio.");
        this.playerInitialHealth = initialPlayer.getMaxHealth();
        this.playerPerception = initialPlayer.getPerception();
        this.playerPosition = initialPlayer.getCurrentPosition();
        this.initialDinosaurs = copyDinosaurs(dinosaurs);
        this.initialSupplyBoxes = copySupplyBoxes(supplyBoxes);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Board restoreBoard() {
        return board.copy();
    }

    public Player restorePlayer() {
        return new Player(playerInitialHealth, playerPerception, playerPosition);
    }

    public List<Dinosaur> restoreDinosaurs() {
        return new ArrayList<>(copyDinosaurs(initialDinosaurs));
    }

    public List<SupplyBox> restoreSupplyBoxes() {
        return new ArrayList<>(copySupplyBoxes(initialSupplyBoxes));
    }

    private List<Dinosaur> copyDinosaurs(List<Dinosaur> dinosaurs) {
        Objects.requireNonNull(dinosaurs, "A lista de dinossauros e obrigatoria.");
        List<Dinosaur> copies = new ArrayList<>();

        for (Dinosaur dinosaur : dinosaurs) {
            copies.add(dinosaur.copy());
        }

        // Copias independentes impedem que uma partida altere os proximos reinicios.
        return Collections.unmodifiableList(copies);
    }

    private List<SupplyBox> copySupplyBoxes(List<SupplyBox> supplyBoxes) {
        Objects.requireNonNull(supplyBoxes, "A lista de caixas e obrigatoria.");
        List<SupplyBox> copies = new ArrayList<>();

        for (SupplyBox supplyBox : supplyBoxes) {
            copies.add(supplyBox.copy());
        }

        return Collections.unmodifiableList(copies);
    }
}
