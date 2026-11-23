package domain;

import java.util.HashMap;

/**
 * Represents a Player in DOPOSoccer
 */
public class Player extends Entity{

    private static final double BASE_SPEED = 260.0;
    private static final double BASE_JUMP_SPEED = 480.0;
    private static final double GRAVITY = 980.0;
    private static final double BASE_KICK_DAMAGE = 10.0;
    private static final double HITBOX_WIDTH_FACTOR = 0.70;
    private static final double HITBOX_HEIGHT_FACTOR = 0.72;

    private int goals;
    private int points;
    private Nationality nationality;
    private StatusEffect activeEffect;
    private double currentHealth;
    private double powerBar;
    private boolean facingRight;
    private double stunTimer;
    private double kickTimer;
    private boolean hasActiveAura;
    private boolean isJumping;
    private boolean isKicking;

    //Player's stats (Speed, Jump, Kick, Survival)
    private HashMap<String, Integer> stats;

    /**
     * Creates a new Player
     * @param posX initial X coordinate
     * @param posY initial Y coordinate
     * @param width player width
     * @param height player height
     */
    public Player(double posX, double posY, int width, int height){
        super(posX, posY, width, height);
        points = 0;
        goals = 0;
        powerBar = 0;
        currentHealth = 100;
        stunTimer = 0;
        kickTimer = 0;
        hasActiveAura = false;
        isJumping = false;
        isKicking = false;
        activeEffect = null;
        stats = new HashMap<>();
        stats.put("Speed", 1);
        stats.put("Jump", 1);
        stats.put("Kick", 1);
        stats.put("Survival", 1);
    }

    /**
     * Returns the reduced hitbox width so collisions fit the character body
     * @return hitbox width
     */
    @Override
    public double getHitboxWidth(){
        return getWidth() * HITBOX_WIDTH_FACTOR;
    }

    /**
     * Returns the reduced hitbox height so the ball can pass over the player
     * @return hitbox height
     */
    @Override
    public double getHitboxHeight(){
        return getHeight() * HITBOX_HEIGHT_FACTOR;
    }

    /**
     * Returns the centered horizontal hitbox coordinate
     * @return hitbox X
     */
    @Override
    public double getHitboxX(){
        return getPosX() + (getWidth() - getHitboxWidth()) / 2.0;
    }

    @Override
    public boolean isMachine(){
        return false;
    }

    /**
     * Returns the vertical hitbox coordinate aligned with the player's feet
     * @return hitbox Y
     */
    @Override
    public double getHitboxY(){
        return getPosY() + (getHeight() - getHitboxHeight());
    }

    /**
     * Upgrades a specific stat if the player has enough points
     * @param statName the stat that will be upgraded
     * @throws DopoSoccerException MAX_LEVEL_REACHED
     *                             NOT_ENOUGH_POINTS
     */
    public void upgradeStat(String statName) throws DopoSoccerException{
        int currentLevel = getStatLevel(statName);
        if(currentLevel >= 10){
            throw new DopoSoccerException(DopoSoccerException.MAX_LEVEL_REACHED);
        }
        int cost = getUpgradeCost(statName);
        if(cost > points){
            throw new DopoSoccerException(DopoSoccerException.NOT_ENOUGH_POINTS);
        }
        else{
            points -= cost;
        }
        stats.put(statName, currentLevel + 1);
    }

    /**
     * Returns the current level of a stat
     * @param statName name of the stat
     * @return stat level (1 to 10)
     */
    public int getStatLevel(String statName){
        return stats.get(statName);
    }

    /**
     * Adds points to the player's total
     * @param points amount of points to add
     */
    public void givePoints(int points){
        this.points += points;
    }

    /**
     * Returns the player's accumulated points
     * @return total points
     */
    public int getPoints(){
        return points;
    }

    /**
     * Calculates the upgrade cost for the given stat
     * @param statName name of the stat
     * @return upgrade cost in points
     */
    public int getUpgradeCost(String statName){
        return (int)(500 * Math.pow(2, getStatLevel(statName) - 1));
    }

    /**
     * Returns the goals scored in the current match
     * @return goals count
     */
    public int getGoals(){
        return goals;
    }

    /**
     * Sets the player's chosen nationality
     * @param nationality country character
     */
    public void setNationality(Nationality nationality){
        this.nationality = nationality;
    }

    /**
     * Returns the player's nationality
     * @return nationality instance
     */
    public Nationality getNationality(){
        return nationality;
    }

    /**
     * Calculates the player's maximum health based on Survival stat
     * @return maximum health
     */
    public double getMaxHealth(){
        return 100 * (1 + 0.10 * (stats.get("Survival") - 1));
    }

    /**
     * Calculates the kick damage multiplier based on Kick stat
     * @return kick multiplier
     */
    public double getKickMultiplier(){
        return 1 + 0.10 * (stats.get("Kick") - 1);
    }

    /**
     * Calculates normal kick damage
     * @return kick damage
     */
    public double getKickDamage(){
        return BASE_KICK_DAMAGE * getKickMultiplier();
    }

    /**
     * Calculates effective horizontal speed scaled to the field resolution
     * @return effective speed
     */
    public double getEffectiveSpeed(){
        double speed = BASE_SPEED * getScale() * (1 + 0.10 * (stats.get("Speed") - 1));
        if(activeEffect != null){
            speed = speed * activeEffect.getSpeedMultiplier();
        }
        return speed;
    }

    /**
     * Calculates effective jump speed scaled to the field resolution
     * @return effective jump speed
     */
    public double getEffectiveJumpSpeed(){
        return BASE_JUMP_SPEED * getScale() * (1 + 0.10 * (stats.get("Jump") - 1));
    }

    /**
     * Returns the country name of the player's nationality
     * @return country name
     */
    public String getIdentificaction(){
        return nationality.getCountryName();
    }

    /**
     * Charges the player's power bar up to a maximum of 100
     * @param amount amount to charge
     */
    public void chargePowerBar(double amount){
        powerBar = Math.min(100, powerBar + amount);
    }

    /**
     * Applies damage to the player's health and triggers 3s stun if health reaches 0
     * @param damage amount of damage taken
     */
    public void takeDamage(double damage){
        if(isStunned()){
            return;
        }
        currentHealth = Math.max(0, currentHealth - damage);
        if(currentHealth == 0){
            stunTimer = 3.0;
            paralyzeActions();
        }
    }

    /**
     * Applies a new status effect from a power shot, replacing any previous effect
     * @param newEffect the status effect to apply
     */
    public void applyStatusEffect(StatusEffect newEffect){
        this.activeEffect = newEffect;
        if(this.activeEffect != null){
            this.activeEffect.onApply(this);
        }
    }

    /**
     * Returns the currently active status effect
     * @return active StatusEffect or null
     */
    public StatusEffect getActiveEffect(){
        return activeEffect;
    }

    /**
     * Stops horizontal movement and cancels kicking when stunned or unconscious
     */
    public void paralyzeActions(){
        isKicking = false;
        kickTimer = 0;
        setVelX(0);
    }

    /**
     * Indicates if the player is stunned due to health reaching zero
     * @return true if stunned
     */
    public boolean isStunned(){
        return stunTimer > 0;
    }

    /**
     * Indicates if the player is paralyzed either by stun or by an unconscious effect
     * @return true if paralyzed
     */
    public boolean isParalyzed(){
        if(isStunned()){
            return true;
        }
        if(activeEffect != null && !activeEffect.allowsMove() && !activeEffect.allowsKick()){
            return true;
        }
        return false;
    }

    /**
     * Indicates if the player can currently move horizontally
     * @return true if allowed to move
     */
    public boolean canMove(){
        if(isStunned()){
            return false;
        }
        if(activeEffect != null){
            return activeEffect.allowsMove();
        }
        return true;
    }

    /**
     * Indicates if the player can currently jump
     * @return true if allowed to jump
     */
    public boolean canJump(){
        if(isStunned()){
            return false;
        }
        if(activeEffect != null){
            return activeEffect.allowsJump();
        }
        return true;
    }

    /**
     * Indicates if the player can currently kick
     * @return true if allowed to kick
     */
    public boolean canKick(){
        if(isStunned()){
            return false;
        }
        if(activeEffect != null){
            return activeEffect.allowsKick();
        }
        return true;
    }

    /**
     * Returns current health
     * @return current health
     */
    public double getCurrentHealth(){
        return currentHealth;
    }

    /**
     * Returns current power bar percentage
     * @return power bar value
     */
    public double getPowerBar(){
        return powerBar;
    }

    /**
     * Resets player stats, position and scaled dimensions for a new match
     * @param startX initial X coordinate
     * @param startY initial Y coordinate
     * @param width scaled width
     * @param height scaled height
     * @param facingRight true if facing right
     */
    public void resetForNewMatch(double startX, double startY, int width, int height, boolean facingRight){
        goals = 0;
        powerBar = 0;
        this.currentHealth = getMaxHealth();
        this.facingRight = facingRight;
        stunTimer = 0;
        kickTimer = 0;
        hasActiveAura = false;
        isJumping = false;
        isKicking = false;
        activeEffect = null;
        setPosX(startX);
        setPosY(startY);
        setWidth(width);
        setHeight(height);
        setVelX(0);
        setVelY(0);
    }

    /**
     * Repositions the player to fall from the center after a goal is scored
     * @param startX center-side X coordinate
     * @param startY drop Y coordinate
     */
    public void resetAfterGoal(double startX, double startY){
        isKicking = false;
        kickTimer = 0;
        isJumping = true;
        activeEffect = null;
        if(isStunned()){
            stunTimer = 0;
            currentHealth = getMaxHealth();
        }
        setPosX(startX);
        setPosY(startY);
        setVelX(0);
        setVelY(0);
    }

    /**
     * Moves the player horizontally (-1 left, 1 right, 0 stop)
     * @param direction movement direction
     */
    public void moveHorizontal(int direction){
        if(!canMove()){
            setVelX(0);
            return;
        }
        setVelX(direction * getEffectiveSpeed());
    }

    /**
     * Makes the player jump if standing on a surface (ground or goal crossbar)
     * @param surfaceY the Y coordinate of the surface below the player
     */
    public void jump(int surfaceY){
        if(!canJump()){
            return;
        }
        if(!isJumping && getPosY() + getHeight() >= surfaceY - 3){
            isJumping = true;
            setVelY(-getEffectiveJumpSpeed());
        }
    }

    /**
     * Triggers the temporary kick state if allowed
     * @return true if the kick was executed
     */
    public boolean kick(){
        if(!canKick() || isKicking){
            return false;
        }
        isKicking = true;
        kickTimer = 0.25;
        return true;
    }

    /**
     * Checks if a target entity is within the foot's kick reach
     * @param target the entity (ball or rival) to check
     * @return true if inside kick range
     */
    public boolean isInKickRange(Entity target){
        double reach = getHitboxWidth() * 0.45;
        double kickX;
        if(facingRight){
            kickX = getHitboxX() + getHitboxWidth() * 0.35;
        }
        else{
            kickX = getHitboxX() - reach;
        }
        double kickW = getHitboxWidth() * 0.65 + reach;
        boolean overlapX = target.getHitboxX() < kickX + kickW &&
                           target.getHitboxX() + target.getHitboxWidth() > kickX;
        boolean overlapY = target.getHitboxY() < getHitboxY() + getHitboxHeight() &&
                           target.getHitboxY() + target.getHitboxHeight() > getHitboxY();
        return overlapX && overlapY;
    }

    /**
     * Updates the player's state, timers, active effects and position
     * @param dt time step in seconds
     * @param minX minimum X boundary
     * @param maxX maximum X boundary
     * @param surfaceY floor or goal top Y coordinate below the player
     * @param ceilingY upper Y boundary of the field
     */
    public void updateState(double dt, int minX, int maxX, int surfaceY, int ceilingY){
        chargePowerBar(dt * 10);

        if(isStunned()){
            stunTimer -= dt;
            setVelX(0);
            if(stunTimer <= 0){
                stunTimer = 0;
                currentHealth = getMaxHealth();
            }
        }

        if(activeEffect != null){
            activeEffect.update(this, dt);
            if(activeEffect.isExpired()){
                if(!activeEffect.allowsMove()){
                    setVelX(0);
                }
                activeEffect = null;
            }
        }

        if(kickTimer > 0){
            kickTimer = Math.max(0, kickTimer - dt);
            if(kickTimer == 0){
                isKicking = false;
            }
        }

        if(getPosY() + getHeight() < surfaceY || getVelY() < 0){
            isJumping = true;
            setVelY(getVelY() + (GRAVITY * getScale()) * dt);
        }
        updatePosition(dt);

        if(getHitboxY() < ceilingY){
            setPosY(ceilingY - (getHeight() - getHitboxHeight()));
            if(getVelY() < 0){
                setVelY(0);
            }
        }

        if(getPosY() + getHeight() >= surfaceY && getVelY() >= 0){
            setPosY(surfaceY - getHeight());
            setVelY(0);
            isJumping = false;
        }

        if(getPosX() < minX){
            setPosX(minX);
        }
        else if(getPosX() + getWidth() > maxX){
            setPosX(maxX - getWidth());
        }
    }

    /**
     * Indicates if the player faces right
     * @return true if facing right
     */
    public boolean isFacingRight(){
        return facingRight;
    }

    /**
     * Indicates if the player currently has an active power aura
     * @return true if aura is active
     */
    public boolean hasActiveAura(){
        return hasActiveAura;
    }

    /**
     * Consumes the active aura when the player touches the ball to fire a power shot
     */
    public void consumeAura(){
        this.hasActiveAura = false;
    }

    /**
     * Indicates if the player is in the air
     * @return true if jumping
     */
    public boolean isJumping(){
        return isJumping;
    }

    /**
     * Indicates if the player is currently kicking
     * @return true if kicking
     */
    public boolean isKicking(){
        return isKicking;
    }

    /**
     * Activates the power aura if the power bar is full and the player does not already have one
     */
    public void activatePower(){
        if(powerBar >= 100.0 && !hasActiveAura && !isParalyzed()){
            hasActiveAura = true;
            powerBar = 0;
        }
    }

    /**
     * Increments the player's goal count by one
     */
    public void addGoal(){
        goals++;
    }

    /**
     * Returns the exact sprite suffix according to the player's state and aura
     * @return sprite suffix (ex: "Normal", "Saltar", "AuraSaltar", "Patear", "Paralizado")
     */
    public String getSpriteState(){
        String prefix;
        if(hasActiveAura){
            prefix = "Aura";
        }
        else{
            prefix = "";
        }

        if(isParalyzed()){
            return prefix + "Paralizado";
        }
        if(isKicking){
            return prefix + "Patear";
        }
        if(isJumping){
            return prefix + "Saltar";
        }
        if(Math.abs(getVelX()) > 0.1){
            return prefix + "Caminar";
        }
        return prefix + "Normal";
    }
}