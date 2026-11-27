package test;

import domain.Player;
import domain.PushEffect;
import domain.SouthKorea;
import domain.UnconsciousEffect;
import domain.DopoSoccerException;
import domain.ElectrocutedEffect;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
    private Player p;

    @Before
    public void setUp(){
        p = new Player(1, 0, 2, 2);
    }

    /**
     * Should stats level start by 1.
     */
    @Test
    public void shouldStatLevelsStartByOne(){
        assertEquals(1, p.getStatLevel("Speed"));
        assertEquals(1, p.getStatLevel("Jump"));
        assertEquals(1, p.getStatLevel("Kick"));
        assertEquals(1, p.getStatLevel("Survival"));
    }

    /**
     * Should player points be greater if you give him points
     */
    @Test
    public void shouldPlayerGetPoints(){
        p.givePoints(500);
        assertEquals(500, p.getPoints());
    }

    /**
     * Should stat be upgraded if you have enough points.
     */
    @Test
    public void shouldStatBeUpgradedWithEnoughPoints(){
        p.givePoints(500);
        try{
            p.upgradeStat("Speed");
            assertEquals(2, p.getStatLevel("Speed"));
            assertEquals(0, p.getPoints());
        }
        catch(DopoSoccerException e){
            fail("It threw an exception");
        }
    }

    /**
     * Should throw an exception if there's not enough points to buy an update.
     */
    @Test
    public void shouldThrowAnExceptionIfThereIsNoEnoughPointsForAnUpdate(){
        try{
            p.upgradeStat("Speed");
            fail("It didn't throw an exception");
        }
        catch(DopoSoccerException e){
            assertEquals(DopoSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
        }
    }

    /**
     * Should prices be greater (it should duplicate its price) by each upgrade
     */
    @Test
    public void shouldPricesBeGreateByEachUpgrade(){
        p.givePoints(500);
        try{
            assertEquals(500, p.getUpgradeCost("Speed"));
            p.upgradeStat("Speed");
            assertEquals(0, p.getPoints());
            p.givePoints(1000);
            assertEquals(1000, p.getUpgradeCost("Speed"));
            p.upgradeStat("Speed");
            assertEquals(0, p.getPoints());
            p.givePoints(2000);
            assertEquals(2000, p.getUpgradeCost("Speed"));
            p.upgradeStat("Speed");
            assertEquals(0, p.getPoints());
        }
        catch(DopoSoccerException e){
            fail("It threw an exception");
        }
    }

    /**
     * Should player start with zero goals
     */
    @Test
    public void shouldStartWithZeroGoals(){
        assertEquals(0, p.getGoals());
    }

    /**
     * Should maxHealth be bigger if survival is upgraded
     */
    @Test
    public void shouldMaxHealthBeBiggerIfSurvivalIsUpgraded(){
        assertEquals(100.0, p.getMaxHealth(), 0.001);
        p.givePoints(500);
        try{
            p.upgradeStat("Survival");
        }
        catch(DopoSoccerException e){
            fail("It threw an exception");
        }
        assertEquals(110.0, p.getMaxHealth(), 0.001);
    }

    /**
     * Should currentHealth be lower if I take damage.
     */
    @Test
    public void shouldCurrentHealthBeLowerIfITakeDamage(){
        double original = p.getCurrentHealth();
        p.takeDamage(10);
        assertEquals(original - 10.0, p.getCurrentHealth(), 0.001);
    }

    /**
     * Should current health never be lower than 0.
     */
    @Test
    public void shouldCurrentHealthAlwaysBeGreaterOrEqualsToZero(){
        p.takeDamage(10000000000000000000000.0);
        assertEquals(0, p.getCurrentHealth(), 0.001);
    }

    /**
     * Should power bar start by zero and allow charging it
     */
    @Test
    public void shouldPowerBarStartByZeroAndChargeIt(){
        assertEquals(0, p.getPowerBar(), 0.001);
        p.chargePowerBar(20);
        assertEquals(20.0, p.getPowerBar(), 0.001);
    }

    /**
     * Shouldn't power bar be greater than 100
     */
    @Test
    public void shouldNotPowerBarBeGreaterThanOneHundred(){
        p.chargePowerBar(10000000000.0);
        assertEquals(100.0, p.getPowerBar(), 0.001);
    }

    /**
     * Should player move horizontal
     */
    @Test
    public void shouldPlayerMoveHor(){
        p.setVelX(1);
        p.updatePosition(2);
        assertEquals(p.getPosX(), 3.0, 0.00001);
    }

    /**
     * Should player move vertical
     */
    @Test
    public void shouldPlayerMoveVer(){
        p.setVelY(1);
        p.updatePosition(2);
        assertEquals(p.getPosY(), 2.0, 0.00001);
    }

    /**
     * Should the players collide
     */
    @Test
    public void ShouldPlayerCollide(){
        Player p2 = new Player(2, 0, 2, 2);
        assertTrue(p2.collidesWith(p));
    }

    /**
     * Should power aura require 100% power bar, empty the bar, and block re-activation while active
     */
    @Test
    public void shouldActivateAuraOnlyWhenBarIsFullAndPreventDoubleActivation() {
        Player player = new Player(200, 400, 90, 90);
        player.resetForNewMatch(200, 400, 90, 90, true);

        player.chargePowerBar(50.0);
        player.activatePower();
        assertFalse(player.hasActiveAura());

        player.chargePowerBar(50.0);
        player.activatePower();
        assertTrue(player.hasActiveAura());
        assertEquals(0.0, player.getPowerBar(), 0.01);
        assertEquals("AuraNormal", player.getSpriteState());

        player.chargePowerBar(100.0);
        player.activatePower();
        assertEquals(100.0, player.getPowerBar(), 0.01);
    }

    /**
     * Should applying a new status effect replace the previous one without accumulating
     */
    @Test
    public void shouldReplacePreviousStatusEffectWhenNewEffectIsApplied() {
        Player player = new Player(200, 400, 90, 90);
        player.resetForNewMatch(200, 400, 90, 90, true);

        ElectrocutedEffect electrocuted = new ElectrocutedEffect();
        player.applyStatusEffect(electrocuted);
        assertFalse(player.canJump());
        assertTrue(player.canMove());
        assertTrue(player.canKick());
        assertFalse(player.isParalyzed());
        assertEquals(130.0, player.getEffectiveSpeed(), 0.01);

        UnconsciousEffect unconscious = new UnconsciousEffect();
        player.applyStatusEffect(unconscious);
        assertFalse(player.canMove());
        assertFalse(player.canJump());
        assertFalse(player.canKick());
        assertTrue(player.isParalyzed());
        assertEquals(260.0, player.getEffectiveSpeed(), 0.01);

        player.updateState(0.5, 0, 1000, 490, 100);
        assertEquals(3.0, electrocuted.getRemainingDuration(), 0.01);
        assertEquals(2.5, unconscious.getRemainingDuration(), 0.01);
    }

    /**
     * Should charge power bar when updating player state
     */
    @Test
    public void shouldUpdatePlayersState(){
        p.updateState(0.02, 0, 0, 0, 0);
        assertEquals(0.2, p.getPowerBar(), 0.001);
    }

    /**
     * Should stun the player for 3 seconds and then restore full health
     */
    @Test
    public void shouldStunPlayerForThreeSecondsWhenHealthReachesZeroAndThenRestoreFullHealth() {
        Player player = new Player(200, 400, 90, 90);
        player.resetForNewMatch(200, 400, 90, 90, true);

        player.takeDamage(120.0);
        assertEquals(0.0, player.getCurrentHealth(), 0.01);
        assertTrue(player.isStunned());
        assertFalse(player.canMove());
        assertFalse(player.canJump());
        assertFalse(player.canKick());
        assertEquals("Paralizado", player.getSpriteState());

        player.updateState(2.0, 0, 1000, 490, 100);
        assertTrue(player.isStunned());
        player.updateState(1.1, 0, 1000, 490, 100);
        assertFalse(player.isStunned());
        assertEquals(player.getMaxHealth(), player.getCurrentHealth(), 0.01);
    }

    /**
     * Should handle different status effects and paralysis
     */
    @Test
    public void shouldHandleStatusEffectsAndParalysis() {
        assertTrue(p.canMove());
        assertTrue(p.canJump());
        assertTrue(p.canKick());
        assertFalse(p.isParalyzed());

        p.takeDamage(200.0);
        assertTrue(p.isStunned());
        assertTrue(p.isParalyzed());
        assertFalse(p.canMove());
        assertFalse(p.canJump());
        assertFalse(p.canKick());

        p.paralyzeActions();
        assertEquals(0, p.getVelX(), 0.001);
    }

    /**
     * Should preserve push velocity when horizontal input is received and return identification and facing direction
     */
    @Test
    public void shouldKeepPushVelocityOnMoveInputAndReturnDirectionAndIdentification() {
        Player player = new Player(200, 400, 90, 90);
        player.setNationality(new SouthKorea());
        player.resetForNewMatch(200, 400, 90, 90, true);

        assertEquals("Corea del Sur", player.getIdentification());
        assertEquals(1, player.getFacingDirection());

        player.applyStatusEffect(new PushEffect());
        double pushedVelX = player.getVelX();
        assertTrue(pushedVelX < 0);

        player.moveHorizontal(0);
        assertEquals(pushedVelX, player.getVelX(), 0.01);

        player.resetForNewMatch(700, 400, 90, 90, false);
        assertEquals(-1, player.getFacingDirection());
    }

    /**
     * Should activate power with its conditions
     */
    @Test
    public void shouldTestPowerActivationConditions() {
        assertFalse(p.hasActiveAura());
        p.activatePower();
        assertFalse(p.hasActiveAura());
        p.chargePowerBar(150.0);
        assertEquals(100.0, p.getPowerBar(), 0.001);
        p.activatePower();
        assertTrue(p.hasActiveAura());
        assertEquals(0.0, p.getPowerBar(), 0.001);

        p.consumeAura();
        assertFalse(p.hasActiveAura());
    }

    /**
     * Should reset a match
     */
    @Test
    public void shouldTestMatchResetAndGoals() {
        p.addGoal();
        assertEquals(1, p.getGoals());
        p.resetForNewMatch(50, 50, 80, 80, true);
        assertEquals(0, p.getGoals());
        assertEquals(0.0, p.getPowerBar(), 0.001);
        p.resetAfterGoal(100, 100);
        assertTrue(p.isJumping());
    }
}