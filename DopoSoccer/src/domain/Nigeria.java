package domain;

/**
 * Represents Nigeria nationality and its Tornado power shot
 */
public class Nigeria extends Nationality {

    /**
     * Creates a new Nigeria nationality
     */
    public Nigeria(){
        super("Nigeria", "Tornado", 40.0);
    }

    /**
     * Launches the ball low to the ground bouncing rapidly
     * @param ball the ball being shot
     * @param shooter the player triggering the shot
     * @param match the current match
     */
    @Override
    public void executePowerShot(Ball ball, Player shooter, Match match){
        int dir = shooter.getFacingDirection();
        ball.activatePowerShot(this, shooter, true, 1.05);
        ball.setVelX(dir * 640.0);
        ball.setVelY(420.0);
    }

    /**
     * Creates the UnconsciousEffect (3.0s)
     * @return a new UnconsciousEffect
     */
    @Override
    public StatusEffect createStatusEffect(){
        return new UnconsciousEffect();
    }
}