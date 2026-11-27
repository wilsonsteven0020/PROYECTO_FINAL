package domain;

/**
 * Represents the Confusion effect from United States' power shot
 */
public class ConfusionEffect extends StatusEffect {

    /**
     * Creates a new ConfusionEffect with a duration of 2.5 seconds
     */
    public ConfusionEffect(){
        super(2.5);
    }

    /**
     * Applies initial confusion state
     * @param target the affected player
     */
    @Override
    public void onApply(Player target){
        // Visual confusion state active for 2.5 seconds
    }

    /**
     * Continuous logic for confusion
     * @param target the affected player
     * @param dt time step in seconds
     */
    @Override
    public void applyEffectLogic(Player target, double dt){
        // Maintained until remainingDuration reaches 0
    }
}