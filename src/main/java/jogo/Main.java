package jogo;

import jogo.interfaceusuario.console.InterfaceConsole;

/**
 * Ponto de entrada do Sobrevivência Jurássica.
 */
public class Main {
    // A versao console cria a interface de terminal e chama os metodos publicos do jogo.
    public static void main(String[] args) {
        InterfaceConsole interfaceConsole = new InterfaceConsole();
        Game game = new Game(interfaceConsole);
        interfaceConsole.iniciar(game);
    }
}
