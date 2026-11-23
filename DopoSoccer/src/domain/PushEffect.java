package domain;

/**
 * Represents the Push effect from South Korea's power shot
 */
public class PushEffect extends StatusEffect {

    private static final double PUSH_SPEED = 380.0;

    /**
     * Creates a new PushEffect with a duration of 1.5 seconds
     */
    public PushEffect(){
        super("Empuje", 1.5);
    }

    /**
     * Applies initial push velocity toward the target's own goal
     * @param target the affected player
     */
    @Override
    public void onApply(Player target){
        pushTowardOwnGoal(target);
    }

    /**
     * Keeps pushing the player toward their own goal while active
     * @param target the affected player
     * @param dt time step in seconds
     */
    @Override
    public void applyEffectLogic(Player target, double dt){
        pushTowardOwnGoal(target);
    }

    /**
     * Prevents the player from walking against the push
     * @return false
     */
    @Override
    public boolean allowsMove(){
        return false;
    }

    /**
     * Sets scaled horizontal velocity toward the player's own goal
     * @param target the affected player
     */
    private void pushTowardOwnGoal(Player target){
        double scaledSpeed = PUSH_SPEED * target.getScale();
        if(target.isFacingRight()){
            target.setVelX(-scaledSpeed);
        }
        else{
            target.setVelX(scaledSpeed);
        }
    }
}