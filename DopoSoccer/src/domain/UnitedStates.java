package domain;

/**
 * Represents United States nationality and its Illusion power shot
 */
public class UnitedStates extends Nationality {

    /**
     * Creates a new UnitedStates nationality
     */
    public UnitedStates(){
        super("Estados Unidos", "Ilusión", 30.0);
    }

    /**
     * Splits the ball into 8 identical balls (1 real and 7 fake for 2.5s)
     * @param ball the real ball being shot
     * @param shooter the player triggering the shot
     * @param match the current match
     */
    @Override
    public void executePowerShot(Ball ball, Player shooter, Match match){
        int dir;
        if(shooter.isFacingRight()){
            dir = 1;
        }
        else{
            dir = -1;
        }
        ball.activatePowerShot(this, shooter, true, 0.80);
        ball.setVelX(dir * 560.0);
        ball.setVelY(-380.0);
        match.spawnIllusionBalls(ball, dir, 7, 2.5);
    }

    /**
     * Creates the ConfusionEffect (2.5s)
     * @return a new ConfusionEffect
     */
    @Override
    public StatusEffect createStatusEffect(){
        return new ConfusionEffect();
    }
}