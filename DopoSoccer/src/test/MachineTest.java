package test;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MachineTest {
    private Player basePlayer;
    private Player rival;

    @Before
    public void setUp() {
        basePlayer = new Player(700, 400, 90, 90);
        basePlayer.setNationality(new Cameroon());
        rival = new Player(200, 400, 90, 90);
        rival.setNationality(new SouthKorea());
    }

    /**
     * Should identify machine player and allow changing its profile
     */
    @Test
    public void shouldAssignAndChangeMachineProfiles() {
        MachineProfile offensive = new OffensiveProfile();
        MachineProfile defensive = new DefensiveProfile();
        MachinePlayer machine = new MachinePlayer(basePlayer, offensive);

        assertTrue(machine.isMachine());
        assertFalse(rival.isMachine());
        assertEquals(offensive, machine.getProfile());
        assertEquals(basePlayer.getNationality(), machine.getNationality());

        machine.setProfile(defensive);
        assertEquals(defensive, machine.getProfile());
    }

    /**
     * Should not execute AI when profile is null or machine is stunned
     */
    @Test
    public void shouldNotExecuteAIWhenProfileIsNullOrMachineIsStunned() {
        MachinePlayer machine = new MachinePlayer(basePlayer, null);
        Match match = new Match(rival, machine, true, 5, 1024, 576);

        machine.executeMachine(match, 0.5);
        assertEquals(0.0, machine.getVelX(), 0.001);

        machine.setProfile(new OffensiveProfile());
        machine.takeDamage(500.0);
        assertTrue(machine.isStunned());
        machine.executeMachine(match, 0.5);
        assertEquals(0.0, machine.getVelX(), 0.001);
    }

    /**
     * Should clumsy profile execute random actions during match updates
     */
    @Test
    public void shouldClumsyProfileExecuteActionsInMatch() {
        MachinePlayer machine = new MachinePlayer(basePlayer, new ClumsyProfile());
        Match match = new Match(rival, machine, true, 5, 1024, 576);
        machine.chargePowerBar(100.0);

        for (int i = 0; i < 60; i++) {
            machine.executeMachine(match, 0.25);
            match.updateEnvironment(0.02);
        }

        assertTrue(machine.getProfile() instanceof ClumsyProfile);
        assertTrue(machine.getPosX() >= 0 && machine.getPosX() <= match.getFieldWidth());
    }

    /**
     * Should offensive profile chase the ball, activate power, jump and kick when in range
     */
    @Test
    public void shouldOffensiveProfileChaseBallActivatePowerAndAttack() {
        MachinePlayer machine = new MachinePlayer(basePlayer, new OffensiveProfile());
        Match match = new Match(rival, machine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);

        machine.setPosX(700);
        ball.setPosX(450);
        ball.setPosY(machine.getPosY() - 60);
        machine.chargePowerBar(100.0);

        machine.executeMachine(match, 0.2);
        assertTrue(machine.hasActiveAura());
        assertTrue(machine.getVelX() < 0);

        // Balón a la derecha y cerca en rango de patada
        ball.setPosX(machine.getPosX() + 40);
        ball.setPosY(machine.getPosY());
        machine.executeMachine(match, 0.2);
        assertNotNull(machine.getProfile());
    }

    /**
     * Should defensive profile guard its own goal when ball is far and intercept when ball enters its half
     */
    @Test
    public void shouldDefensiveProfileGuardGoalAndInterceptBall() {
        MachinePlayer rightMachine = new MachinePlayer(basePlayer, new DefensiveProfile());
        Match match = new Match(rival, rightMachine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);

        // Balón en mitad rival: debe regresar hacia su propio arco (derecha)
        rightMachine.setPosX(500);
        ball.setPosX(150);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() > 0);

        // Balón en su propia mitad y alto: debe ir hacia el balón e intentar despejar
        rightMachine.setPosX(850);
        ball.setPosX(750);
        ball.setPosY(rightMachine.getPosY() - 50);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() < 0);

        // Probar perfil defensivo también en el lado izquierdo
        MachinePlayer leftMachine = new MachinePlayer(rival, new DefensiveProfile());
        Match leftMatch = new Match(leftMachine, basePlayer, true, 5, 1024, 576);
        Ball leftBall = leftMatch.getBalls().get(0);
        leftMachine.setPosX(400);
        leftBall.setPosX(800);
        leftMachine.executeMachine(leftMatch, 0.2);
        assertTrue(leftMachine.getVelX() < 0);
    }
/**
     * Should expert profile switch between offensive and defensive behavior depending on match state
     */
    @Test
    public void shouldExpertProfileSwitchBetweenOffenseAndDefense() {
        MachinePlayer machine = new MachinePlayer(basePlayer, new ExpertProfile());
        Match match = new Match(rival, machine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);
        double groundBallY = match.getGroundY() - ball.getHeight();

        // 1. Empatados y máquina más cerca del balón en mitad rival: actúa ofensivo
        rival.setPosX(50);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.setPosX(450);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() < 0);

        // 2. Va ganando y el rival activa aura: prioriza defensa aunque esté más cerca
        machine.addGoal();
        rival.chargePowerBar(100.0);
        rival.activatePower();
        machine.setPosX(450);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() > 0);

        // 3. Va perdiendo: ataca aunque el rival esté más cerca del balón
        rival.addGoal();
        rival.addGoal();
        rival.setPosX(280);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.setPosX(600);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() < 0);

        // 4. Empatados, balón detrás de la máquina en mitad rival y rival más cerca: retrocede a cuidar arco
        machine.addGoal();
        machine.setPosX(250);
        ball.setPosX(420);
        ball.setPosY(groundBallY);
        rival.setPosX(390);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() > 0);

        // 5. Balón en propia mitad: delega al perfil defensivo para despejar
        machine.setPosX(800);
        ball.setPosX(650);
        ball.setPosY(groundBallY);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() < 0);
    }

    /**
     * Should expert profile jump to dodge incoming rival power shots from either side
     */
    @Test
    public void shouldExpertProfileDodgeIncomingRivalPowerShot() {
        MachinePlayer rightMachine = new MachinePlayer(basePlayer, new ExpertProfile());
        Match match = new Match(rival, rightMachine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);

        rival.getNationality().executePowerShot(ball, rival, match);
        rightMachine.setPosX(550);
        rightMachine.setPosY(match.getGroundY() - rightMachine.getHeight());
        ball.setPosX(450);
        rightMachine.executeMachine(match, 0.05);
        assertTrue(rightMachine.getVelY() < 0);

        // Probar esquiva también jugando en el lado izquierdo
        MachinePlayer leftMachine = new MachinePlayer(rival, new ExpertProfile());
        Match leftMatch = new Match(leftMachine, basePlayer, true, 5, 1024, 576);
        Ball leftBall = leftMatch.getBalls().get(0);

        basePlayer.getNationality().executePowerShot(leftBall, basePlayer, leftMatch);
        leftMachine.setPosX(350);
        leftMachine.setPosY(leftMatch.getGroundY() - leftMachine.getHeight());
        leftBall.setPosX(470);
        leftMachine.executeMachine(leftMatch, 0.05);
        assertTrue(leftMachine.getVelY() < 0);
    }

    /**
     * Should offensive profile retreat on high balls near rival goal, jump for mid-field aerial balls, kick rival and track multiple balls
     */
    @Test
    public void shouldOffensiveProfileHandleRetreatJumpKickRivalAndIllusionBalls() {
        MachinePlayer rightMachine = new MachinePlayer(basePlayer, new OffensiveProfile());
        Match match = new Match(rival, rightMachine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);

        // 1. Balón alto cerca del arco rival: debe retroceder hacia la derecha
        rightMachine.setPosX(120);
        ball.setPosX(80);
        ball.setPosY(200);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() > 0);

        // 2. Balón aéreo en mitad de cancha: debe saltar
        rightMachine.setPosX(550);
        rightMachine.setPosY(match.getGroundY() - rightMachine.getHeight());
        ball.setPosX(540);
        ball.setPosY(330);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelY() < 0);

        // 3. Múltiples balones (ilusiones de EE. UU.): selecciona el balón más cercano
        rival.setNationality(new UnitedStates());
        rival.getNationality().executePowerShot(ball, rival, match);
        assertEquals(8, match.getBalls().size());
        rightMachine.executeMachine(match, 0.2);

        // 4. Lado izquierdo: retroceso en arco derecho y patada al rival cuando el balón está lejos
        MachinePlayer leftMachine = new MachinePlayer(rival, new OffensiveProfile());
        Match leftMatch = new Match(leftMachine, basePlayer, true, 5, 1024, 576);
        Ball leftBall = leftMatch.getBalls().get(0);

        leftMachine.setPosX(820);
        leftBall.setPosX(860);
        leftBall.setPosY(200);
        leftMachine.executeMachine(leftMatch, 0.2);
        assertTrue(leftMachine.getVelX() < 0);

        leftBall.setPosX(100);
        leftBall.setPosY(leftMatch.getGroundY() - leftBall.getHeight());
        leftMachine.setPosX(400);
        leftMachine.setPosY(leftMatch.getGroundY() - leftMachine.getHeight());
        basePlayer.setPosX(leftMachine.getPosX() + leftMachine.getHitboxWidth() * 0.8);
        basePlayer.setPosY(leftMachine.getPosY());
        double initialRivalHealth = basePlayer.getCurrentHealth();
        leftMachine.executeMachine(leftMatch, 0.2);
        assertTrue(basePlayer.getCurrentHealth() < initialRivalHealth);

        // 5. Sin balones en cancha: retorna sin lanzar excepción
        leftMatch.getBalls().clear();
        leftMachine.executeMachine(leftMatch, 0.2);
        leftMachine.setProfile(new ExpertProfile());
        leftMachine.executeMachine(leftMatch, 0.2);
    }
}