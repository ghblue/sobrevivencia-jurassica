package game;

public class ElectricBaton extends Item {
    public ElectricBaton() {
        super("Bastao eletrico");
    }

    @Override
    public String applyTo(Player player) {
        player.addElectricBaton();
        return "Bastao eletrico adicionado ao inventario.";
    }
}
