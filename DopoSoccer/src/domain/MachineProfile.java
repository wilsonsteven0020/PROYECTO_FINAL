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



    default double getDistanceToRivalGoal(Match match, MachinePlayer machine, Ball ball){
        SoccerGoal rivalGoal = getRivalGoal(match, machine);
        double ballCenterX = ball.getHitboxCenterX();
        if(machine.isFacingRight()){
            return rivalGoal.getPosX() - ballCenterX;
        }
        else{
            return ballCenterX - (rivalGoal.getPosX() + rivalGoal.getWidth());
        }
    }

    default boolean isBallHighNearRivalGoal(Match match, MachinePlayer machine, Ball ball){
        double scale = machine.getScale();
        double distToRivalGoal = getDistanceToRivalGoal(match, machine, ball);
        SoccerGoal rivalGoal = getRivalGoal(match, machine);
        double ballCenterY = ball.getHitboxCenterY();
        return distToRivalGoal < (240.0 * scale) && ballCenterY < rivalGoal.getPosY() + (30.0 * scale);
    }

    default Ball selectTargetBall(Match match, MachinePlayer machine){
        ArrayList<Ball> balls = match.getBalls();
        if(balls == null || balls.isEmpty()){
            return null;
        }
        if(balls.size() == 1){
            return balls.get(0);
        }

        double machineX = machine.getHitboxCenterX();
        double machineY = machine.getHitboxCenterY();
        Ball closest = balls.get(0);
        double minDistSq = Double.MAX_VALUE;

        for(Ball ball : balls){
            double dx = ball.getHitboxCenterX() - machineX;
            double dy = ball.getHitboxCenterY() - machineY;
            double distSq = (dx * dx) + (dy * dy);
            if(distSq < minDistSq){
                minDistSq = distSq;
                closest = ball;
            }
        }
        return closest;
    }

    default boolean isBallInOwnHalf(Match match, MachinePlayer machine, Ball ball){
        double dx = ball.getHitboxCenterX() - (match.getFieldWidth() / 2.0);
        return (dx * machine.getFacingDirection()) < 0;
    }

    default boolean isBallBehindMachine(MachinePlayer machine, Ball ball){
        double dx = ball.getHitboxCenterX() - machine.getHitboxCenterX();
        return (dx * machine.getFacingDirection()) < 0;
    }

    default double getAttackPositionX(MachinePlayer machine, Ball ball){
        double ballCenterX = ball.getHitboxCenterX();
        double offset = machine.getHitboxWidth() * 0.45;
        return ballCenterX - (machine.getFacingDirection() * offset);
    }

    default void moveTowardX(MachinePlayer machine, double targetX){
        double centerX = machine.getHitboxCenterX();
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

    default boolean shouldJumpForBall(MachinePlayer machine, Ball ball){
        double scale = machine.getScale();
        double dx = Math.abs(ball.getHitboxCenterX() - machine.getHitboxCenterX());

        double ballBottomY = ball.getPosY() + ball.getHeight();
        boolean aboveHead = ballBottomY < machine.getHitboxY() + (12.0 * scale);
        boolean withinJumpHeight = ballBottomY > machine.getHitboxY() - (160.0 * scale);
        return dx < (95.0 * scale) && aboveHead && withinJumpHeight;
    }
}