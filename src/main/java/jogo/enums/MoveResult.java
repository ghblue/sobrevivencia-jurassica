package jogo.enums;

/**
 * Informa o que aconteceu após uma tentativa de movimento do jogador.
 */
public enum MoveResult {
    SUCCESS,
    OUT_OF_BOUNDS,
    WALL,
    DINOSAUR,
    SUPPLY_BOX
}
