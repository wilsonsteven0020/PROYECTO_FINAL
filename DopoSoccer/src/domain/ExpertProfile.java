package domain;

/**
 * Represents the expert machine profile that dynamically adapts to the match state
 */
public class ExpertProfile implements MachineProfile {

    private static final double EXPERT_INTERVAL = 0.01;

    private final OffensiveProfile offensive;
    private final DefensiveProfile defensive;

    /**
     * Creates a new ExpertProfile
     */
    public ExpertProfile(){
        offensive = new OffensiveProfile();
        defensive = new DefensiveProfile();
    }

    /**
     * Returns a faster decision interval for the expert profile
     * @return decision interval in seconds
     */
    @Override
    public double getDecisionInterval(){
        return EXPERT_INTERVAL;
    }

    /**
     * Evaluates the match state to choose between attacking, clearing, dodging or guarding
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

        if(targetBall.hasActivePower() && targetBall.getShooter() == rival){
            double ballCenterX = targetBall.getHitboxX() + targetBall.getHitboxWidth() / 2.0;
            double machineCenterX = machine.getHitboxX() + machine.getHitboxWidth() / 2.0;
            double dx = ballCenterX - machineCenterX;
            boolean comingTowardMachine = (dx < 0 && targetBall.getVelX() > 0) || (dx > 0 && targetBall.getVelX() < 0);
            if(comingTowardMachine && Math.abs(dx) < 170.0 * machine.getScale()){
                match.jumpPlayer(machineNum);
            }
        }

        boolean inOwnHalf = isBallInOwnHalf(match, machine, targetBall);
        boolean closerToBall = isCloserToBall(machine, rival, targetBall);
        boolean ballBehind = isBallBehindMachine(machine, targetBall);
        boolean isWinning = machine.getGoals() > rival.getGoals();
        boolean isLosing = machine.getGoals() < rival.getGoals();
        boolean rivalHasAura = rival.hasActiveAura();

        if(inOwnHalf){
            defensive.decideAction(match, machine);
        }
        else if(ballBehind && !closerToBall){
            defensive.guardGoal(match, machine);
        }
        else if(isLosing){
            offensive.decideAction(match, machine);
        }
        else if(closerToBall && (!isWinning || !rivalHasAura)){
            offensive.decideAction(match, machine);
        }
        else{
            defensive.guardGoal(match, machine);
        }
    }

    /**
     * Indicates if the machine is horizontally closer to the target ball than the rival
     * @param machine machine player
     * @param rival rival player
     * @param ball target ball
     * @return true if the machine is closer or at equal distance
     */
    private boolean isCloserToBall(MachinePlayer machine, Player rival, Ball ball){
        double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        double machineDist = Math.abs(ballCenterX - (machine.getHitboxX() + machine.getHitboxWidth() / 2.0));
        double rivalDist = Math.abs(ballCenterX - (rival.getHitboxX() + rival.getHitboxWidth() / 2.0));
        return machineDist <= rivalDist;
    }
}