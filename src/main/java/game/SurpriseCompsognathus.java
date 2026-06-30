package game;

public class SurpriseCompsognathus extends Item {
    public SurpriseCompsognathus() {
        super("Compsognathus surpresa");
    }

    @Override
    public String applyTo(Player player) {
        return "Um Compsognathus surpresa apareceu! Combate sera implementado em uma proxima etapa.";
    }

    @Override
    public Item copy() {
        return new SurpriseCompsognathus();
    }
}
