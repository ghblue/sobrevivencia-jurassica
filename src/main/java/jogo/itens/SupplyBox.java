package jogo.itens;

import java.util.Objects;
import jogo.modelo.Position;

/**
 * Representa uma caixa posicionada no tabuleiro e o item guardado nela.
 * A caixa é removida depois que o jogador coleta e aplica seu conteúdo.
 */
public class SupplyBox {
    public static final String VISUAL_SYMBOL = "X";

    private final Position position;
    private final Item content;

    public SupplyBox(Position position, Item content) {
        this.position = Objects.requireNonNull(position, "A posicao da caixa e obrigatoria.");
        this.content = Objects.requireNonNull(content, "O conteudo da caixa e obrigatorio.");
    }

    public Position getPosition() {
        return position;
    }

    public Item getContent() {
        return content;
    }

    // Copia a caixa e seu item para preservar o estado inicial da partida.
    public SupplyBox copy() {
        return new SupplyBox(position, content.copy());
    }
}
