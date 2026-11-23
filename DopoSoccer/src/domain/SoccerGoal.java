package domain;

/**
 * Represents a SoccerGoal
 */
public class SoccerGoal extends Entity{

    private String side;

    /**
     * Creates a new SoccerGoal
     * @param side "LEFT" or "RIGHT"
     * @param posX X coordinate
     * @param posY Y coordinate
     * @param width goal width in pixels
     * @param height goal height in pixels
     */
    public SoccerGoal(String side, double posX, double posY, int width, int height){
        super(posX, posY, width, height);
        this.side = side;
    }

    /**
     * Checks if the ball is inside the soccer goal mouth below the crossbar
     * @param ball the ball to check
     * @return true if the ball entered the goal area
     */
    public boolean isInsideSoccerGoal(Ball ball){
        double crossbarThickness = 10.0 * getScale();
        double ballCenterY = ball.getPosY() + ball.getHeight() / 2.0;
        boolean belowCrossbar = ballCenterY >= getPosY() + crossbarThickness;

        if(!belowCrossbar){
            return false;
        }

        double ballCenterX = ball.getPosX() + ball.getWidth() / 2.0;
        if("LEFT".equals(side)){
            return ballCenterX <= getPosX() + getWidth() * 0.55;
        }
        else{
            return ballCenterX >= getPosX() + getWidth() * 0.45;
        }
    }

    /**
     * Checks if a player is positioned above the goal's crossbar to stand on it
     * @param player the player to check
     * @return true if the player can land on top of this goal
     */
    public boolean canPlayerStandOnTop(Player player){
        boolean overlapX = player.getHitboxX() + player.getHitboxWidth() > getPosX() &&
                           player.getHitboxX() < getPosX() + getWidth();
        boolean aboveTop = player.getPosY() + player.getHeight() <= getPosY() + (18.0 * getScale());
        return overlapX && aboveTop;
    }
    /**
     * Bounces the ball only if it hits the top crossbar roof or front edge of the goal
     * @param ball the ball to check
     */
    public void bounceCrossbar(Ball ball){
        if(!collidesWith(ball)){
            return;
        }
        double crossbarThickness = 10.0 * getScale();
        double ballCenterY = ball.getPosY() + ball.getHeight() / 2.0;

        if(ballCenterY > getPosY() + crossbarThickness){
            return;
        }

        if(ball.getPosY() + ball.getHeight() <= getPosY() + (16.0 * getScale()) && ball.getVelY() >= 0){
            ball.setPosY(getPosY() - ball.getHeight());
            if(ball.isStraightHorizontalShot()){
                ball.clearPowerShot();
                ball.setVelY(-180.0 * getScale());
            }
            else{
                if(ball.hasActivePower()){
                    ball.clearPowerShot();
                }
                ball.setVelY(-Math.abs(ball.getVelY()) * 0.75);
            }
        }
        else{
            int dir;
            if("LEFT".equals(side)){
                dir = 1;
                ball.setPosX(getPosX() + getWidth());
            }
            else{
                dir = -1;
                ball.setPosX(getPosX() - ball.getWidth());
            }
            if(ball.hasActivePower()){
                ball.clearPowerShot();
            }
            ball.setVelX(dir * Math.max(200.0 * getScale(), Math.abs(ball.getVelX()) * 0.8));
        }
    }
    @Override
    public boolean isMachine(){
        return false;
    }

    /**
     * Returns the goal side ("LEFT" or "RIGHT")
     * @return side string
     */
    public String getSide(){
        return side;
    }
}