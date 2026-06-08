# Sobrevivencia Jurassica

Projeto Java de Programacao Orientada a Objetos.

Neste primeiro passo, o jogo possui apenas uma estrutura inicial em console:

- classe principal `Main`;
- classe `Game`;
- mensagem de boas-vindas;
- menu com as opcoes `Jogar` e `Sair`.

Ainda nao foram implementados tabuleiro, dinossauros, combate, itens ou interface grafica.

## Estrutura do projeto

```text
sobrevivencia-jurassica/
├── src/
│   └── main/
│       └── java/
│           └── game/
├── README.md
├── .gitignore
└── docs/
    └── relatorio.md
```

## Como executar

Compile o projeto a partir da raiz:

```bash
javac -d out src/main/java/game/*.java
```

Execute a classe principal:

```bash
java -cp out game.Main
```
