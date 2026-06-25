package game;

import java.util.Objects;

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
}
