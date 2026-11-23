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
     * Should verifies that a fake illusion ball expires after its duration elapses
     */
    @Test
    public void shouldExpireFakeBallAfterDuration() {
        Ball fakeBall = new Ball(500, 300, 35, 35);
        fakeBall.setFakeBall(2.5);
        assertTrue(fakeBall.isFake());
        assertFalse(fakeBall.isExpiredFake());
        fakeBall.updatePhysics(1.5, 0, 1000, 490, 100);
        assertFalse(fakeBall.isExpiredFake());

        fakeBall.updatePhysics(1.1, 0, 1000, 490, 100);
        assertTrue(fakeBall.isExpiredFake());
    }

    /**
     * Should verifies that the ball accelerates downward due to gravity and bounces upward when hitting the ground
     */
    @Test
    public void shouldFallWithGravityAndBounceOnGround() {
        Ball ball = new Ball(400, 200, 35, 35);
        int groundY = 490;
        ball.updatePhysics(0.1, 0, 1000, groundY, 100);
        assertTrue(ball.getVelY() > 0);
        assertTrue(ball.getPosY() > 200);
        ball.setPosY(groundY - ball.getHeight() + 5);
        ball.setVelY(300.0);
        ball.updatePhysics(0.02, 0, 1000, groundY, 100);
        assertEquals(groundY - ball.getHeight(), ball.getPosY(), 0.01);
        assertTrue(ball.getVelY() < 0);
    }

    /**
     * Should handle the gravity and the power
     */
    @Test
    public void shouldHandleGravityAndPowerStateInBall() {
        assertTrue(ball.isAffectedByGravity());
        ball.setAffectedByGravity(false);
        assertFalse(ball.isAffectedByGravity());
        ball.setFake(true);
        assertTrue(ball.isFake());
        ball.setAffectedByGravity(false);
        ball.setPosX(-10);
        ball.updatePhysics(0.1, 0, 1000, 500, 0);
        assertFalse(ball.hasActivePower());
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
        assertTrue(wallBall.isAffectedByGravity());

        // Balón con velocidad mínima tocando el suelo
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
        assertNotNull(ball.getSkinName());

        Nationality[] nationalities = {
            new SouthKorea(),
            new Cameroon(),
            new Nigeria(),
            new UnitedStates()
        };

        for (Nationality nationality : nationalities) {
            ball.activatePowerShot(nationality, shooter, false, 0.75);
            assertNotNull(ball.getSkinName());
            assertTrue(ball.hasActivePower());
        }

        ball.clearPowerShot(0.25);
        assertFalse(ball.hasActivePower());
        assertFalse(ball.canTriggerPowerShot());

        ball.updatePhysics(0.3, 0, 1000, 490, 100);
        assertTrue(ball.canTriggerPowerShot());
    }
}