package jogo.servicos;

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

        if (dinosaurStarts) {
            interfaceUsuario.mostrarMensagem(dinosaur.getName() + " encontrou o jogador durante a movimentacao.");
            interfaceUsuario.mostrarMensagem("Voce foi surpreendido pelo " + dinosaur.getName() + ".");
            interfaceUsuario.mostrarMensagem("Um dinossauro iniciou o combate!");
        } else {
            interfaceUsuario.mostrarMensagem("Voce encontrou um dinossauro!");
        }

        interfaceUsuario.mostrarMensagem("Dinossauro encontrado: " + dinosaur.getName());
        printHealthStatus(player, dinosaur);

        if (dinosaurStarts) {
            dinosaurAttack(player, dinosaur);
            printHealthStatus(player, dinosaur);

            if (!player.isAlive()) {
                return CombatResult.PLAYER_DEFEATED;
            }
        }

        // Cada rodada começa com a ação do jogador e, se o inimigo sobreviver, termina com o contra-ataque.
        while (player.isAlive() && dinosaur.isAlive()) {
            int damage = choosePlayerAction(player, dinosaur);

            if (damage < 0) {
                interfaceUsuario.mostrarMensagem("Voce fugiu do combate.");
                return CombatResult.FLED;
            }

            if (damage > 0) {
                dinosaur.takeDamage(damage);
                interfaceUsuario.mostrarMensagem("Voce causou " + damage + " de dano.");
            } else {
                interfaceUsuario.mostrarMensagem("Voce nao causou dano.");
            }

            if (!dinosaur.isAlive()) {
                interfaceUsuario.mostrarMensagem("Voce derrotou o " + dinosaur.getName() + ".");
                return CombatResult.PLAYER_WON;
            }

            dinosaurAttack(player, dinosaur);
            printHealthStatus(player, dinosaur);
        }

        return player.isAlive() ? CombatResult.PLAYER_WON : CombatResult.PLAYER_DEFEATED;
    }

    // Lê a ação do jogador e devolve o dano calculado ou o sinal de fuga.
    private int choosePlayerAction(Player player, Dinosaur dinosaur) {
        while (true) {
            showAttackMenu(player);
            String option = interfaceUsuario.solicitarEntrada("Opcao: ");

            switch (option) {
                case "1":
                    return attackWithHands(dinosaur);
                case "2":
                    if (!player.hasElectricBaton()) {
                        interfaceUsuario.mostrarMensagem("Opcao indisponivel: voce nao possui bastao eletrico.");
                        break;
                    }

                    return attackWithElectricBaton();
                case "3":
                    if (player.getTranquilizerAmmo() <= 0) {
                        interfaceUsuario.mostrarMensagem("Opcao indisponivel: voce nao possui municao de dardo.");
                        break;
                    }

                    return attackWithTranquilizerGun(player, dinosaur);
                case "0":
                    return -1;
                default:
                    interfaceUsuario.mostrarMensagem("Opcao invalida.");
                    break;
            }
        }
    }

    private void showAttackMenu(Player player) {
        interfaceUsuario.mostrarMensagem("Escolha sua acao:");
        interfaceUsuario.mostrarMensagem("1 - Atacar com as maos");

        if (player.hasElectricBaton()) {
            interfaceUsuario.mostrarMensagem("2 - Atacar com bastao eletrico");
        }

        if (player.getTranquilizerAmmo() > 0) {
            interfaceUsuario.mostrarMensagem("3 - Atacar com arma de dardos");
        }

        interfaceUsuario.mostrarMensagem("0 - Fugir");
    }

    // Calcula falha, dano comum ou crítico do ataque desarmado.
    private int attackWithHands(Dinosaur dinosaur) {
        int roll = dice.rollD6();
        interfaceUsuario.mostrarMensagem("Dado de ataque: " + roll);

        if (!dinosaur.canTakeUnarmedDamage()) {
            interfaceUsuario.mostrarMensagem("O ataque com as maos nao surtiu efeito contra o T-Rex.");
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
    private int attackWithElectricBaton() {
        int roll = dice.rollD6();
        interfaceUsuario.mostrarMensagem("Dado de ataque: " + roll);

        if (roll > 5) {
            return 2;
        }

        if (roll == 1) {
            return 0;
        }

        return 1;
    }

    // Consome o dardo e aplica a regra de esquiva especial do Velociraptor.
    private int attackWithTranquilizerGun(Player player, Dinosaur dinosaur) {
        player.useTranquilizerAmmo();
        interfaceUsuario.mostrarMensagem("Voce gastou 1 municao de dardo.");

        if (!dinosaur.canBeHitByTranquilizer()) {
            interfaceUsuario.mostrarMensagem("O Velociraptor desviou do dardo.");
            return 0;
        }

        return 2;
    }

    // Resolve o turno inimigo usando percepção para decidir a esquiva.
    private void dinosaurAttack(Player player, Dinosaur dinosaur) {
        int roll = dice.rollD3();
        interfaceUsuario.mostrarMensagem("Teste de percepcao: " + roll);

        // A percepção define se o jogador consegue desviar do ataque inimigo.
        if (roll <= player.getPerception()) {
            interfaceUsuario.mostrarMensagem("Voce desviou do ataque.");
            return;
        }

        int damage = dinosaur.getAttackDamage();
        player.takeDamage(damage);
        interfaceUsuario.mostrarMensagem(dinosaur.getName() + " causou " + damage + " de dano.");

        if (!player.isAlive()) {
            interfaceUsuario.mostrarMensagem("Voce foi derrotado.");
        }
    }

    private void printHealthStatus(Player player, Dinosaur dinosaur) {
        interfaceUsuario.mostrarMensagem(String.format(
                "Saude do jogador: %d/%d",
                player.getHealth(),
                player.getMaxHealth()
        ));
        interfaceUsuario.mostrarMensagem("Saude do dinossauro: " + dinosaur.getHealth());
    }
}
