package domain;

/**
 * Represents the Electrocuted effect from Cameroon's power shot
 */
public class ElectrocutedEffect extends StatusEffect {

    /**
     * Creates a new ElectrocutedEffect with a duration of 3.0 seconds
     */
    public ElectrocutedEffect(){
        super("Electrocutado", 3.0);
    }

    /**
     * Applies initial electrocution state
     * @param target the affected player
     */
    @Override
    public void onApply(Player target){
        target.setVelX(target.getVelX() * 0.5);
    }

    /**
     * Continuous logic for electrocution
     * @param target the affected player
     * @param dt time step in seconds
     */
    @Override
    public void applyEffectLogic(Player target, double dt){
        // Speed reduction and jump restriction are handled by getters
    }

    /**
     * Prevents the player from jumping while electrocuted
     * @return false
     */
    @Override
    public boolean allowsJump(){
        return false;
    }

    /**
     * Reduces the player's movement speed to 50%
     * @return 0.5 multiplier
     */
    @Override
    public double getSpeedMultiplier(){
        return 0.5;
    }
}    

