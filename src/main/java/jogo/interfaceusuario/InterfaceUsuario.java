package jogo.interfaceusuario;

public interface InterfaceUsuario {
    // Define como o jogo envia mensagens sem depender diretamente do console.
    void mostrarMensagem(String mensagem);

    String solicitarEntrada(String mensagem);

    int solicitarOpcao(String titulo, String[] opcoes);

    boolean solicitarConfirmacao(String mensagem);
}
