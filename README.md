# Sobrevivência Jurássica

Jogo desenvolvido em Java para a disciplina de Programação Orientada a Objetos.

O projeto possui uma versão gráfica em Swing e uma versão de console mantida no
código.

## Sobre o jogo

Em Sobrevivência Jurássica, o jogador explora um parque ocupado por dinossauros.
O objetivo é sobreviver, coletar equipamentos e derrotar as ameaças espalhadas
pelo mapa.

O tabuleiro possui paredes, caixas de suprimentos e diferentes tipos de
dinossauros. Cada espécie possui características próprias, como resistência,
dano, movimentação ou restrição contra certos ataques.

A vitória acontece quando todos os dinossauros ativos foram derrotados e não há
mais ameaça escondida em caixa de suprimentos.

## Funcionalidades

- Menu inicial na interface gráfica.
- Escolha de dificuldade.
- Geração aleatória de mapa, paredes, caixas e dinossauros.
- Movimentação do jogador por botões.
- Movimentação do jogador pelas teclas W, A, S e D.
- Visão limitada do tabuleiro.
- Modo DEBUG para exibir o mapa completo.
- Caixas de suprimentos.
- Inventário com kit médico, bastão elétrico e munições de dardo.
- Uso de kit médico.
- Combate por turnos.
- Ataque com as mãos.
- Ataque com bastão elétrico.
- Ataque com dardo tranquilizante.
- Compsognato surpresa em caixa de suprimentos.
- Movimentação dos dinossauros por turno.
- Vitória, derrota, novo jogo e reinício da partida.
- Interface gráfica em Java Swing.
- Versão console funcional.

## Conceitos de POO utilizados

- Encapsulamento: classes como `Player`, `Dinosaur`, `Board` e `Cell` mantêm seus
  dados internos privados e expõem métodos para alterar o estado com validação.
- Herança: `Dinosaur` é a classe base dos dinossauros. `MovableDinosaur` agrupa
  os dinossauros que podem se mover.
- Polimorfismo: `TRex` e `Velociraptor` sobrescrevem regras de ataque; cada
  dinossauro implementa seu próprio `copy()`.
- Composição: `Board` é formado por uma matriz de `Cell`; `SupplyBox` possui um
  `Item` como conteúdo.
- Interface: `Movable` é implementada por `Player` e por `MovableDinosaur`,
  permitindo mover apenas entidades que realmente podem mudar de posição.

## Organização do projeto

```text
src/main/java/
└── jogo/
    ├── dinossauros/
    ├── enums/
    ├── interfacegrafica/
    │   └── recursos/
    ├── interfaceusuario/
    │   └── console/
    ├── itens/
    ├── modelo/
    ├── resultado/
    ├── servicos/
    └── util/

src/main/resources/
└── imagens/
```

Classes principais:

- `jogo.interfacegrafica.PrincipalGrafico`: inicia a versão gráfica.
- `jogo.Main`: inicia a versão console.
- `jogo.Game`: controla a partida e integra tabuleiro, jogador, itens e combate.

## Requisitos

- Java Development Kit instalado.
- Visual Studio Code, se quiser executar pela IDE.
- Extension Pack for Java no VS Code, caso use o botão `Run Java`.

## Como executar

### Pelo Visual Studio Code

1. Abra a pasta do projeto no VS Code.
2. Abra o arquivo:

```text
src/main/java/jogo/interfacegrafica/PrincipalGrafico.java
```

3. Clique em `Run Java`.

### Pelo terminal

Na raiz do projeto, compile os arquivos Java:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src/main/java -name "*.java")
```

Execute a versão gráfica:

```bash
java -cp out:src/main/resources jogo.interfacegrafica.PrincipalGrafico
```

Execute a versão console:

```bash
java -cp out:src/main/resources jogo.Main
```

## Como jogar

1. Escolha uma dificuldade no início da partida.
2. Mova o jogador pelos botões ou pelas teclas W, A, S e D.
3. Explore o mapa respeitando paredes e posições ocultas.
4. Colete caixas de suprimentos para obter itens.
5. Use kit médico quando precisar recuperar saúde.
6. Enfrente dinossauros com mãos, bastão elétrico ou dardos.
7. Use o modo DEBUG para visualizar o mapa completo.
8. Derrote todos os inimigos para vencer.

## Legenda

A versão gráfica usa imagens quando elas estão disponíveis, mas os símbolos
continuam importantes na versão console e como alternativa visual.

```text
P — Jogador
# — Parede
C — Compsognato
T — Troodonte
V — Velociraptor
R — Tiranossauro Rex
X — Caixa de suprimentos
? — Posição não visível
. — Espaço vazio
```

## Recursos gráficos

As imagens ficam em:

```text
src/main/resources/imagens/
```

Arquivos usados pela interface gráfica incluem `logo.png`, `jogador.png`,
`parede.png`, `chao.png`, `compsognato.png`, `troodonte.png`,
`velociraptor.png`, `tiranossauro_rex.png`, `caixa_suprimentos.png`,
`desconhecido.png`, `kit_medico.png`, `bastao_eletrico.png` e
`arma_dardos.png`.

Se uma imagem não for encontrada, a interface mantém o símbolo correspondente
como alternativa.

## Status do projeto

O jogo possui uma versão funcional com interface gráfica e uma versão console.
A movimentação dos inimigos é baseada em turnos.

O projeto não possui salvamento de partida nem uso de threads.
