package domain;

/**
 * Represents the Unconscious effect from Nigeria's power shot
 */
public class UnconsciousEffect extends StatusEffect {

    private static final double LAUNCH_SPEED_Y = -520.0;

    /**
     * Creates a new UnconsciousEffect with a duration of 3.0 seconds
     */
    public UnconsciousEffect(){
        super(3.0);
    }

    /**
     * Launches the player into the air and paralyzes their actions
     * @param target the affected player
     */
@Override
    public void onApply(Player target){
        target.paralyzeActions();
        target.setVelY(LAUNCH_SPEED_Y * target.getScale());
    }
    /**
     * Keeps the player's horizontal movement paralyzed
     * @param target the affected player
     * @param dt time step in seconds
     */
    @Override
    public void applyEffectLogic(Player target, double dt){
        target.paralyzeActions();
    }

    /**
     * Prevents horizontal movement
     * @return false
     */
    @Override
    public boolean allowsMove(){
        return false;
    }

    /**
     * Prevents jumping
     * @return false
     */
    @Override
    public boolean allowsJump(){
        return false;
    }

    /**
     * Prevents kicking
     * @return false
     */
    @Override
    public boolean allowsKick(){
        return false;
    }

    
}