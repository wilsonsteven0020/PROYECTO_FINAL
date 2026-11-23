package domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface that defines the behavior strategy of a machine-controlled player
 */
public interface MachineProfile extends Serializable {

    /**
     * Analyzes the current match state and executes an action on the machine player
     * @param match current match in progress
     * @param machine machine player that will perform the decision
     */
    void decideAction(Match match, MachinePlayer machine);

    /**
     * Returns the time interval in seconds between consecutive AI decisions
     * @return decision interval in seconds
     */
    default double getDecisionInterval(){
        return 0.08;
    }

    /**
     * Returns the player index (0 for left, 1 for right) of the machine in the match
     * @param match current match
     * @param machine machine player
     * @return 0 if left player, 1 if right player
     */
    default int getMachineNum(Match match, MachinePlayer machine){
        if(match.getLeftPlayer() == machine){
            return 0;
        }
        else{
            return 1;
        }
    }

    /**
     * Returns the rival player facing the machine in the match
     * @param match current match
     * @param machine machine player
     * @return rival Player
     */
    default Player getRival(Match match, MachinePlayer machine){
        if(match.getLeftPlayer() == machine){
            return match.getRightPlayer();
        }
        else{
            return match.getLeftPlayer();
        }
    }

    /**
     * Returns the rival's soccer goal that the machine is attacking
     * @param match current match
     * @param machine machine player
     * @return rival SoccerGoal
     */
    default SoccerGoal getRivalGoal(Match match, MachinePlayer machine){
        if(machine.isFacingRight()){
            return match.getRightSoccerGoal();
        }
        else{
            return match.getLeftSoccerGoal();
        }
    }

    /**
     * Calculates the horizontal distance from the ball center to the mouth of the rival goal
     * @param match current match
     * @param machine machine player
     * @param ball target ball
     * @return horizontal distance in pixels
     */
    default double getDistanceToRivalGoal(Match match, MachinePlayer machine, Ball ball){
        SoccerGoal rivalGoal = getRivalGoal(match, machine);
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        if(machine.isFacingRight()){
            return rivalGoal.getPosX() - ballCenterX;
        }
        else{
            return ballCenterX - (rivalGoal.getPosX() + rivalGoal.getWidth());
        }
    }

    /**
     * Indicates if the ball is bouncing high near the rival goal where jumping would send it over the crossbar
     * @param match current match
     * @param machine machine player
     * @param ball target ball
     * @return true if the machine should step back and let the ball drop
     */
    default boolean isBallHighNearRivalGoal(Match match, MachinePlayer machine, Ball ball){
        double scale = machine.getScale();
        double distToRivalGoal = getDistanceToRivalGoal(match, machine, ball);
        SoccerGoal rivalGoal = getRivalGoal(match, machine);
        double ballCenterY = ball.getPosY() + ball.getHeight() / 2.0;
        return distToRivalGoal < (240.0 * scale) && ballCenterY < rivalGoal.getPosY() + (30.0 * scale);
    }

    /**
     * Returns a safe X position to step back to when the ball is high near the rival goal
     * @param match current match
     * @param machine machine player
     * @return retreat center X coordinate
     */
    default double getRetreatFromRivalGoalX(Match match, MachinePlayer machine){
        double scale = machine.getScale();
        SoccerGoal rivalGoal = getRivalGoal(match, machine);
        double retreatDistance = 260.0 * scale;
        if(machine.isFacingRight()){
            return rivalGoal.getPosX() - retreatDistance;
        }
        else{
            return rivalGoal.getPosX() + rivalGoal.getWidth() + retreatDistance;
        }
    }

    /**
     * Determines if kicking the ball will send it toward the goal instead of launching it over the crossbar
     * @param match current match
     * @param machine machine player
     * @param ball target ball
     * @return true if kicking is advantageous, false if pushing horizontally is better
     */
    default boolean shouldKickBall(Match match, MachinePlayer machine, Ball ball){
        if(machine.hasActiveAura()){
            return true;
        }
        if(isBallInOwnHalf(match, machine, ball)){
            return true;
        }
        double scale = machine.getScale();
        double distToRivalGoal = getDistanceToRivalGoal(match, machine, ball);
        double ballBottomY = ball.getPosY() + ball.getHeight();
        boolean isBallLow = ballBottomY >= match.getGroundY() - (95.0 * scale);
        return distToRivalGoal >= (330.0 * scale) && isBallLow;
    }

    /**
     * Selects a ball to track without cheating when illusion balls are active
     * @param match current match
     * @param machine machine player
     * @return closest Ball or null if none exist
     */
    default Ball selectTargetBall(Match match, MachinePlayer machine){
        ArrayList<Ball> balls = match.getBalls();
        if(balls == null || balls.isEmpty()){
            return null;
        }
        if(balls.size() == 1){
            return balls.get(0);
        }

        double machineX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
        double machineY = machine.getHitboxY() + machine.getHitboxHeight() / 2.0;
        Ball closest = balls.get(0);
        double minDistSq = Double.MAX_VALUE;

        for(Ball ball : balls){
            double bx = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
            double by = ball.getHitboxY() + ball.getHitboxHeight() / 2.0;
            double dx = bx - machineX;
            double dy = by - machineY;
            double distSq = (dx * dx) + (dy * dy);
            if(distSq < minDistSq){
                minDistSq = distSq;
                closest = ball;
            }
        }
        return closest;
    }

    /**
     * Indicates if the given ball is currently in the machine's defensive half
     * @param match current match
     * @param machine machine player
     * @param ball ball to evaluate
     * @return true if the ball is in the machine's own half
     */
    default boolean isBallInOwnHalf(Match match, MachinePlayer machine, Ball ball){
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        double halfField = match.getFieldWidth() / 2.0;
        if(machine.isFacingRight()){
            return ballCenterX < halfField;
        }
        else{
            return ballCenterX > halfField;
        }
    }

    /**
     * Indicates if the ball has passed behind the machine toward the machine's own goal
     * @param machine machine player
     * @param ball target ball
     * @return true if the ball is behind the machine
     */
    default boolean isBallBehindMachine(MachinePlayer machine, Ball ball){
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        double machineCenterX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
        if(machine.isFacingRight()){
            return ballCenterX < machineCenterX;
        }
        else{
            return ballCenterX > machineCenterX;
        }
    }

    /**
     * Calculates an X target slightly behind the ball so the machine pushes it toward the rival goal
     * @param machine machine player
     * @param ball target ball
     * @return recommended center X coordinate
     */
    default double getAttackPositionX(MachinePlayer machine, Ball ball){
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        double offset = machine.getHitboxWidth() * 0.45;
        if(machine.isFacingRight()){
            return ballCenterX - offset;
        }
        else{
            return ballCenterX + offset;
        }
    }

    /**
     * Moves the machine horizontally toward the given center X target using scaled tolerance
     * @param machine machine player
     * @param targetX desired center X coordinate
     */
    default void moveTowardX(MachinePlayer machine, double targetX){
        double centerX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
        double tolerance = 16.0 * machine.getScale();
        if(targetX > centerX + tolerance){
            machine.moveHorizontal(1);
        }
        else if(targetX < centerX - tolerance){
            machine.moveHorizontal(-1);
        }
        else{
            machine.moveHorizontal(0);
        }
    }

    /**
     * Determines if the ball is within scaled horizontal and aerial range to jump for it
     * @param machine machine player
     * @param ball target ball
     * @return true if the machine should jump
     */
    default boolean shouldJumpForBall(MachinePlayer machine, Ball ball){
        double scale = machine.getScale();
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        double machineCenterX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
        double dx = Math.abs(ballCenterX - machineCenterX);

        double ballBottomY = ball.getPosY() + ball.getHeight();
        boolean aboveHead = ballBottomY < machine.getHitboxY() + (12.0 * scale);
        boolean withinJumpHeight = ballBottomY > machine.getHitboxY() - (160.0 * scale);
        return dx < (95.0 * scale) && aboveHead && withinJumpHeight;
    }
}