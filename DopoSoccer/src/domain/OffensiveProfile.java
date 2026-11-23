package domain;

/**
 * Represents the offensive machine profile that constantly chases the ball and attacks
 */
public class OffensiveProfile implements MachineProfile {

    /**
     * Evaluates the match and drives the machine player toward the ball to attack
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

        if(machine.getPowerBar() >= 100.0 && !machine.hasActiveAura()){
            machine.activatePower();
        }

        if(isBallHighNearRivalGoal(match, machine, targetBall)){
            double retreatX = getRetreatFromRivalGoalX(match, machine);
            moveTowardX(machine, retreatX);
            return;
        }

        double targetX = getAttackPositionX(machine, targetBall);
        moveTowardX(machine, targetX);

        double distToRivalGoal = getDistanceToRivalGoal(match, machine, targetBall);
        if(distToRivalGoal >= (260.0 * machine.getScale()) && shouldJumpForBall(machine, targetBall)){
            match.jumpPlayer(machineNum);
        }

        if(machine.isInKickRange(targetBall)){
            if(shouldKickBall(match, machine, targetBall)){
                match.kickPlayer(machineNum);
            }
        }
        else if(machine.isInKickRange(rival)){
            match.kickPlayer(machineNum);
        }
    }
}