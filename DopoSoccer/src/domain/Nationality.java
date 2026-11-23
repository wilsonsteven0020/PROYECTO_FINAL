package domain;
import java.io.Serializable;

/**
 * Represents a playable country character and its unique power shot
 */
public abstract class Nationality implements Serializable{

    private String countryName;
    private String powerName;
    private double basePowerDamage;

    /**
     * Creates a new Nationality
     * @param countryName name of the country
     * @param powerName name of the special power shot
     * @param basePowerDamage base damage of the power shot (already multiplied by 5)
     */
    public Nationality(String countryName, String powerName, double basePowerDamage){
        this.countryName = countryName;
        this.powerName = powerName;
        this.basePowerDamage = basePowerDamage;
    }

    /**
     * Returns the country name
     * @return country name
     */
    public String getCountryName(){
        return countryName;
    }

    /**
     * Returns the power shot name
     * @return power name
     */
    public String getPowerName(){
        return powerName;
    }

    /**
     * Returns the base damage of the power shot
     * @return base power damage
     */
    public double getBasePowerDamage(){
        return basePowerDamage;
    }

    /**
     * Calculates the total power shot damage using the shooter's kick multiplier
     * @param shooter the player who fired the power shot
     * @return total power damage
     */
    public double calculatePowerDamage(Player shooter){
        return basePowerDamage * shooter.getKickMultiplier();
    }

    /**
     * Configures the ball's trajectory and special behavior when the power shot is triggered
     * @param ball the ball being shot
     * @param shooter the player triggering the power shot
     * @param match the current match
     */
    public abstract void executePowerShot(Ball ball, Player shooter, Match match);

    /**
     * Creates a new instance of this nationality's status effect
     * @return the status effect to apply on the rival
     */
    public abstract StatusEffect createStatusEffect();
}