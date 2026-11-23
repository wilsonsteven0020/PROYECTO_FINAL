package test;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Test;


public class SoccerGoalTest {

    /**
     * Should verifies that a ball inside the goal mouth below the crossbar is detected as a goal, while one on the roof is not
     */
    @Test
    public void shouldDetectGoalOnlyWhenBallIsBelowCrossbarAndDeepInside() {
        SoccerGoal leftGoal = new SoccerGoal("LEFT", 0, 340, 52, 148);
        Ball ball = new Ball(5, 400, 35, 35);

        assertTrue(leftGoal.isInsideSoccerGoal(ball));
        ball.setPosY(325);
        assertFalse(leftGoal.isInsideSoccerGoal(ball));
    }

    /**
     * Should verifies that a ball falling onto the top crossbar bounces upward
     */
    @Test
    public void shouldBounceBallUpwardWhenHittingTopCrossbar() {
        SoccerGoal rightGoal = new SoccerGoal("RIGHT", 950, 340, 52, 148);
        Ball ball = new Ball(955, 315, 35, 35);
        ball.setVelY(250.0);

        rightGoal.bounceCrossbar(ball);

        assertTrue(ball.getVelY() < 0);
        assertEquals(rightGoal.getPosY() - ball.getHeight(), ball.getPosY(), 0.01);
    }

    /**
     * Should v erifies that a player positioned above the goal can stand on top of the crossbar
     */
    @Test
    public void shouldAllowPlayerToStandOnTopOfGoalWhenAboveCrossbar() {
        SoccerGoal leftGoal = new SoccerGoal("LEFT", 0, 340, 52, 148);
        Player player = new Player(5, 250, 90, 90);

        assertTrue(leftGoal.canPlayerStandOnTop(player));

        player.setPosY(400);
        assertFalse(leftGoal.canPlayerStandOnTop(player));
    }
    /**
     * Should return goal side and identify that goal is not a machine
     */
    @Test
    public void shouldReturnGoalSideAndNotBeMachine() {
        SoccerGoal leftGoal = new SoccerGoal("LEFT", 0, 340, 52, 148);
        SoccerGoal rightGoal = new SoccerGoal("RIGHT", 950, 340, 52, 148);

        assertEquals("LEFT", leftGoal.getSide());
        assertEquals("RIGHT", rightGoal.getSide());
        assertFalse(leftGoal.isMachine());
    }

    /**
     * Should bounce power shots off top crossbar roof and front edge on both goals
     */
    @Test
    public void shouldBouncePowerShotsOnCrossbarRoofAndFrontEdge() {
        SoccerGoal leftGoal = new SoccerGoal("LEFT", 0, 340, 52, 148);
        SoccerGoal rightGoal = new SoccerGoal("RIGHT", 950, 340, 52, 148);
        Player shooter = new Player(100, 400, 90, 90);

        // Tiro horizontal recto (Corea del Sur) golpeando techo del arco
        Ball straightBall = new Ball(955, 315, 35, 35);
        straightBall.activatePowerShot(new SouthKorea(), shooter, false, 0.75);
        straightBall.setVelY(0.0);
        rightGoal.bounceCrossbar(straightBall);
        assertFalse(straightBall.hasActivePower());
        assertEquals(-180.0, straightBall.getVelY(), 0.01);

        // Tiro con poder no recto (Nigeria) golpeando techo del arco
        Ball curvePowerBall = new Ball(955, 315, 35, 35);
        curvePowerBall.activatePowerShot(new Nigeria(), shooter, true, 1.05);
        curvePowerBall.setVelY(200.0);
        rightGoal.bounceCrossbar(curvePowerBall);
        assertFalse(curvePowerBall.hasActivePower());
        assertTrue(curvePowerBall.getVelY() < 0);

        // Borde frontal del arco izquierdo con tiro de poder activo
        Ball frontLeftBall = new Ball(30, 330, 35, 35);
        frontLeftBall.activatePowerShot(new Cameroon(), shooter, false, 0.75);
        frontLeftBall.setVelX(-100.0);
        leftGoal.bounceCrossbar(frontLeftBall);
        assertFalse(frontLeftBall.hasActivePower());
        assertEquals(52.0, frontLeftBall.getPosX(), 0.01);
        assertTrue(frontLeftBall.getVelX() >= 200.0);

        // Borde frontal del arco derecho
        Ball frontRightBall = new Ball(935, 330, 35, 35);
        frontRightBall.setVelX(300.0);
        rightGoal.bounceCrossbar(frontRightBall);
        assertEquals(950.0 - 35.0, frontRightBall.getPosX(), 0.01);
        assertTrue(frontRightBall.getVelX() <= -200.0);

        // Balón debajo del travesaño entrando al arco: no rebota
        Ball insideBall = new Ball(10, 400, 35, 35);
        insideBall.setVelX(-150.0);
        leftGoal.bounceCrossbar(insideBall);
        assertEquals(-150.0, insideBall.getVelX(), 0.01);
    }
}