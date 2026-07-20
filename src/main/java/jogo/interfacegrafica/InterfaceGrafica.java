package jogo.interfacegrafica;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jogo.interfaceusuario.InterfaceUsuario;

public class InterfaceGrafica implements InterfaceUsuario {
    private final Component componentePai;

    public InterfaceGrafica(Component componentePai) {
        this.componentePai = Objects.requireNonNull(componentePai, "O componente pai e obrigatorio.");
    }

    // Mostra mensagens da logica usando dialogos simples do Swing.
    @Override
    public void mostrarMensagem(String mensagem) {
        String texto = mensagem == null ? "" : mensagem;

        if (texto.contains(System.lineSeparator()) || texto.length() > 120) {
            mostrarMensagemEmCaixaDeTexto(texto);
            return;
        }

        JOptionPane.showMessageDialog(componentePai, texto);
    }

    private void mostrarMensagemEmCaixaDeTexto(String mensagem) {
        JTextArea areaTexto = new JTextArea(mensagem);
        areaTexto.setEditable(false);
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setCaretPosition(0);

        JScrollPane rolagem = new JScrollPane(areaTexto);
        rolagem.setPreferredSize(new Dimension(420, 220));
        JOptionPane.showMessageDialog(componentePai, rolagem, "Resumo", JOptionPane.INFORMATION_MESSAGE);
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
