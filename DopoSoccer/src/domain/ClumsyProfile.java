package domain;

import java.util.Random;

/**
 * Represents the clumsy machine profile that performs random actions
 */
public class ClumsyProfile implements MachineProfile {

    private static final double CLUMSY_INTERVAL = 0.22;

    private final Random random;

    /**
     * Creates a new ClumsyProfile
     */
    public ClumsyProfile(){
        random = new Random();
    }

    /**
     * Returns a slower decision interval so random movements look natural
     * @return decision interval in seconds
     */
    @Override
    public double getDecisionInterval(){
        return CLUMSY_INTERVAL;
    }

    /**
     * Evaluates the match and executes random actions on the machine player
     * @param match current match
     * @param machine machine player controlled by this profile
     */
    @Override
    public void decideAction(Match match, MachinePlayer machine){
        int machineNum = getMachineNum(match, machine);

        int dir = random.nextInt(3) - 1;
        machine.moveHorizontal(dir);

        if(random.nextDouble() < 0.12){
            match.jumpPlayer(machineNum);
        }
        if(random.nextDouble() < 0.18){
            match.kickPlayer(machineNum);
        }
        if(machine.getPowerBar() >= 100.0 && random.nextDouble() < 0.15){
            machine.activatePower();
        }
    }
}