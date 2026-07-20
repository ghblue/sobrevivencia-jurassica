package jogo.interfacegrafica;

import java.awt.Component;
import java.util.Objects;
import javax.swing.JOptionPane;
import jogo.interfaceusuario.InterfaceUsuario;

public class InterfaceGrafica implements InterfaceUsuario {
    private final Component componentePai;

    public InterfaceGrafica(Component componentePai) {
        this.componentePai = Objects.requireNonNull(componentePai, "O componente pai e obrigatorio.");
    }

    // Mostra mensagens da logica usando dialogos simples do Swing.
    @Override
    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(componentePai, mensagem == null ? "" : mensagem);
    }

    // Solicita texto quando alguma regra ainda depender de entrada livre.
    @Override
    public String solicitarEntrada(String mensagem) {
        String entrada = JOptionPane.showInputDialog(componentePai, mensagem);
        return entrada == null ? "" : entrada.trim();
    }

    // Apresenta escolhas graficas e devolve a opcao em formato compatível com o console.
    @Override
    public int solicitarOpcao(String titulo, String[] opcoes) {
        if (opcoes == null || opcoes.length == 0) {
            return 0;
        }

        int escolha = JOptionPane.showOptionDialog(
                componentePai,
                titulo,
                "Escolha",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        return escolha >= 0 ? escolha + 1 : 0;
    }

    // Solicita confirmacao para saidas e transicoes importantes.
    @Override
    public boolean solicitarConfirmacao(String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
                componentePai,
                mensagem,
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );

        return resposta == JOptionPane.YES_OPTION;
    }
}
