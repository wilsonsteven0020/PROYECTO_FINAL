package test;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BallTest {
    private Ball ball;

    @Before
    public void setUp() {
        ball = new Ball(100, 100, 40, 40);
    }

    /**
     * Should ball reset its coordinates
     */
    @Test
    public void shouldBallResetProperly() {
        ball.setVelX(500);
        ball.setVelY(300);
        ball.resetPosition(200, 200);

        assertEquals(200.0, ball.getPosX(), 0.001);
        assertEquals(200.0, ball.getPosY(), 0.001);
        assertEquals(0.0, ball.getVelX(), 0.001);
        assertEquals(0.0, ball.getVelY(), 0.001);
    }

    /**
     * Should verify that a fake illusion ball expires after its duration elapses
     */
    @Test
    public void shouldExpireFakeBallAfterDuration() {
        Ball fakeBall = new Ball(500, 300, 35, 35);
        fakeBall.setFakeBall(2.5, "Estados Unidos");
        assertTrue(fakeBall.isFake());
        assertEquals("Estados Unidos", fakeBall.getSkinName());
        assertFalse(fakeBall.isExpiredFake());

        fakeBall.updatePhysics(1.5, 0, 1000, 490, 100);
        assertFalse(fakeBall.isExpiredFake());

        fakeBall.updatePhysics(1.1, 0, 1000, 490, 100);
        assertTrue(fakeBall.isExpiredFake());
    }

    /**
     * Should verify that the ball accelerates downward due to gravity and bounces upward when hitting the ground
     */
    @Test
    public void shouldFallWithGravityAndBounceOnGround() {
        Ball fallingBall = new Ball(400, 200, 35, 35);
        int groundY = 490;
        fallingBall.updatePhysics(0.1, 0, 1000, groundY, 100);
        assertTrue(fallingBall.getVelY() > 0);
        assertTrue(fallingBall.getPosY() > 200);

        fallingBall.setPosY(groundY - fallingBall.getHeight() + 5);
        fallingBall.setVelY(300.0);
        fallingBall.updatePhysics(0.02, 0, 1000, groundY, 100);
        assertEquals(groundY - fallingBall.getHeight(), fallingBall.getPosY(), 0.01);
        assertTrue(fallingBall.getVelY() < 0);
    }

    /**
     * Should handle straight horizontal shot physics, left wall reset and fake ball fallback skin
     */
    @Test
    public void shouldHandleGravityAndPowerStateInBall() {
        Player shooter = new Player(100, 300, 90, 90);
        ball.activatePowerShot(new SouthKorea(), shooter, false, 0.75);
        assertTrue(ball.hasActivePower());
        assertTrue(ball.isStraightHorizontalShot());
        assertTrue(ball.shouldIgnorePlayer(shooter));

        ball.setVelX(-200.0);
        ball.updatePhysics(0.05, 0, 1000, 500, 0);
        assertEquals(0.0, ball.getVelY(), 0.001);

        ball.setPosX(-10);
        ball.updatePhysics(0.1, 0, 1000, 500, 0);
        assertFalse(ball.hasActivePower());
        assertFalse(ball.isStraightHorizontalShot());
        assertTrue(ball.getVelX() > 0);

        Ball fakeWithNullSkin = new Ball(200, 200, 40, 40);
        fakeWithNullSkin.setFakeBall(1.0, null);
        assertTrue(fakeWithNullSkin.isFake());
        assertEquals("Normal", fakeWithNullSkin.getSkinName());
        assertFalse(fakeWithNullSkin.canTriggerPowerShot());
    }

    /**
     * Should bounce on ceiling and right wall, clearing horizontal power shot state
     */
    @Test
    public void shouldBounceOnCeilingAndRightWallAndClearHorizontalPowerShot() {
        Ball ceilingBall = new Ball(400, 95, 35, 35);
        ceilingBall.setVelY(-200.0);
        ceilingBall.updatePhysics(0.05, 0, 1000, 490, 100);
        assertTrue(ceilingBall.getVelY() > 0);

        Ball wallBall = new Ball(970, 300, 35, 35);
        Player shooter = new Player(100, 300, 90, 90);
        wallBall.activatePowerShot(new SouthKorea(), shooter, false, 0.75);
        wallBall.setVelX(780.0);
        wallBall.updatePhysics(0.1, 0, 1000, 490, 100);

        assertTrue(wallBall.getVelX() < 0);
        assertFalse(wallBall.hasActivePower());
        assertFalse(wallBall.isStraightHorizontalShot());

        // Gravity resumes normally once the straight power shot is cleared
        wallBall.setVelY(0.0);
        wallBall.updatePhysics(0.1, 0, 1000, 490, 100);
        assertTrue(wallBall.getVelY() > 0);

        // Ball with minimal speed touching the floor
        Ball floorBall = new Ball(400, 455, 35, 35);
        floorBall.setVelY(5.0);
        floorBall.updatePhysics(0.01, 0, 1000, 490, 100);
        assertEquals(490.0 - 35.0, floorBall.getPosY(), 0.01);
    }

    /**
     * Should return appropriate skin names for normal ball and active power shots
     */
    @Test
    public void shouldReturnSkinNamesAndManagePowerShotCooldown() {
        Player shooter = new Player(100, 300, 90, 90);
        assertEquals("Normal", ball.getSkinName());

        ball.activatePowerShot(new SouthKorea(), shooter, false, 0.75);
        assertTrue(ball.hasActivePower());
        assertEquals("Corea del Sur", ball.getSkinName());

        ball.activatePowerShot(new Cameroon(), shooter, false, 0.75);
        assertTrue(ball.hasActivePower());
        assertEquals("Camerún", ball.getSkinName());

        ball.activatePowerShot(new Nigeria(), shooter, true, 1.05);
        assertTrue(ball.hasActivePower());
        assertEquals("Nigeria", ball.getSkinName());

        ball.activatePowerShot(new UnitedStates(), shooter, true, 0.80);
        assertTrue(ball.hasActivePower());
        assertEquals("Normal", ball.getSkinName());

        ball.clearPowerShot(0.25);
        assertFalse(ball.hasActivePower());
        assertFalse(ball.canTriggerPowerShot());
        assertEquals("Normal", ball.getSkinName());

        ball.updatePhysics(0.3, 0, 1000, 490, 100);
        assertTrue(ball.canTriggerPowerShot());
    }
}