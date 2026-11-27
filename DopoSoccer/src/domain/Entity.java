package domain;
import java.io.Serializable;

/**
 * Represents a physical entity in the field
 */
public abstract class Entity implements Serializable{
    private double posX;
    private double posY;
    private double velX;
    private double velY;
    private int width;
    private int height;
    private double scale;

    /**
     * Creates a new Entity
     * @param posX initial X coordinate
     * @param posY initial Y coordinate
     * @param width entity width in pixels
     * @param height entity height in pixels
     */
    public Entity(double posX, double posY, int width, int height){
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.velX = 0;
        this.velY = 0;
        this.scale = 1.0;
    }

    /**
     * Updates the entity's position based on its velocity
     * @param dt time step in seconds
     */
    public void updatePosition(double dt){
        setPosX(getPosX() + velX * dt);
        setPosY(getPosY() + velY * dt);
    }

    /**
     * Returns the left X coordinate of the collision box
     * @return hitbox X
     */
    public double getHitboxX(){
        return posX;
    }

    /**
     * Returns the top Y coordinate of the collision box
     * @return hitbox Y
     */
    public double getHitboxY(){
        return posY;
    }

    /**
     * Returns the width of the collision box
     * @return hitbox width
     */
    public double getHitboxWidth(){
        return width;
    }

    /**
     * Returns the height of the collision box
     * @return hitbox height
     */
    public double getHitboxHeight(){
        return height;
    }

    /**
     * Returns the horizontal center of the collision box
     * @return hitbox center X
     */
    public double getHitboxCenterX(){
        return getHitboxX() + getHitboxWidth() / 2.0;
    }

    /**
     * Returns the vertical center of the collision box
     * @return hitbox center Y
     */
    public double getHitboxCenterY(){
        return getHitboxY() + getHitboxHeight() / 2.0;
    }

    /**
     * Checks if this entity collides with another entity in 2D using their hitboxes
     * @param other the other entity
     * @return true if their hitboxes intersect
     */
    public boolean collidesWith(Entity other){
        boolean overlapX = this.getHitboxX() < other.getHitboxX() + other.getHitboxWidth() &&
                           this.getHitboxX() + this.getHitboxWidth() > other.getHitboxX();
        boolean overlapY = this.getHitboxY() < other.getHitboxY() + other.getHitboxHeight() &&
                           this.getHitboxY() + this.getHitboxHeight() > other.getHitboxY();
        return overlapX && overlapY;
    }

    public double getPosX(){
        return posX;
    }

    public double getPosY(){
        return posY;
    }

    public double getVelX(){
        return velX;
    }

    public double getVelY(){
        return velY;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public double getScale(){
        return scale;
    }

    public void setPosX(double x){
        this.posX = x;
    }

    public void setPosY(double y){
        this.posY = y;
    }

    public void setVelX(double vx){
        this.velX = vx;
    }

    public void setVelY(double vy){
        this.velY = vy;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public void setScale(double scale){
        this.scale = scale;
    }

}