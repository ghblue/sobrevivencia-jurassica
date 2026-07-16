package jogo.resultado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jogo.enums.GameStatus;

public class ResultadoAcao {
    private final TipoResultadoAcao tipo;
    private final GameStatus estadoPartida;
    private final boolean exibirEstado;
    private final boolean exibirStatusJogador;
    private final List<String> mensagens;

    public ResultadoAcao(
            TipoResultadoAcao tipo,
            GameStatus estadoPartida,
            boolean exibirEstado,
            boolean exibirStatusJogador,
            List<String> mensagens
    ) {
        this.tipo = tipo;
        this.estadoPartida = estadoPartida;
        this.exibirEstado = exibirEstado;
        this.exibirStatusJogador = exibirStatusJogador;
        this.mensagens = Collections.unmodifiableList(new ArrayList<>(mensagens));
    }

    public TipoResultadoAcao getTipo() {
        return tipo;
    }

    public GameStatus getEstadoPartida() {
        return estadoPartida;
    }

    public boolean deveExibirEstado() {
        return exibirEstado;
    }

    public boolean deveExibirStatusJogador() {
        return exibirStatusJogador;
    }

    public List<String> getMensagens() {
        return mensagens;
    }
}
