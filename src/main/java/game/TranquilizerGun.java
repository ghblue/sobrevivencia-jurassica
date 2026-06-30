package game;

public class TranquilizerGun extends Item {
    public TranquilizerGun() {
        super("Arma de dardos tranquilizantes");
    }

    @Override
    public String applyTo(Player player) {
        player.addTranquilizerAmmo();
        return "Municao de dardo adicionada ao inventario.";
    }

    @Override
    public Item copy() {
        return new TranquilizerGun();
    }
}
