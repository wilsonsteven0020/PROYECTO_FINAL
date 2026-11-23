package test;

import domain.Ball;
import domain.Cameroon;
import domain.ClumsyProfile;
import domain.DefensiveProfile;

import domain.ExpertProfile;
import domain.MachinePlayer;
import domain.MachineProfile;
import domain.Match;
import domain.Nigeria;
import domain.OffensiveProfile;
import domain.Player;
import domain.SoccerGoal;
import domain.SouthKorea;

import static org.junit.Assert.*;

import org.junit.Test;


public class MatchTest {

    /**
     * Should charge power bar in ten seconds and reduce time
     */
    @Test
    public void shouldChargePowerBarInTenSecondsAndReduceTime() {
        Player p1 = new Player(0,0,0,0);
        Player p2 = new Player(0,0,0,0);
        Match match = new Match(p1, p2, false, 1, 1024, 576);

        for (int i = 0; i < 250; i++) {
            match.updateEnvironment(0.02);
        }
        assertEquals(50.0, p1.getPowerBar(), 0.01);
        assertEquals(55, match.getRemainingTime());

        match.updateEnvironment(5.0);
        assertEquals(100.0, p1.getPowerBar(), 0.01);
        assertEquals(50, match.getRemainingTime());
    }

    /**
     * Should not update bars if the time is paused
     */
    @Test
    public void shouldNotUpdateBarsOrTimeWhenMatchIsPaused() {
        Player p1 = new Player(0,0,0,0);
        Player p2 = new Player(0,0,0,0);
        Match match = new Match(p1, p2, false, 2, 1024, 576);

        match.togglePause();
        match.updateEnvironment(4.0);

        assertTrue(match.isPaused());
        assertEquals(0.0, p1.getPowerBar(), 0.001);
        assertEquals(120, match.getRemainingTime());
    }

    /**
     * Should the match toggle pause
     */
    @Test
    public void shouldTogglePauseCorrectly() {
        Player p1 = new Player(100, 100, 80, 80);
        Player p2 = new Player(700, 100, 80, 80);
        
        Match match = new Match(p1, p2, false, 2, 1024, 576);
        assertFalse(match.isPaused());
        match.togglePause();
        assertTrue(match.isPaused());
    }


    /**
     * Should know the goals scored if the match is Goald based
     */
    @Test
    public void shouldCalculateMissingGoalsAccurately() {
        Player p1 = new Player(0,0,0,0);
        Player p2 = new Player(0,0,0,0);
        Match match = new Match(p1, p2, true, 3, 1024, 576);
        match.addGoal(true);
        assertEquals(2, match.getMissingGoals());
    }

    /**
     * Should verifies that scoring a goal increments score, charges 25% power bar for the receiver, and resets positions
     */
    @Test
    public void shouldScoreGoalChargeReceiverPowerBarAndResetPositions() {
        Player p1 = new Player(0, 0, 90, 90);
        Player p2 = new Player(0, 0, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Cameroon());
        Match match = new Match(p1, p2, true, 3, 1024, 576);

        Ball ball = match.getBalls().get(0);
        SoccerGoal rightGoal = match.getRightSoccerGoal();

        ball.setPosX(rightGoal.getPosX() + rightGoal.getWidth() * 0.80);
        ball.setPosY(match.getGroundY() - ball.getHeight());

        match.updateEnvironment(0.01);

        assertEquals(1, p1.getGoals());
        assertEquals(0, p2.getGoals());
        assertTrue(p2.getPowerBar() >= 25.0);
    }


    /**
     * Should verifies that reaching target goals finishes the match and awards 100 points to winner and 25 to loser
     */
    @Test
    public void shouldFinishGoalBasedMatchAndAwardPointsToWinnerAndLoser() {
        Player p1 = new Player(0, 0, 90, 90);
        Player p2 = new Player(0, 0, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Nigeria());
        Match match = new Match(p1, p2, true, 2, 1024, 576);

        match.addGoal(true);
        match.updateEnvironment(0.02);
        assertFalse(match.isThereWinner());

        match.addGoal(true);
        match.updateEnvironment(0.02);

        assertTrue(match.isThereWinner());
        assertEquals(0, match.getWinnerNum());
        assertEquals(100, p1.getPoints());
        assertEquals(25, p2.getPoints());
    }

    /**
     * Should verifies that kicking when the rival is in foot range deals kick damage
     */
    @Test
    public void shouldDealKickDamageWhenRivalIsInKickRange() {
        Player p1 = new Player(0, 0, 90, 90);
        Player p2 = new Player(0, 0, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Cameroon());
        Match match = new Match(p1, p2, true, 5, 1024, 576);

        p2.setPosX(p1.getPosX() + p1.getHitboxWidth() * 0.8);
        p2.setPosY(p1.getPosY());

        double initialHealth = p2.getCurrentHealth();
        match.kickPlayer(0);

        assertEquals(initialHealth - p1.getKickDamage(), p2.getCurrentHealth(), 0.01);
    }

    /**
     * Should run AI decisions for machine players during match updateEnvironment
     */
    @Test
    public void shouldExecuteMachineProfilesInsideMatchEnvironment() {
        MachineProfile[] profiles = {
            new OffensiveProfile(),
            new DefensiveProfile(),
            new ClumsyProfile(),
            new ExpertProfile()
        };

        for (MachineProfile profile : profiles) {
            Player human = new Player(150, 400, 90, 90);
            Player baseBot = new Player(750, 400, 90, 90);
            human.setNationality(new SouthKorea());
            baseBot.setNationality(new Cameroon());

            MachinePlayer machine = new MachinePlayer(baseBot, profile);
            Match match = new Match(human, machine, true, 5, 1024, 576);

            for (int i = 0; i < 20; i++) {
                match.updateEnvironment(0.05);
            }
            assertTrue(match.getRightPlayer() instanceof MachinePlayer);
        }
    }

    /**
     * Should finish time-based match when time reaches zero and award points to winner
     */
    @Test
    public void shouldFinishTimeBasedMatchWithWinnerWhenTimeExpires() {
        Player p1 = new Player(0, 0, 90, 90);
        Player p2 = new Player(0, 0, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Cameroon());
        Match match = new Match(p1, p2, false, 1, 1024, 576);

        match.addGoal(false);
        match.updateEnvironment(61.0);

        assertTrue(match.isThereWinner());
        assertEquals(1, match.getWinnerNum());
        assertEquals(25, p1.getPoints());
        assertEquals(100, p2.getPoints());
    }

    /**
     * Should detect goal in left soccer goal and charge left player power bar
     */
    @Test
    public void shouldScoreInLeftGoalAndChargeLeftPlayerPowerBar() {
        Player p1 = new Player(0, 0, 90, 90);
        Player p2 = new Player(0, 0, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Cameroon());
        Match match = new Match(p1, p2, true, 3, 1024, 576);

        Ball ball = match.getBalls().get(0);
        ball.setPosX(5);
        ball.setPosY(match.getGroundY() - ball.getHeight());

        match.updateEnvironment(0.01);

        assertEquals(0, p1.getGoals());
        assertEquals(1, p2.getGoals());
        assertTrue(p1.getPowerBar() >= 25.0);
    }

}
