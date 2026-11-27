package domain;

/**
 * Represents South Korea nationality and its Blue Aura power shot
 */
public class SouthKorea extends Nationality {

    private static final double HORIZONTAL_SHOT_SPEED = 860.0;

    /**
     * Creates a new SouthKorea nationality
     */
    public SouthKorea(){
        super("Corea del Sur", "Aura Azul", 40.0);
    }

    /**
     * Launches the ball 100% straight and horizontally at high speed
     * @param ball the ball being shot
     * @param shooter the player triggering the shot
     * @param match the current match
     */
    @Override
    public void executePowerShot(Ball ball, Player shooter, Match match){
        int dir = shooter.getFacingDirection();
        double scale = ball.getScale();
        double margin = 4.0 * scale;

        double launchX;
        if(dir > 0){
            launchX = shooter.getHitboxX() + shooter.getHitboxWidth() + margin;
        }
        else{
            launchX = shooter.getHitboxX() - ball.getWidth() - margin;
        }
        launchX = Math.max(0.0, Math.min(match.getFieldWidth() - ball.getWidth(), launchX));

        double maxLaunchY = match.getGroundY() - ball.getHeight() - margin;
        double launchY = Math.min(ball.getPosY(), maxLaunchY);

        ball.setPosX(launchX);
        ball.setPosY(launchY);
        ball.activatePowerShot(this, shooter, false, 0.75);
        ball.setVelX(dir * HORIZONTAL_SHOT_SPEED);
        ball.setVelY(0.0);
    }

    /**
     * Creates the PushEffect (1.5s)
     * @return a new PushEffect
     */
    @Override
    public StatusEffect createStatusEffect(){
        return new PushEffect();
    }
}