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
    private final List<DinosaurState> dinosaurStates;
    private final List<SupplyBoxState> supplyBoxStates;

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
        this.dinosaurStates = snapshotDinosaurs(dinosaurs);
        this.supplyBoxStates = snapshotSupplyBoxes(supplyBoxes);
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
        List<Dinosaur> restoredDinosaurs = new ArrayList<>();

        for (DinosaurState state : dinosaurStates) {
            restoredDinosaurs.add(state.restore());
        }

        return restoredDinosaurs;
    }

    public List<SupplyBox> restoreSupplyBoxes() {
        List<SupplyBox> restoredSupplyBoxes = new ArrayList<>();

        for (SupplyBoxState state : supplyBoxStates) {
            restoredSupplyBoxes.add(state.restore());
        }

        return restoredSupplyBoxes;
    }

    private List<DinosaurState> snapshotDinosaurs(List<Dinosaur> dinosaurs) {
        Objects.requireNonNull(dinosaurs, "A lista de dinossauros e obrigatoria.");
        List<DinosaurState> states = new ArrayList<>();

        for (Dinosaur dinosaur : dinosaurs) {
            states.add(new DinosaurState(dinosaur));
        }

        return Collections.unmodifiableList(states);
    }

    private List<SupplyBoxState> snapshotSupplyBoxes(List<SupplyBox> supplyBoxes) {
        Objects.requireNonNull(supplyBoxes, "A lista de caixas e obrigatoria.");
        List<SupplyBoxState> states = new ArrayList<>();

        for (SupplyBox supplyBox : supplyBoxes) {
            states.add(new SupplyBoxState(supplyBox));
        }

        return Collections.unmodifiableList(states);
    }

    private static final class DinosaurState {
        private final DinosaurType type;
        private final int health;
        private final Position position;

        private DinosaurState(Dinosaur dinosaur) {
            Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
            this.type = DinosaurType.from(dinosaur);
            this.health = dinosaur.getHealth();
            this.position = dinosaur.getCurrentPosition();
        }

        private Dinosaur restore() {
            Dinosaur dinosaur = type.create(position);
            dinosaur.setHealth(health);
            return dinosaur;
        }
    }

    private enum DinosaurType {
        COMPSOGNATHUS,
        VELOCIRAPTOR,
        TROODON,
        T_REX;

        private static DinosaurType from(Dinosaur dinosaur) {
            if (dinosaur instanceof Compsognathus) {
                return COMPSOGNATHUS;
            }

            if (dinosaur instanceof Velociraptor) {
                return VELOCIRAPTOR;
            }

            if (dinosaur instanceof Troodon) {
                return TROODON;
            }

            if (dinosaur instanceof TRex) {
                return T_REX;
            }

            throw new IllegalArgumentException("Tipo de dinossauro nao suportado no estado inicial.");
        }

        private Dinosaur create(Position position) {
            switch (this) {
                case COMPSOGNATHUS:
                    return new Compsognathus(position);
                case VELOCIRAPTOR:
                    return new Velociraptor(position);
                case TROODON:
                    return new Troodon(position);
                case T_REX:
                    return new TRex(position);
                default:
                    throw new IllegalStateException("Tipo de dinossauro invalido.");
            }
        }
    }

    private static final class SupplyBoxState {
        private final Position position;
        private final ItemType itemType;

        private SupplyBoxState(SupplyBox supplyBox) {
            Objects.requireNonNull(supplyBox, "A caixa de suprimentos e obrigatoria.");
            this.position = supplyBox.getPosition();
            this.itemType = ItemType.from(supplyBox.getContent());
        }

        private SupplyBox restore() {
            return new SupplyBox(position, itemType.create());
        }
    }

    private enum ItemType {
        MEDICAL_KIT,
        ELECTRIC_BATON,
        TRANQUILIZER_GUN,
        SURPRISE_COMPSOGNATHUS;

        private static ItemType from(Item item) {
            if (item instanceof MedicalKit) {
                return MEDICAL_KIT;
            }

            if (item instanceof ElectricBaton) {
                return ELECTRIC_BATON;
            }

            if (item instanceof TranquilizerGun) {
                return TRANQUILIZER_GUN;
            }

            if (item instanceof SurpriseCompsognathus) {
                return SURPRISE_COMPSOGNATHUS;
            }

            throw new IllegalArgumentException("Tipo de item nao suportado no estado inicial.");
        }

        private Item create() {
            switch (this) {
                case MEDICAL_KIT:
                    return new MedicalKit();
                case ELECTRIC_BATON:
                    return new ElectricBaton();
                case TRANQUILIZER_GUN:
                    return new TranquilizerGun();
                case SURPRISE_COMPSOGNATHUS:
                    return new SurpriseCompsognathus();
                default:
                    throw new IllegalStateException("Tipo de item invalido.");
            }
        }
    }
}
