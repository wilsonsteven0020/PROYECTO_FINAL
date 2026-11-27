package domain;

/**
 * Represents a computer-controlled player in Player vs Machine mode
 */
public class MachinePlayer extends Player {


    private MachineProfile profile;
    private double decisionTimer;

    /**
     * Creates a new MachinePlayer from a base player and an machine profile
     * @param baseStatsPlayer player from which initial position, dimensions, and nationality are copied
     * @param profile initial machine behavior profile
     */
    public MachinePlayer(Player baseStatsPlayer, MachineProfile profile){
        super(
            baseStatsPlayer.getPosX(),
            baseStatsPlayer.getPosY(),
            baseStatsPlayer.getWidth(),
            baseStatsPlayer.getHeight()
        );
        this.profile = profile;
        this.decisionTimer = 0.0;
        if(baseStatsPlayer.getNationality() != null){
            this.setNationality(baseStatsPlayer.getNationality());
        }
    }

    /**
     * Indicates that this player is controlled by a Machine
     * @return true since this player is a machine
     */
    @Override
    public boolean isMachine(){
        return true;
    }

    /**
     * Executes the machine decision cycle delegating behavior to the assigned profile
     * @param match current match in progress
     * @param dt time step in seconds
     */
    public void executeMachine(Match match, double dt){
        if(profile == null || isParalyzed()){
            return;
        }
        decisionTimer -= dt;
        if(decisionTimer <= 0.0){
            profile.decideAction(match, this);
            decisionTimer = profile.getDecisionInterval();
        }
    }

}