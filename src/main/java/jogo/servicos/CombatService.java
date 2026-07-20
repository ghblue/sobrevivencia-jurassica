package jogo.servicos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jogo.dinossauros.Dinosaur;
import jogo.enums.CombatResult;
import jogo.interfaceusuario.InterfaceUsuario;
import jogo.modelo.Player;
import jogo.util.Dice;

/**
 * Coordena o combate em turnos entre o jogador e um dinossauro.
 * Processa a escolha de ataque, os dados, a esquiva e o resultado final.
 */
public class CombatService {
    private final InterfaceUsuario interfaceUsuario;
    private final Dice dice;

    public CombatService(InterfaceUsuario interfaceUsuario, Dice dice) {
        this.interfaceUsuario = Objects.requireNonNull(interfaceUsuario, "A interface de usuario e obrigatoria.");
        this.dice = Objects.requireNonNull(dice, "O dado e obrigatorio.");
    }

    public CombatResult startCombat(Player player, Dinosaur dinosaur) {
        return startCombat(player, dinosaur, false);
    }

    // Executa rodadas alternadas até vitória, derrota ou fuga do jogador.
    public CombatResult startCombat(Player player, Dinosaur dinosaur, boolean dinosaurStarts) {
        Objects.requireNonNull(player, "O jogador e obrigatorio.");
        Objects.requireNonNull(dinosaur, "O dinossauro e obrigatorio.");
        List<String> contextoInicial = new ArrayList<>();

        if (dinosaurStarts) {
            contextoInicial.add(dinosaur.getName() + " encontrou o jogador durante a movimentacao.");
            contextoInicial.add("Voce foi surpreendido pelo " + dinosaur.getName() + ".");
            contextoInicial.add("Um dinossauro iniciou o combate.");
        } else {
            contextoInicial.add("Voce encontrou um dinossauro.");
        }

        contextoInicial.add("Dinossauro encontrado: " + dinosaur.getName());

        if (dinosaurStarts) {
            List<String> mensagensAtaqueInicial = new ArrayList<>();
            mensagensAtaqueInicial.addAll(contextoInicial);
            dinosaurAttack(
                    player,
                    dinosaur,
                    mensagensAtaqueInicial,
                    "desviar do ataque inicial do " + dinosaur.getName()
            );
            addHealthStatus(player, dinosaur, mensagensAtaqueInicial);
            mostrarResumoCombate(mensagensAtaqueInicial);
            contextoInicial.clear();

            if (!player.isAlive()) {
                return CombatResult.PLAYER_DEFEATED;
            }
        }

        // Cada rodada começa com a ação do jogador e, se o inimigo sobreviver, termina com o contra-ataque.
        while (player.isAlive() && dinosaur.isAlive()) {
            List<String> mensagensRodada = new ArrayList<>();
            int damage = choosePlayerAction(player, dinosaur, contextoInicial, mensagensRodada);
            contextoInicial.clear();

            if (damage < 0) {
                interfaceUsuario.mostrarMensagem("Voce fugiu do combate.");
                return CombatResult.FLED;
            }

            if (damage > 0) {
                dinosaur.takeDamage(damage);
                mensagensRodada.add("Voce causou " + damage + " pontos de dano.");
            } else {
                mensagensRodada.add("Voce nao causou dano.");
            }

            if (!dinosaur.isAlive()) {
                mensagensRodada.add("Voce derrotou o " + dinosaur.getName() + ".");
                mostrarResumoCombate(mensagensRodada);
                return CombatResult.PLAYER_WON;
            }

            mensagensRodada.add("O dinossauro sobreviveu.");
            dinosaurAttack(
                    player,
                    dinosaur,
                    mensagensRodada,
                    "desviar do ataque do " + dinosaur.getName()
            );
            addHealthStatus(player, dinosaur, mensagensRodada);
            mostrarResumoCombate(mensagensRodada);
        }

        return player.isAlive() ? CombatResult.PLAYER_WON : CombatResult.PLAYER_DEFEATED;
    }

    // Lê a ação do jogador e devolve o dano calculado ou o sinal de fuga.
    private int choosePlayerAction(
            Player player,
            Dinosaur dinosaur,
            List<String> contextoCombate,
            List<String> mensagensRodada
    ) {
        while (true) {
            List<String> opcoes = new ArrayList<>();
            List<String> acoes = new ArrayList<>();

            opcoes.add("Atacar com as maos");
            acoes.add("MAOS");

            if (player.hasElectricBaton()) {
                opcoes.add("Atacar com bastao eletrico");
                acoes.add("BASTAO");
            }

            if (player.getTranquilizerAmmo() > 0) {
                opcoes.add("Atacar com dardos");
                acoes.add("DARDOS");
            }

            opcoes.add("Fugir");
            acoes.add("FUGIR");

            int option = interfaceUsuario.solicitarOpcao(
                    montarTituloAcao(player, dinosaur, contextoCombate),
                    opcoes.toArray(new String[0])
            );

            if (option <= 0) {
                interfaceUsuario.mostrarMensagem("Opcao cancelada. Voce fugiu do combate.");
                return -1;
            }

            if (option > acoes.size()) {
                interfaceUsuario.mostrarMensagem("Opcao invalida.");
                continue;
            }

            switch (acoes.get(option - 1)) {
                case "MAOS":
                    return attackWithHands(dinosaur, mensagensRodada);
                case "BASTAO":
                    return attackWithElectricBaton(mensagensRodada);
                case "DARDOS":
                    return attackWithTranquilizerGun(player, dinosaur, mensagensRodada);
                case "FUGIR":
                    return -1;
                default:
                    interfaceUsuario.mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private String montarTituloAcao(Player player, Dinosaur dinosaur, List<String> contextoCombate) {
        StringBuilder titulo = new StringBuilder();

        for (String mensagem : contextoCombate) {
            titulo.append(mensagem).append(System.lineSeparator());
        }

        if (!contextoCombate.isEmpty()) {
            titulo.append(System.lineSeparator());
        }

        titulo.append(String.format(
                "Combate contra %s%nSaude do jogador: %d/%d%nSaude do dinossauro: %d%nEscolha sua acao:",
                dinosaur.getName(),
                player.getHealth(),
                player.getMaxHealth(),
                dinosaur.getHealth()
        ));
        return titulo.toString();
    }

    private void mostrarResumoCombate(List<String> mensagens) {
        if (mensagens.isEmpty()) {
            return;
        }

        StringBuilder resumo = new StringBuilder();

        for (String mensagem : mensagens) {
            if (resumo.length() > 0) {
                resumo.append(System.lineSeparator());
            }

            resumo.append(mensagem);
        }

        interfaceUsuario.mostrarMensagem(resumo.toString());
    }

    private void addHealthStatus(Player player, Dinosaur dinosaur, List<String> mensagens) {
        mensagens.add(String.format(
                "Saude do jogador: %d/%d%nSaude do dinossauro: %d",
                player.getHealth(),
                player.getMaxHealth(),
                dinosaur.getHealth()
        ));
    }

    // Calcula falha, dano comum ou crítico do ataque desarmado.
    private int attackWithHands(Dinosaur dinosaur, List<String> mensagens) {
        int roll = dice.rollD6();
        mensagens.add("Rolou o dado de ataque com as maos e tirou " + roll + ".");

        if (!dinosaur.canTakeUnarmedDamage()) {
            mensagens.add("O ataque com as maos nao surtiu efeito contra o T-Rex.");
            return 0;
        }

        // O valor do dado define falha, dano normal ou dano crítico do ataque.
        if (roll == 6) {
            return 2;
        }

        if (roll == 1 || roll == 2) {
            return 0;
        }

        return 1;
    }

    // Calcula o dano do bastão elétrico de acordo com o dado de ataque.
    private int attackWithElectricBaton(List<String> mensagens) {
        int roll = dice.rollD6();
        mensagens.add("Rolou o dado de ataque com o bastao eletrico e tirou " + roll + ".");

        if (roll > 5) {
            return 2;
        }

        if (roll == 1) {
            return 0;
        }

        return 1;
    }

    // Consome o dardo e aplica a regra de esquiva especial do Velociraptor.
    private int attackWithTranquilizerGun(Player player, Dinosaur dinosaur, List<String> mensagens) {
        player.useTranquilizerAmmo();
        mensagens.add("Voce gastou 1 municao de dardo.");

        if (!dinosaur.canBeHitByTranquilizer()) {
            mensagens.add("O Velociraptor desviou do dardo.");
            return 0;
        }

        return 2;
    }

    // Resolve o turno inimigo usando percepção para decidir a esquiva.
    private void dinosaurAttack(Player player, Dinosaur dinosaur, List<String> mensagens, String motivo) {
        int roll = dice.rollD3();
        mensagens.add("Rolou o teste de percepcao para " + motivo + " e tirou " + roll + ".");

        // A percepção define se o jogador consegue desviar do ataque inimigo.
        if (roll <= player.getPerception()) {
            mensagens.add("Voce desviou do ataque.");
            return;
        }

        int damage = dinosaur.getAttackDamage();
        player.takeDamage(damage);
        mensagens.add(dinosaur.getName() + " causou " + damage + " de dano.");

        if (!player.isAlive()) {
            mensagens.add("Voce foi derrotado.");
        }
    }

}
