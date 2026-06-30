package game;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DinosaurMovementService {
    private final Random random;
    private final CombatService combatService;

    public DinosaurMovementService(Random random, CombatService combatService) {
        this.random = Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
        this.combatService = Objects.requireNonNull(combatService, "O servico de combate e obrigatorio.");
    }

    public void moveDinosaurs(Board board, Player player, List<Dinosaur> dinosaurs) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Objects.requireNonNull(dinosaurs, "A lista de dinossauros e obrigatoria.");

        for (int index = 0; index < dinosaurs.size() && player.isAlive(); index++) {
            Dinosaur dinosaur = dinosaurs.get(index);

            boolean removed = moveDinosaur(board, player, dinosaurs, dinosaur);

            if (removed) {
                index--;
            }
        }
    }

    private boolean moveDinosaur(Board board, Player player, List<Dinosaur> dinosaurs, Dinosaur dinosaur) {
        int stepCount = dinosaur.getMovementStepCount();

        for (int step = 0; step < stepCount && player.isAlive(); step++) {
            MovementDirection direction = getRandomDirection();
            Position nextPosition = direction.getNextPosition(dinosaur.getCurrentPosition());

            if (!board.canDinosaurMoveTo(nextPosition)) {
                continue;
            }

            if (board.isPlayerAt(nextPosition)) {
                return handleDinosaurFoundPlayer(board, player, dinosaurs, dinosaur);
            }

            board.moveDinosaurTo(dinosaur, nextPosition);
        }

        return false;
    }

    private MovementDirection getRandomDirection() {
        MovementDirection[] directions = MovementDirection.values();
        return directions[random.nextInt(directions.length)];
    }

    private boolean handleDinosaurFoundPlayer(
            Board board,
            Player player,
            List<Dinosaur> dinosaurs,
            Dinosaur dinosaur
    ) {
        System.out.println(dinosaur.getName() + " encontrou o jogador durante a movimentacao.");
        System.out.println("Voce foi surpreendido pelo " + dinosaur.getName() + ".");

        CombatResult result = combatService.startCombat(player, dinosaur, true);

        if (result == CombatResult.PLAYER_WON) {
            dinosaurs.remove(dinosaur);
            board.removeDinosaur(dinosaur);
            System.out.println("Vitoria no combate.");
            return true;
        }

        return false;
    }
}
