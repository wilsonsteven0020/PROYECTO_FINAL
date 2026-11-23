package domain;

/**
 * Represents the defensive machine profile that protects its own goal
 */
public class DefensiveProfile implements MachineProfile {

    private static final double BASE_GUARD_DISTANCE = 55.0;

    /**
     * Evaluates the match and positions the machine player to defend its goal
     * @param match current match
     * @param machine machine player controlled by this profile
     */
    @Override
    public void decideAction(Match match, MachinePlayer machine){
        Ball targetBall = selectTargetBall(match, machine);
        if(targetBall == null){
            return;
        }

        int machineNum = getMachineNum(match, machine);
        Player rival = getRival(match, machine);

        if(isBallInOwnHalf(match, machine, targetBall)){
            double targetX = getAttackPositionX(machine, targetBall);
            moveTowardX(machine, targetX);

            double ballCenterX = targetBall.getHitboxX() + targetBall.getHitboxWidth() / 2.0;
            double machineCenterX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
            if(machine.getPowerBar() >= 100.0 && !machine.hasActiveAura() && Math.abs(ballCenterX - machineCenterX) < 160.0 * machine.getScale()){
                machine.activatePower();
            }

            if(shouldJumpForBall(machine, targetBall)){
                match.jumpPlayer(machineNum);
            }

            if(machine.isInKickRange(targetBall) || machine.isInKickRange(rival)){
                match.kickPlayer(machineNum);
            }
        }
        else{
            guardGoal(match, machine);
        }
    }

    /**
     * Moves the machine back to guard its own goal at a scaled distance
     * @param match current match
     * @param machine machine player
     */
    public void guardGoal(Match match, MachinePlayer machine){
        double guardDistance = BASE_GUARD_DISTANCE * machine.getScale();
        double guardX;
        if(machine.isFacingRight()){
            SoccerGoal ownGoal = match.getLeftSoccerGoal();
            guardX = ownGoal.getPosX() + ownGoal.getWidth() + guardDistance;
        }
        else{
            SoccerGoal ownGoal = match.getRightSoccerGoal();
            guardX = ownGoal.getPosX() - guardDistance;
        }
        moveTowardX(machine, guardX);
    }
}