package domain;

/**
 * Represents a Ball in the match
 */
public class Ball extends Entity{

    private static final double GRAVITY = 980.0;
    private static final double DEFAULT_BOUNCE = 0.75;
    private static final double FRICTION = 0.985;
    private static final double SHOOTER_GRACE_TIME = 0.18;

    private boolean isFake;
    private double fakeTimer;
    private boolean affectedByGravity;
    private boolean straightHorizontalShot;
    private double bounceFactor;
    private Nationality activePower;
    private Player shooter;
    private double shooterGraceTimer;
    private double powerTriggerCooldown;

    /**
     * Creates a new Ball
     * @param posX initial X coordinate
     * @param posY initial Y coordinate
     * @param width ball width in pixels
     * @param height ball height in pixels
     */
    public Ball(double posX, double posY, int width, int height){
        super(posX, posY, width, height);
        this.isFake = false;
        this.fakeTimer = 0.0;
        this.affectedByGravity = true;
        this.straightHorizontalShot = false;
        this.bounceFactor = DEFAULT_BOUNCE;
        this.activePower = null;
        this.shooter = null;
        this.shooterGraceTimer = 0.0;
        this.powerTriggerCooldown = 0.0;
    }

    /**
     * Resets the ball to the given coordinates and clears any active power
     * @param startX center X coordinate
     * @param startY center Y coordinate
     */
    public void resetPosition(double startX, double startY){
        setPosX(startX);
        setPosY(startY);
        setVelX(0);
        setVelY(0);
        clearPowerShot();
        this.powerTriggerCooldown = 0.0;
    }

    /**
     * Activates a power shot state on this ball
     * @param power nationality power carried by the ball
     * @param shooter player who triggered the power shot
     * @param affectedByGravity true if gravity affects this shot
     * @param bounceFactor bounce elasticity factor
     */
    public void activatePowerShot(Nationality power, Player shooter, boolean affectedByGravity, double bounceFactor){
        this.activePower = power;
        this.shooter = shooter;
        this.affectedByGravity = affectedByGravity;
        this.straightHorizontalShot = !affectedByGravity;
        this.bounceFactor = bounceFactor;
        this.shooterGraceTimer = SHOOTER_GRACE_TIME;
    }

    /**
     * Clears the active power shot state and restores normal ball physics
     */
    public void clearPowerShot(){
        clearPowerShot(0.0);
    }

    /**
     * Clears the active power shot state and optionally prevents immediate re-triggering
     * @param triggerCooldown seconds before another player aura can trigger on this ball
     */
    public void clearPowerShot(double triggerCooldown){
        this.activePower = null;
        this.shooter = null;
        this.affectedByGravity = true;
        this.straightHorizontalShot = false;
        this.bounceFactor = DEFAULT_BOUNCE;
        this.shooterGraceTimer = 0.0;
        this.powerTriggerCooldown = Math.max(this.powerTriggerCooldown, triggerCooldown);
    }

    /**
     * Indicates if the ball currently carries an active power shot
     * @return true if power shot is active
     */
    public boolean hasActivePower(){
        return activePower != null && shooter != null;
    }

    /**
     * Indicates if this ball is currently flying as a locked straight horizontal power shot
     * @return true if straight horizontal shot is active
     */
    public boolean isStraightHorizontalShot(){
        return straightHorizontalShot && hasActivePower();
    }

    /**
     * Indicates if collision with the given player should be ignored (shooter grace window)
     * @param player player to check
     * @return true if the ball should ignore this player
     */
    public boolean shouldIgnorePlayer(Player player){
        return hasActivePower() && shooter == player && (shooterGraceTimer > 0.0 || straightHorizontalShot);
    }

    /**
     * Indicates if a player's aura is currently allowed to trigger a power shot on this ball
     * @return true if eligible for a new power shot
     */
    public boolean canTriggerPowerShot(){
        return !isFake && !hasActivePower() && powerTriggerCooldown <= 0.0;
    }

    /**
     * Returns the nationality power carried by the ball
     * @return active Nationality power or null
     */
    public Nationality getActivePower(){
        return activePower;
    }

    /**
     * Returns the player who shot the active power
     * @return shooter player or null
     */
    public Player getShooter(){
        return shooter;
    }

    @Override
    public boolean isMachine(){
        return false;
    }

    /**
     * Updates the ball's movement, timers and boundary collisions
     * @param dt time step in seconds
     * @param minX left boundary X coordinate
     * @param maxX right boundary X coordinate
     * @param groundY floor Y coordinate
     * @param ceilingY ceiling Y coordinate
     */
    public void updatePhysics(double dt, int minX, int maxX, int groundY, int ceilingY){
        if(isFake){
            fakeTimer = Math.max(0.0, fakeTimer - dt);
        }
        if(shooterGraceTimer > 0.0){
            shooterGraceTimer = Math.max(0.0, shooterGraceTimer - dt);
        }
        if(powerTriggerCooldown > 0.0){
            powerTriggerCooldown = Math.max(0.0, powerTriggerCooldown - dt);
        }

        if(isStraightHorizontalShot()){
            setVelY(0.0);
        }
        else if(affectedByGravity){
            setVelY(getVelY() + (GRAVITY * getScale()) * dt);
        }

        updatePosition(dt);
        handleBoundaries(minX, maxX, groundY, ceilingY);
    }

    /**
     * Handles ball rebounds against the floor, ceiling and side walls
     * @param minX left boundary X coordinate
     * @param maxX right boundary X coordinate
     * @param groundY floor Y coordinate
     * @param ceilingY ceiling Y coordinate
     */
    private void handleBoundaries(int minX, int maxX, int groundY, int ceilingY){
        if(getPosY() + getHeight() >= groundY){
            setPosY(groundY - getHeight());
            if(isStraightHorizontalShot()){
                setVelY(0.0);
            }
            else{
                if(Math.abs(getVelY()) < 60.0 * getScale()){
                    setVelY(0);
                }
                else{
                    setVelY(-Math.min(620.0 * getScale(), Math.abs(getVelY()) * bounceFactor));
                }
                setVelX(getVelX() * FRICTION);
            }
        }
        if(getPosY() < ceilingY){
            setPosY(ceilingY);
            if(isStraightHorizontalShot()){
                clearPowerShot();
            }
            setVelY(Math.abs(getVelY()) * DEFAULT_BOUNCE);
        }
        if(getPosX() < minX){
            setPosX(minX);
            setVelX(Math.abs(getVelX()) * DEFAULT_BOUNCE);
            if(hasActivePower()){
                clearPowerShot();
            }
        }
        else if(getPosX() + getWidth() > maxX){
            setPosX(maxX - getWidth());
            setVelX(-Math.abs(getVelX()) * DEFAULT_BOUNCE);
            if(hasActivePower()){
                clearPowerShot();
            }
        }
    }

    /**
     * Configures this ball as a temporary fake illusion ball
     * @param duration lifetime in seconds
     */
    public void setFakeBall(double duration){
        this.isFake = true;
        this.fakeTimer = duration;
    }

    /**
     * Indicates if this ball is an illusion clone
     * @return true if fake
     */
    public boolean isFake(){
        return isFake;
    }

    /**
     * Sets whether this ball is fake
     * @param isFake true if fake
     */
    public void setFake(boolean isFake){
        this.isFake = isFake;
    }

    /**
     * Indicates if a fake ball's duration has expired
     * @return true if fake and expired
     */
    public boolean isExpiredFake(){
        return isFake && fakeTimer <= 0.0;
    }

    /**
     * Indicates if the ball is affected by gravity
     * @return true if affected by gravity
     */
    public boolean isAffectedByGravity(){
        return affectedByGravity;
    }

    /**
     * Sets whether the ball is affected by gravity
     * @param affectedByGravity true to enable gravity
     */
    public void setAffectedByGravity(boolean affectedByGravity){
        this.affectedByGravity = affectedByGravity;
    }

    /**
     * Returns the skin identifier for this ball based on its active power or illusion state
     * @return country name if a power skin applies, or "Normal" otherwise
     */
    public String getSkinName(){
        if(isFake){
            return "Estados Unidos";
        }
        else if(hasActivePower()){
            String country = activePower.getCountryName();
            if("Estados Unidos".equals(country)){
                return "Normal";
            }
            return country;
        }
        else{
            return "Normal";
        }
    }
}