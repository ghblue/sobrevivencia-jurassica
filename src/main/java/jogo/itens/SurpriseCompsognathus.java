package jogo.itens;

import jogo.modelo.Player;

public class SurpriseCompsognathus extends Item {
    public SurpriseCompsognathus() {
        super("Compsognathus surpresa");
    }

    // Mantem a descricao do conteúdo; o encontro real é resolvido pelo Game.
    @Override
    public String applyTo(Player player) {
        return "Um Compsognato estava escondido na caixa!";
    }

    @Override
    public Item copy() {
        return new SurpriseCompsognathus();
    }
}
