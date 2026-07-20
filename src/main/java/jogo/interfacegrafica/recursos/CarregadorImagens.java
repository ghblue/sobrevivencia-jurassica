package jogo.interfacegrafica.recursos;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;

public final class CarregadorImagens {
    private static final String PASTA_IMAGENS = "/imagens/";
    private static final String PASTA_RECURSOS_LOCAL = "src/main/resources/imagens/";
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();
    private static final Set<String> AUSENTES_REGISTRADAS = new HashSet<>();

    private CarregadorImagens() {
    }

    // Carrega uma imagem obrigatoria pelo classpath e registra fallback quando ela faltar.
    public static ImageIcon carregar(String nomeArquivo, int largura, int altura, String simboloFallback) {
        return carregar(nomeArquivo, largura, altura, simboloFallback, true);
    }

    // Carrega uma imagem opcional pelo classpath sem poluir o console quando ela faltar.
    public static ImageIcon carregarOpcional(String nomeArquivo, int largura, int altura) {
        return carregar(nomeArquivo, largura, altura, "", false);
    }

    private static ImageIcon carregar(
            String nomeArquivo,
            int largura,
            int altura,
            String simboloFallback,
            boolean registrarAusencia
    ) {
        if (nomeArquivo == null || nomeArquivo.isEmpty() || largura <= 0 || altura <= 0) {
            return null;
        }

        String chave = nomeArquivo + ":" + largura + "x" + altura;

        if (CACHE.containsKey(chave)) {
            return CACHE.get(chave);
        }

        URL recurso = localizarRecurso(nomeArquivo);

        if (recurso == null) {
            registrarImagemAusente(nomeArquivo, simboloFallback, registrarAusencia);
            CACHE.put(chave, null);
            return null;
        }

        ImageIcon imagemOriginal = new ImageIcon(recurso);
        Image imagemRedimensionada = imagemOriginal.getImage().getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH
        );
        ImageIcon imagemFinal = new ImageIcon(imagemRedimensionada);
        CACHE.put(chave, imagemFinal);
        return imagemFinal;
    }

    private static URL localizarRecurso(String nomeArquivo) {
        URL recurso = CarregadorImagens.class.getResource(PASTA_IMAGENS + nomeArquivo);

        if (recurso != null) {
            return recurso;
        }

        return localizarRecursoLocal(nomeArquivo);
    }

    private static URL localizarRecursoLocal(String nomeArquivo) {
        File arquivo = new File(PASTA_RECURSOS_LOCAL + nomeArquivo);

        if (!arquivo.exists()) {
            return null;
        }

        try {
            return arquivo.toURI().toURL();
        } catch (java.net.MalformedURLException exception) {
            return null;
        }
    }

    private static void registrarImagemAusente(
            String nomeArquivo,
            String simboloFallback,
            boolean registrarAusencia
    ) {
        if (!registrarAusencia || AUSENTES_REGISTRADAS.contains(nomeArquivo)) {
            return;
        }

        AUSENTES_REGISTRADAS.add(nomeArquivo);

        if (simboloFallback == null || simboloFallback.isEmpty()) {
            System.out.println("Imagem nao encontrada: " + nomeArquivo + ".");
            return;
        }

        System.out.println("Imagem nao encontrada: " + nomeArquivo + ". Usando simbolo " + simboloFallback + ".");
    }
}
