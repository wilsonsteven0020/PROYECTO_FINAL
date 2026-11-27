package domain;
import java.io.Serializable;

/**
 * Represents a temporary status effect applied to a player by a power shot
 */
public abstract class StatusEffect implements Serializable{

    private double remainingDuration;

    /**
     * Creates a new StatusEffect
     * @param duration duration in seconds
     */
    public StatusEffect(double duration){
        this.remainingDuration = duration;
    }

    /**
     * Updates the effect timer and applies its continuous logic
     * @param target the affected player
     * @param dt time step in seconds
     */
    public void update(Player target, double dt){
        remainingDuration = Math.max(0.0, remainingDuration - dt);
        if(!isExpired()){
            applyEffectLogic(target, dt);
        }
    }

    /**
     * Indicates if the effect duration has finished
     * @return true if expired, false otherwise
     */
    public boolean isExpired(){
        return remainingDuration <= 0.0;
    }

    /**
     * Indicates if the player is allowed to move horizontally
     * @return true if movement is allowed
     */
    public boolean allowsMove(){
        return true;
    }

    /**
     * Indicates if the player is allowed to jump
     * @return true if jumping is allowed
     */
    public boolean allowsJump(){
        return true;
    }

    /**
     * Indicates if the player is allowed to kick
     * @return true if kicking is allowed
     */
    public boolean allowsKick(){
        return true;
    }

    /**
     * Returns the speed multiplier applied to the player
     * @return speed factor (1.0 by default)
     */
    public double getSpeedMultiplier(){
        return 1.0;
    }

    /**
     * Returns the remaining duration of the effect
     * @return remaining seconds
     */
    public double getRemainingDuration(){
        return remainingDuration;
    }

    /**
     * Executes immediate actions when the effect is first applied
     * @param target the affected player
     */
    public abstract void onApply(Player target);

    /**
     * Executes continuous actions on each frame while the effect is active
     * @param target the affected player
     * @param dt time step in seconds
     */
    public abstract void applyEffectLogic(Player target, double dt);
}