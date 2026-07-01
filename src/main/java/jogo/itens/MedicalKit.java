package jogo.itens;

import jogo.modelo.Player;

/**
 * Item que adiciona um kit médico ao inventário do jogador.
 */
public class MedicalKit extends Item {
    public MedicalKit() {
        super("Kit medico");
    }

    // Adiciona um kit ao inventário para que ele possa ser usado depois.
    @Override
    public String applyTo(Player player) {
        player.addMedicalKit();
        return "Kit medico adicionado ao inventario.";
    }

    @Override
    public Item copy() {
        return new MedicalKit();
    }
}
