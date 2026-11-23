package domain;

/**
 * Represents Cameroon nationality and its Lightning power shot
 */
public class Cameroon extends Nationality {

    /**
     * Creates a new Cameroon nationality
     */
    public Cameroon(){
        super("Camerún", "Rayo", 60.0);
    }

    /**
     * Launches the ball straight covered in electricity
     * @param ball the ball being shot
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
        ball.activatePowerShot(this, shooter, false, 0.75);
        ball.setVelX(dir * 740.0);
        ball.setVelY(0.0);
    }

    /**
     * Creates the ElectrocutedEffect (3.0s)
     * @return a new ElectrocutedEffect
     */
    @Override
    public StatusEffect createStatusEffect(){
        return new ElectrocutedEffect();
    }
}