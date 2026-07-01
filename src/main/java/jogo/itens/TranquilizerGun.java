package jogo.itens;

import jogo.modelo.Player;

/**
 * Item que adiciona uma munição de dardo tranquilizante ao inventário.
 */
public class TranquilizerGun extends Item {
    public TranquilizerGun() {
        super("Arma de dardos tranquilizantes");
    }

    // Acrescenta uma munição para o ataque com dardo tranquilizante.
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
