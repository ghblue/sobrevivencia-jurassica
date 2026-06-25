package game;

public class MedicalKit extends Item {
    public MedicalKit() {
        super("Kit medico");
    }

    @Override
    public String applyTo(Player player) {
        player.addMedicalKit();
        return "Kit medico adicionado ao inventario.";
    }
}
