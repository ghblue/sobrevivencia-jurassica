# Sobrevivencia Jurassica

Projeto Java de Programacao Orientada a Objetos.

O jogo funciona no console e possui tabuleiro, movimentacao, visao limitada,
dinossauros, itens, caixas de suprimentos e combate por turnos.

A interface grafica ainda nao faz parte desta versao.

## Estrutura do projeto

```text
sobrevivencia-jurassica/
├── src/
│   └── main/
│       └── java/
│           └── jogo/
│               ├── dinossauros/
│               ├── enums/
│               ├── itens/
│               ├── modelo/
│               ├── servicos/
│               └── util/
├── README.md
├── .gitignore
└── docs/
    └── decimo-quarto-passo.md
```

## Como executar

Compile o projeto a partir da raiz:

```bash
mkdir -p out
find src/main/java -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d out
```

Execute a classe principal:

```bash
java -cp out jogo.Main
```
