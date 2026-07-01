package jogo.itens;

import java.util.Objects;
import jogo.modelo.Player;

/**
 * Base dos conteúdos encontrados nas caixas de suprimentos.
 * Cada item sabe aplicar seu próprio efeito ao jogador e criar uma cópia.
 */
public abstract class Item {
    private final String name;

    protected Item(String name) {
        this.name = Objects.requireNonNull(name, "O nome do item e obrigatorio.");
    }

    public String getName() {
        return name;
    }

    // Aplica ao jogador o efeito definido pelo tipo concreto do item.
    public abstract String applyTo(Player player);

    public abstract Item copy();
}
