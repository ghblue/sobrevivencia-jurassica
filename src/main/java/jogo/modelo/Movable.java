package jogo.modelo;

/**
 * Contrato comum para elementos que possuem posição e podem se mover.
 */
public interface Movable {
    Position getCurrentPosition();

    void moveTo(Position newPosition);
}
