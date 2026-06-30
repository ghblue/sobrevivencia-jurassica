package game;

public interface Movable {
    Position getCurrentPosition();

    void moveTo(Position newPosition);
}
