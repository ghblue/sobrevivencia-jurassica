package jogo.itens;

import jogo.modelo.Player;

/**
 * Item que adiciona uma munição de dardo tranquilizante ao inventário.
 */
public class TranquilizerGun extends Item {
    private static final int DANO_DARDO = 2;

    public TranquilizerGun() {
        super("Arma de dardos tranquilizantes");
    }

    // Informa o dano fixo causado pelo dardo quando o alvo pode ser atingido.
    public int calcularDano() {
        return DANO_DARDO;
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
