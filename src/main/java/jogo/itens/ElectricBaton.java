package jogo.itens;

import jogo.modelo.Player;

/**
 * Item que libera o bastão elétrico como opção permanente de ataque.
 */
public class ElectricBaton extends Item {
    private static final int RESULTADO_CRITICO = 6;
    private static final int RESULTADO_FALHA = 1;
    private static final int DANO_NORMAL = 1;
    private static final int DANO_CRITICO = 2;
    private static final int SEM_DANO = 0;

    public ElectricBaton() {
        super("Bastao eletrico");
    }

    // Calcula o dano do bastão a partir do resultado do dado de ataque.
    public int calcularDano(int resultadoDado) {
        if (resultadoDado == RESULTADO_CRITICO) {
            return DANO_CRITICO;
        }

        if (resultadoDado == RESULTADO_FALHA) {
            return SEM_DANO;
        }

        return DANO_NORMAL;
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
