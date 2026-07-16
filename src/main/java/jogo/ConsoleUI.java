package jogo;

import java.util.Scanner;
import jogo.interfaceusuario.console.InterfaceConsole;

public class ConsoleUI extends InterfaceConsole {
    // Mantem compatibilidade com o nome anterior da interface de console.
    public ConsoleUI(Scanner scanner) {
        super(scanner);
    }
}
