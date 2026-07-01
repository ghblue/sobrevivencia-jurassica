package jogo.servicos;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import jogo.dinossauros.Dinosaur;
import jogo.enums.CombatResult;
import jogo.enums.MovementDirection;
import jogo.modelo.Board;
import jogo.modelo.Player;
import jogo.modelo.Position;

/**
 * Executa o turno de movimentação dos dinossauros.
 * Cada espécie informa quantos passos tenta dar e um encontro inicia combate.
 */
public class DinosaurMovementService {
    private final Random random;
    private final CombatService combatService;

    public DinosaurMovementService(Random random, CombatService combatService) {
        this.random = Objects.requireNonNull(random, "O gerador aleatorio e obrigatorio.");
        this.combatService = Objects.requireNonNull(combatService, "O servico de combate e obrigatorio.");
    }

    // Executa o turno de todos os dinossauros enquanto o jogador estiver vivo.
    public void moveDinosaurs(Board board, Player player, List<Dinosaur> dinosaurs) {
        Objects.requireNonNull(board, "O tabuleiro e obrigatorio.");
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Objects.requireNonNull(dinosaurs, "A lista de dinossauros e obrigatoria.");

        for (int index = 0; index < dinosaurs.size() && player.isAlive(); index++) {
            Dinosaur dinosaur = dinosaurs.get(index);

            // A lista pode diminuir durante o turno quando o jogador vence um combate.
            boolean removed = moveDinosaur(board, player, dinosaurs, dinosaur);

            if (removed) {
                index--;
            }
        }
    }

    // Tenta a quantidade de passos definida pela espécie do dinossauro.
    private boolean moveDinosaur(Board board, Player player, List<Dinosaur> dinosaurs, Dinosaur dinosaur) {
        int stepCount = dinosaur.getMovementStepCount();

        for (int step = 0; step < stepCount && player.isAlive(); step++) {
            // Cada passo sorteia uma nova direção e ignora destinos bloqueados.
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

    // Sorteia uma das quatro direções disponíveis para cada tentativa de passo.
    private MovementDirection getRandomDirection() {
        MovementDirection[] directions = MovementDirection.values();
        return directions[random.nextInt(directions.length)];
    }

    // Inicia combate quando o dinossauro tenta entrar na posição do jogador.
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
