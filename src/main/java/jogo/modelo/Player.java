package jogo.modelo;

import java.util.Objects;

/**
 * Representa o jogador controlado pelo usuário.
 * Mantém saúde, percepção, posição e os recursos do inventário.
 */
public class Player implements Movable {
    public static final int INITIAL_HEALTH = 5;

    private int health;
    private final int maxHealth;
    private int perception;
    private Position currentPosition;
    private int medicalKitCount;
    private boolean hasElectricBaton;
    private int tranquilizerAmmo;

    public Player(int health, int perception, Position currentPosition) {
        if (health <= 0) {
            throw new IllegalArgumentException("A saude inicial deve ser positiva.");
        }

        // A saúde máxima é definida no início da partida e limita toda cura posterior.
        this.maxHealth = health;
        this.health = health;
        setPerception(perception);
        setCurrentPosition(currentPosition);
        this.medicalKitCount = 0;
        this.hasElectricBaton = false;
        this.tranquilizerAmmo = 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    private void setHealth(int health) {
        if (health < 0) {
            throw new IllegalArgumentException("A saude nao pode ser negativa.");
        }

        this.health = Math.min(health, maxHealth);
    }

    public boolean isAlive() {
        return health > 0;
    }

    // Reduz a saúde sem permitir que ela fique abaixo de zero.
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("O dano nao pode ser negativo.");
        }

        setHealth(Math.max(0, health - damage));
    }

    // Recupera saúde respeitando o limite máximo definido no início.
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("A cura nao pode ser negativa.");
        }

        setHealth(health + amount);
    }

    public int getPerception() {
        return perception;
    }

    public final void setPerception(int perception) {
        if (perception < 0) {
            throw new IllegalArgumentException("A percepcao nao pode ser negativa.");
        }

        // A percepção é comparada com a rolagem de esquiva durante o combate.
        this.perception = perception;
    }

    @Override
    public Position getCurrentPosition() {
        return currentPosition;
    }

    private void setCurrentPosition(Position currentPosition) {
        this.currentPosition = Objects.requireNonNull(currentPosition, "A posicao atual e obrigatoria.");
    }

    @Override
    public void moveTo(Position newPosition) {
        setCurrentPosition(newPosition);
    }

    public int getMedicalKitCount() {
        return medicalKitCount;
    }

    public boolean hasMedicalKit() {
        return medicalKitCount > 0;
    }

    public boolean hasElectricBaton() {
        return hasElectricBaton;
    }

    public int getTranquilizerAmmo() {
        return tranquilizerAmmo;
    }

    public void addMedicalKit() {
        medicalKitCount++;
    }

    // Consome um kit e devolve a quantidade de saúde realmente recuperada.
    public int useMedicalKit(int healingAmount) {
        if (healingAmount < 0) {
            throw new IllegalArgumentException("A cura nao pode ser negativa.");
        }

        if (!hasMedicalKit()) {
            throw new IllegalStateException("Nao ha kit medico disponivel.");
        }

        int previousHealth = health;
        medicalKitCount--;
        heal(healingAmount);
        return health - previousHealth;
    }

    public void addElectricBaton() {
        hasElectricBaton = true;
    }

    public void addTranquilizerAmmo() {
        tranquilizerAmmo++;
    }

    // Consome uma munição antes de realizar o ataque tranquilizante.
    public void useTranquilizerAmmo() {
        if (tranquilizerAmmo <= 0) {
            throw new IllegalStateException("Nao ha municao de dardo disponivel.");
        }

        tranquilizerAmmo--;
    }

    public String getInventoryStatus() {
        return String.format(
                "Kits medicos: %d%nBastao eletrico: %s%nMunicoes de dardo: %d %n -------------------",
                medicalKitCount,
                hasElectricBaton ? "sim" : "nao",
                tranquilizerAmmo);
    }
}
