package game;

import java.util.Objects;

public abstract class Item {
    private final String name;

    protected Item(String name) {
        this.name = Objects.requireNonNull(name, "O nome do item e obrigatorio.");
    }

    public String getName() {
        return name;
    }

    public abstract String applyTo(Player player);

    public abstract Item copy();
}
