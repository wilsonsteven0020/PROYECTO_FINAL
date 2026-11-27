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
     * Should identify machine players and copy base player nationality
     */
    @Test
    public void shouldIdentifyMachinePlayerAndCopyBaseNationality() {
        MachinePlayer offensiveMachine = new MachinePlayer(basePlayer, new OffensiveProfile());
        MachinePlayer defensiveMachine = new MachinePlayer(basePlayer, new DefensiveProfile());

        assertTrue(offensiveMachine.isMachine());
        assertTrue(defensiveMachine.isMachine());
        assertFalse(rival.isMachine());
        assertEquals(basePlayer.getNationality(), offensiveMachine.getNationality());
        assertEquals(basePlayer.getNationality(), defensiveMachine.getNationality());
    }

    /**
     * Should not execute AI when profile is null or machine is stunned
     */
    @Test
    public void shouldNotExecuteAIWhenProfileIsNullOrMachineIsStunned() {
        MachinePlayer nullProfileMachine = new MachinePlayer(basePlayer, null);
        Match nullProfileMatch = new Match(rival, nullProfileMachine, true, 5, 1024, 576);

        nullProfileMachine.executeMachine(nullProfileMatch, 0.5);
        assertEquals(0.0, nullProfileMachine.getVelX(), 0.001);

        MachinePlayer stunnedMachine = new MachinePlayer(basePlayer, new OffensiveProfile());
        Match stunnedMatch = new Match(rival, stunnedMachine, true, 5, 1024, 576);
        stunnedMachine.takeDamage(500.0);
        assertTrue(stunnedMachine.isStunned());
        stunnedMachine.executeMachine(stunnedMatch, 0.5);
        assertEquals(0.0, stunnedMachine.getVelX(), 0.001);
    }

    /**
     * Should clumsy profile execute random actions during match updates
     */
    @Test
    public void shouldClumsyProfileExecuteActionsInMatch() {
        MachinePlayer machine = new MachinePlayer(basePlayer, new ClumsyProfile());
        Match match = new Match(rival, machine, true, 5, 1024, 576);
        machine.chargePowerBar(100.0);

        for(int i = 0; i < 60; i++){
            machine.executeMachine(match, 0.25);
            match.updateEnvironment(0.02);
        }

        assertTrue(machine.isMachine());
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

        // Ball close in kick range while machine has aura active triggers power shot
        ball.setPosX(machine.getPosX() + 40);
        ball.setPosY(machine.getPosY());
        machine.executeMachine(match, 0.2);
        assertFalse(machine.hasActiveAura());
        assertTrue(ball.hasActivePower());
    }

    /**
     * Should defensive profile guard its own goal when ball is far and intercept when ball enters its half
     */
    @Test
    public void shouldDefensiveProfileGuardGoalAndInterceptBall() {
        MachinePlayer rightMachine = new MachinePlayer(basePlayer, new DefensiveProfile());
        Match match = new Match(rival, rightMachine, true, 5, 1024, 576);
        Ball ball = match.getBalls().get(0);

        // Ball in rival half: return toward own goal (right)
        rightMachine.setPosX(500);
        ball.setPosX(150);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() > 0);

        // Ball in own half and high: move toward ball and attempt to clear
        rightMachine.setPosX(850);
        ball.setPosX(750);
        ball.setPosY(rightMachine.getPosY() - 50);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() < 0);

        // Also test defensive profile on the left side
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

        // 1. Tied and machine closer to ball in rival half: acts offensively
        rival.setPosX(50);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.setPosX(450);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() < 0);

        // 2. Leading and rival activates aura: prioritizes defense even if closer
        machine.addGoal();
        rival.chargePowerBar(100.0);
        rival.activatePower();
        machine.setPosX(450);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() > 0);

        // 3. Trailing: attacks even if rival is closer to the ball
        rival.addGoal();
        rival.addGoal();
        rival.setPosX(280);
        ball.setPosX(350);
        ball.setPosY(groundBallY);
        machine.setPosX(600);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() < 0);

        // 4. Tied, ball behind machine in rival half and rival closer: retreats to guard goal
        machine.addGoal();
        machine.setPosX(250);
        ball.setPosX(420);
        ball.setPosY(groundBallY);
        rival.setPosX(390);
        machine.executeMachine(match, 0.2);
        assertTrue(machine.getVelX() > 0);

        // 5. Ball in own half: delegates to defensive profile to clear
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

        // Also test dodging from the left side
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

        // 1. High ball near rival goal: should retreat to the right
        rightMachine.setPosX(120);
        ball.setPosX(80);
        ball.setPosY(200);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelX() > 0);

        // 2. Aerial ball in midfield: should jump
        rightMachine.setPosX(550);
        rightMachine.setPosY(match.getGroundY() - rightMachine.getHeight());
        ball.setPosX(540);
        ball.setPosY(330);
        rightMachine.executeMachine(match, 0.2);
        assertTrue(rightMachine.getVelY() < 0);

        // 3. Multiple balls (US illusions): selects closest ball
        rival.setNationality(new UnitedStates());
        rival.getNationality().executePowerShot(ball, rival, match);
        assertEquals(8, match.getBalls().size());
        rightMachine.executeMachine(match, 0.2);

        // 4. Left side: retreat near right goal and kick rival when ball is far
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

        // 5. No balls on field: profiles return safely without throwing exceptions
        leftMatch.getBalls().clear();
        leftMachine.executeMachine(leftMatch, 0.2);

        MachinePlayer expertMachine = new MachinePlayer(rival, new ExpertProfile());
        Match expertEmptyMatch = new Match(expertMachine, basePlayer, true, 5, 1024, 576);
        expertEmptyMatch.getBalls().clear();
        expertMachine.executeMachine(expertEmptyMatch, 0.2);

        MachinePlayer defensiveMachine = new MachinePlayer(rival, new DefensiveProfile());
        Match defensiveEmptyMatch = new Match(defensiveMachine, basePlayer, true, 5, 1024, 576);
        defensiveEmptyMatch.getBalls().clear();
        defensiveMachine.executeMachine(defensiveEmptyMatch, 0.2);
    }
}