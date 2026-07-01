package jogo.itens;

import jogo.modelo.Player;

/**
 * Item que libera o bastão elétrico como opção permanente de ataque.
 */
public class ElectricBaton extends Item {
    public ElectricBaton() {
        super("Bastao eletrico");
    }

    // Libera o bastão elétrico como opção de ataque do jogador.
    @Override
    public String applyTo(Player player) {
        player.addElectricBaton();
        return "Bastao eletrico adicionado ao inventario.";
    }

    @Override
    public Item copy() {
        return new ElectricBaton();
    }
}
