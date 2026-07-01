package jogo.itens;

import jogo.modelo.Player;

/**
 * Conteúdo surpresa de caixa que, nesta versão, apenas informa o encontro.
 */
public class SurpriseCompsognathus extends Item {
    public SurpriseCompsognathus() {
        super("Compsognathus surpresa");
    }

    // Apresenta o efeito surpresa que esta versão ainda mantém como mensagem.
    @Override
    public String applyTo(Player player) {
        return "Um Compsognathus surpresa apareceu! Combate sera implementado em uma proxima etapa.";
    }

    @Override
    public Item copy() {
        return new SurpriseCompsognathus();
    }
}
