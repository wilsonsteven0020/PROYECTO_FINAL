package test;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class NationalityTest {

    /**
     * Should calculate Cameroon power damage and apply electrocuted effect on rival impact
     */
    @Test
    public void shouldCalculateCameroonPowerDamageAndApplyElectrocutedEffect() throws DopoSoccerException {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.givePoints(50000);
        p2.givePoints(50000);

        p1.setNationality(new Cameroon());
        for(int i = 0; i < 4; i++){
            p1.upgradeStat("Kick");
        }

        p2.setNationality(new SouthKorea());
        p2.upgradeStat("Survival");
        p2.upgradeStat("Survival");

        Match match = new Match(p1, p2, true, 5, 1024, 576);
        assertEquals(84.0, p1.getNationality().calculatePowerDamage(p1), 0.01);

        Ball ball = match.getBalls().get(0);
        p1.getNationality().executePowerShot(ball, p1, match);
        ball.setPosX(p2.getPosX());
        ball.setPosY(p2.getPosY());

        match.updateEnvironment(0.01);

        assertEquals(36.0, p2.getCurrentHealth(), 0.01);
        assertFalse(p2.canJump());
        assertTrue(p2.canMove());
        assertTrue(p2.canKick());
        assertEquals(130.0, p2.getEffectiveSpeed(), 0.01);
    }

    /**
     * Should verify South Korea's power shot travels horizontally without gravity and pushes the rival toward their own goal
     */
    @Test
    public void shouldExecuteSouthKoreaHorizontalShotAndPushRivalTowardOwnGoal() {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.setNationality(new SouthKorea());
        p2.setNationality(new Nigeria());
        Match match = new Match(p1, p2, true, 5, 1024, 576);

        Ball ball = match.getBalls().get(0);
        p1.getNationality().executePowerShot(ball, p1, match);

        assertTrue(ball.isStraightHorizontalShot());
        assertEquals("Corea del Sur", ball.getSkinName());
        assertEquals(0.0, ball.getVelY(), 0.01);
        assertTrue(ball.getVelX() > 0);

        StatusEffect push = p1.getNationality().createStatusEffect();
        p2.applyStatusEffect(push);
        assertFalse(p2.canMove());
        assertTrue(p2.getVelX() > 0);

        p2.moveHorizontal(-1);
        assertTrue(p2.getVelX() > 0);
    }

    /**
     * Should verify Nigeria's Tornado power shot launches the rival into the air and paralyzes them for 3 seconds
     */
    @Test
    public void shouldLaunchAndParalyzeRivalWithNigeriaUnconsciousEffect() {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.setNationality(new Nigeria());
        p2.setNationality(new SouthKorea());
        p2.resetForNewMatch(600, 400, 90, 90, false);

        StatusEffect unconscious = p1.getNationality().createStatusEffect();
        p2.applyStatusEffect(unconscious);

        assertTrue(p2.getVelY() < 0);
        assertTrue(p2.isParalyzed());
        assertFalse(p2.canMove());
        assertFalse(p2.canJump());
        assertFalse(p2.canKick());
    }

    /**
     * Should verify United States' Illusion power shot splits the ball into 8 balls (1 real and 7 fake)
     */
    @Test
    public void shouldSplitIntoEightBallsWhenUnitedStatesExecutesPowerShot() {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.setNationality(new UnitedStates());
        p2.setNationality(new SouthKorea());
        Match match = new Match(p1, p2, true, 5, 1024, 576);

        Ball ball = match.getBalls().get(0);
        p1.getNationality().executePowerShot(ball, p1, match);

        assertEquals(8, match.getBalls().size());

        int realCount = 0;
        int fakeCount = 0;
        for(Ball b : match.getBalls()){
            if(b.isFake()){
                fakeCount++;
                assertEquals("Estados Unidos", b.getSkinName());
            }
            else{
                realCount++;
                assertEquals("Normal", b.getSkinName());
            }
        }
        assertEquals(1, realCount);
        assertEquals(7, fakeCount);
    }

    /**
     * Should execute Nigeria tornado power shot and apply unconscious effect on rival impact
     */
    @Test
    public void shouldExecuteNigeriaPowerShotAndApplyUnconsciousOnImpact() {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.setNationality(new Nigeria());
        p2.setNationality(new SouthKorea());
        Match match = new Match(p1, p2, true, 5, 1024, 576);

        Ball ball = match.getBalls().get(0);
        p1.getNationality().executePowerShot(ball, p1, match);
        assertTrue(ball.hasActivePower());
        assertTrue(ball.getVelX() > 0);

        ball.setPosX(p2.getPosX());
        ball.setPosY(p2.getPosY());
        match.updateEnvironment(0.01);

        assertEquals(p2.getMaxHealth() - p1.getNationality().calculatePowerDamage(p1), p2.getCurrentHealth(), 0.01);
        assertTrue(p2.isParalyzed());
        assertFalse(p2.canMove());
        assertFalse(p2.canJump());
        assertFalse(p2.canKick());
        assertEquals("Paralizado", p2.getSpriteState());
    }

    /**
     * Should apply United States confusion effect and expire after its duration
     */
    @Test
    public void shouldApplyAndExpireUnitedStatesConfusionEffect() {
        Player p1 = new Player(100, 400, 90, 90);
        Player p2 = new Player(600, 400, 90, 90);
        p1.setNationality(new UnitedStates());
        p2.setNationality(new SouthKorea());
        p2.resetForNewMatch(600, 400, 90, 90, false);

        StatusEffect confusion = p1.getNationality().createStatusEffect();
        assertNotNull(confusion);
        p2.applyStatusEffect(confusion);

        assertFalse(confusion.isExpired());
        assertTrue(p2.canMove());
        p2.moveHorizontal(1);

        p2.updateState(1.0, 0, 1000, 490, 100);
        assertFalse(confusion.isExpired());
        assertEquals(1.5, confusion.getRemainingDuration(), 0.01);

        p2.updateState(2.0, 0, 1000, 490, 100);
        assertTrue(confusion.isExpired());
        assertEquals(0.0, confusion.getRemainingDuration(), 0.01);
    }

    /**
     * Should update and expire push, electrocuted and unconscious status effects over time
     */
    @Test
    public void shouldUpdateAndExpireAllStatusEffectsOverTime() {
        Player target = new Player(500, 400, 90, 90);
        target.resetForNewMatch(500, 400, 90, 90, false);

        StatusEffect[] effects = {
            new SouthKorea().createStatusEffect(),
            new Cameroon().createStatusEffect(),
            new Nigeria().createStatusEffect()
        };

        for(StatusEffect effect : effects){
            target.applyStatusEffect(effect);
            assertFalse(effect.isExpired());
            target.updateState(0.5, 0, 1000, 490, 100);
            assertFalse(effect.isExpired());
            target.updateState(3.5, 0, 1000, 490, 100);
            assertTrue(effect.isExpired());
            assertTrue(target.canMove());
            assertTrue(target.canJump());
            assertTrue(target.canKick());
            assertFalse(target.isParalyzed());
        }
    }

    /**
     * Should expose status effect duration and permissions and push left player toward left goal
     */
    @Test
    public void shouldVerifyStatusEffectGettersAndDirectionalBehaviors() {
        Player leftPlayer = new Player(200, 400, 90, 90);
        leftPlayer.resetForNewMatch(200, 400, 90, 90, true);

        StatusEffect confusion = new UnitedStates().createStatusEffect();
        assertEquals(2.5, confusion.getRemainingDuration(), 0.01);
        assertTrue(confusion.allowsMove());
        assertTrue(confusion.allowsJump());
        assertTrue(confusion.allowsKick());
        assertEquals(1.0, confusion.getSpeedMultiplier(), 0.01);

        leftPlayer.applyStatusEffect(confusion);
        leftPlayer.updateState(1.0, 0, 1000, 490, 100);
        assertEquals(1.5, confusion.getRemainingDuration(), 0.01);

        // PushEffect on left player (facingRight == true) pushes to the left
        StatusEffect push = new SouthKorea().createStatusEffect();
        leftPlayer.applyStatusEffect(push);
        assertFalse(leftPlayer.canMove());
        assertTrue(leftPlayer.getVelX() < 0);

        // ElectrocutedEffect reduces speed when moving
        StatusEffect electrocuted = new Cameroon().createStatusEffect();
        leftPlayer.applyStatusEffect(electrocuted);
        leftPlayer.moveHorizontal(1);
        assertTrue(leftPlayer.getVelX() > 0);
        assertEquals(0.5, electrocuted.getSpeedMultiplier(), 0.01);

        // Nationality metadata and delegated ball skins
        Nationality korea = new SouthKorea();
        assertEquals("Aura Azul", korea.getPowerName());
        assertEquals(40.0, korea.getBasePowerDamage(), 0.01);
        assertEquals("Corea del Sur", korea.getBallSkinName());
        assertEquals("Normal", new UnitedStates().getBallSkinName());
    }
}