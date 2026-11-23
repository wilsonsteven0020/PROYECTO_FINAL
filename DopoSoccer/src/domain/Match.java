package domain;
import java.io.Serializable;

import java.util.ArrayList;

/**
 * Represents a Match in DOPOSoccer
 */
public class Match implements Serializable{

    private static final int BASE_FIELD_HEIGHT = 576;
    private static final int BASE_PLAYER_SIZE = 90;
    private static final int BASE_GOAL_WIDTH = 52;
    private static final int BASE_GOAL_HEIGHT = 148;
    private static final int BASE_BALL_SIZE = 42;
    private static final double KICK_BALL_SPEED_X = 570.0;
    private static final double KICK_BALL_SPEED_Y = -460.0;

    private Player leftPlayer;
    private Player rightPlayer;
    private SoccerGoal leftSoccerGoal;
    private SoccerGoal rightSoccerGoal;
    private ArrayList<Ball> balls;
    private double remainingTime;
    private double initialTime;
    private int fieldWidth;
    private int fieldHeight;
    private int usableFieldWidth;
    private double scale;
    private int groundY;
    private int ceilingY;
    private int minPlayerX;
    private int maxPlayerX;
    private boolean isGoalBased;
    private int targetGoals;
    private boolean isPaused;
    private boolean isSuddenDeath;
    private boolean thereIsWinner;
    private int winnerNum;

    /**
     * Creates a new Match
     * @param leftPlayer the left player
     * @param rightPlayer the right player
     * @param isGoalBased true if match is goal-based, false if time-based
     * @param targetValue target goals or duration in minutes
     * @param fieldWidth width of the field in pixels
     * @param fieldHeight height of the field in pixels
     */
    public Match(Player leftPlayer, Player rightPlayer, boolean isGoalBased, int targetValue, int fieldWidth, int fieldHeight){
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
        this.isGoalBased = isGoalBased;
        this.isPaused = false;
        this.isSuddenDeath = false;
        this.thereIsWinner = false;
        this.winnerNum = -1;
        if(isGoalBased){
            this.remainingTime = -1.0;
            this.initialTime = -1.0;
            this.targetGoals = targetValue;
        }
        else{
            this.remainingTime = targetValue * 60.0;
            this.initialTime = targetValue * 60.0;
            this.targetGoals = 0;
        }
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        prepareEntities();
    }

    /**
     * Initializes goals, players and the ball scaled to the field resolution
     */
    private void prepareEntities(){
        this.scale = (double) fieldHeight / BASE_FIELD_HEIGHT;
        int usablePanelHeight = fieldHeight - 38;
        this.usableFieldWidth = fieldWidth - 16;
        this.groundY = (int) Math.round(usablePanelHeight * 0.91);
        this.ceilingY = (int) Math.round(usablePanelHeight * 0.22);
        this.minPlayerX = (int) Math.round(usableFieldWidth * 0.02);
        this.maxPlayerX = (int) Math.round(usableFieldWidth * 0.98);

        int goalWidth = (int) Math.round(BASE_GOAL_WIDTH * scale);
        int goalHeight = (int) Math.round(BASE_GOAL_HEIGHT * scale);
        int goalY = groundY - goalHeight;

        leftSoccerGoal = new SoccerGoal("LEFT", 0, goalY, goalWidth, goalHeight);
        leftSoccerGoal.setScale(scale);

        rightSoccerGoal = new SoccerGoal("RIGHT", usableFieldWidth - goalWidth, goalY, goalWidth, goalHeight);
        rightSoccerGoal.setScale(scale);

        int playerSize = (int) Math.round(BASE_PLAYER_SIZE * scale);
        int playerY = groundY - playerSize;

        leftPlayer.setScale(scale);
        rightPlayer.setScale(scale);
        leftPlayer.resetForNewMatch(usableFieldWidth * 0.20, playerY, playerSize, playerSize, true);
        rightPlayer.resetForNewMatch((usableFieldWidth * 0.80) - playerSize, playerY, playerSize, playerSize, false);

        int ballSize = (int) Math.round(BASE_BALL_SIZE * scale);
        Ball mainBall = new Ball((usableFieldWidth - ballSize) / 2.0, fieldHeight * 0.35, ballSize, ballSize);
        mainBall.setScale(scale);

        balls = new ArrayList<>();
        balls.add(mainBall);
    }

    /**
     * Toggles the pause state of the match
     */
    public void togglePause(){
        isPaused = !isPaused;
    }

    /**
     * Indicates if the match is currently paused
     * @return true if paused, false otherwise
     */
    public boolean isPaused(){
        return isPaused;
    }

    /**
     * Indicates if there is a winner and the match has ended
     * @return true if there is a winner, false otherwise
     */
    public boolean isThereWinner(){
        return thereIsWinner;
    }

    /**
     * Returns the winner player number (0 for leftPlayer, 1 for rightPlayer, -1 if none)
     * @return winner player number
     */
    public int getWinnerNum(){
        return winnerNum;
    }

    /**
     * Indicates if the match is in sudden death overtime
     * @return true if in sudden death, false otherwise
     */
    public boolean isSuddenDeath(){
        return isSuddenDeath;
    }

    /**
     * Indicates if the match is based on goals
     * @return true if goal-based, false if time-based
     */
    public boolean isGoalBased(){
        return isGoalBased;
    }

    /**
     * Returns the remaining goals needed by the leading player to win
     * @return missing goals to reach targetGoals
     */
    public int getMissingGoals(){
        int maxCurrentGoals = Math.max(leftPlayer.getGoals(), rightPlayer.getGoals());
        return Math.max(0, targetGoals - maxCurrentGoals);
    }

    /**
     * Returns match's remaining time in seconds
     * @return match's remaining time
     */
    public int getRemainingTime(){
        return (int) Math.ceil(remainingTime);
    }
    /**
     * Sets the pause state explicitly
     * @param paused true to pause, false to resume
     */
    public void setPaused(boolean paused){
        this.isPaused = paused;
    }

    /**
     * Returns the initial match duration in seconds (-1 if goal-based)
     * @return initial duration in seconds
     */
    public int getInitialTime(){
        return (int) Math.round(initialTime);
    }

    /**
     * Returns the original window width used when creating this match
     * @return window width in pixels
     */
    public int getWindowWidth(){
        return fieldWidth;
    }

    /**
     * Returns the original window height used when creating this match
     * @return window height in pixels
     */
    public int getWindowHeight(){
        return fieldHeight;
    }

    /**
     * Returns the floor Y coordinate
     * @return ground Y
     */
    public int getGroundY(){
        return groundY;
    }

    /**
     * Returns the left soccer goal
     * @return left SoccerGoal
     */
    public SoccerGoal getLeftSoccerGoal(){
        return leftSoccerGoal;
    }

    /**
     * Returns the right soccer goal
     * @return right SoccerGoal
     */
    public SoccerGoal getRightSoccerGoal(){
        return rightSoccerGoal;
    }

    /**
     * Returns the list of balls currently in the match
     * @return list of Ball instances
     */
    public ArrayList<Ball> getBalls(){
        return balls;
    }

    /**
     * Returns the left player in the match
     * @return left Player
     */
    public Player getLeftPlayer(){
        return leftPlayer;
    }

    /**
     * Returns the right player in the match
     * @return right Player (human or MachinePlayer)
     */
    public Player getRightPlayer(){
        return rightPlayer;
    }

    /**
     * Returns the usable field width in pixels
     * @return field width
     */
    public int getFieldWidth(){
        return usableFieldWidth;
    }

    /**
     * Returns the effective surface Y coordinate (ground or goal roof) for a player
     * @param player the player to evaluate
     * @return surface Y coordinate
     */
    private int getSurfaceYForPlayer(Player player){
        if(leftSoccerGoal.canPlayerStandOnTop(player)){
            return (int) Math.round(leftSoccerGoal.getPosY());
        }
        if(rightSoccerGoal.canPlayerStandOnTop(player)){
            return (int) Math.round(rightSoccerGoal.getPosY());
        }
        return groundY;
    }

    /**
     * Activates the power aura for the specified player
     * @param playerNum 0 for leftPlayer, 1 for rightPlayer
     */
    public void usePlayerPower(int playerNum){
        if(isPaused || thereIsWinner){
            return;
        }
        if(playerNum == 0){
            leftPlayer.activatePower();
        }
        else{
            rightPlayer.activatePower();
        }
    }

    /**
     * Removes any fake illusion balls whose timer has expired
     */
    private void removeExpiredFakeBalls(){
        for(int i = balls.size() - 1; i >= 0; i--){
            if(balls.get(i).isExpiredFake()){
                balls.remove(i);
            }
        }
    }

    /**
     * Spawns fake illusion balls for the United States power shot
     * @param realBall the real ball
     * @param dir horizontal shot direction (1 or -1)
     * @param count number of fake balls to create
     * @param duration lifetime of fake balls in seconds
     */
    public void spawnIllusionBalls(Ball realBall, int dir, int count, double duration){
        for(int i = 0; i < count; i++){
            Ball fake = new Ball(realBall.getPosX(), realBall.getPosY(), realBall.getWidth(), realBall.getHeight());
            fake.setScale(scale);
            fake.setFakeBall(duration);
            double speedX = (420.0 + (i * 45.0)) * dir * scale;
            double speedY = (-180.0 - ((i % 4) * 110.0)) * scale;
            fake.setVelX(speedX);
            fake.setVelY(speedY);
            balls.add(fake);
        }
    }

    /**
     * Moves the specified player horizontally
     * @param playerNum 0 for leftPlayer, 1 for rightPlayer
     * @param direction -1 left, 1 right, 0 stop
     */
    public void movePlayer(int playerNum, int direction){
        if(isPaused || thereIsWinner){
            return;
        }
        if(playerNum == 0){
            leftPlayer.moveHorizontal(direction);
        }
        else{
            rightPlayer.moveHorizontal(direction);
        }
    }

    /**
     * Makes the specified player jump from the ground or from the top of a goal
     * @param playerNum 0 for leftPlayer, 1 for rightPlayer
     */
    public void jumpPlayer(int playerNum){
        if(isPaused || thereIsWinner){
            return;
        }
        if(playerNum == 0){
            leftPlayer.jump(getSurfaceYForPlayer(leftPlayer));
        }
        else{
            rightPlayer.jump(getSurfaceYForPlayer(rightPlayer));
        }
    }

    /**
     * Prevents both players from overlapping horizontally using their hitboxes
     */
    private void resolvePlayerToPlayerCollision(){
        if(!leftPlayer.collidesWith(rightPlayer)){
            return;
        }
        double overlap = (leftPlayer.getHitboxX() + leftPlayer.getHitboxWidth()) - rightPlayer.getHitboxX();
        if(overlap > 0 && leftPlayer.getHitboxX() < rightPlayer.getHitboxX()){
            leftPlayer.setPosX(Math.max(minPlayerX, leftPlayer.getPosX() - overlap / 2.0));
            rightPlayer.setPosX(Math.min(maxPlayerX - rightPlayer.getWidth(), rightPlayer.getPosX() + overlap / 2.0));
        }
    }
/**
     * Updates the match physics, collisions, power shots, goals and end conditions
     * @param dt time step in seconds
     */
    public void updateEnvironment(double dt){
        if(isPaused || thereIsWinner){
            return;
        }

        if(leftPlayer.isMachine()){
            ((MachinePlayer)leftPlayer).executeMachine(this, dt);
        }

        if(rightPlayer.isMachine()){
            ((MachinePlayer)rightPlayer).executeMachine(this, dt);
        }

        if(!isGoalBased && !isSuddenDeath){
            remainingTime = Math.max(0.0, remainingTime - dt);
        }
        int leftSurfaceY = getSurfaceYForPlayer(leftPlayer);
        int rightSurfaceY = getSurfaceYForPlayer(rightPlayer);

        leftPlayer.updateState(dt, minPlayerX, maxPlayerX, leftSurfaceY, ceilingY);
        rightPlayer.updateState(dt, minPlayerX, maxPlayerX, rightSurfaceY, ceilingY);
        resolvePlayerToPlayerCollision();

        ArrayList<Ball> currentBalls = new ArrayList<>(balls);
        for(Ball ball : currentBalls){
            ball.updatePhysics(dt, 0, usableFieldWidth, groundY, ceilingY);

            Player first = leftPlayer;
            Player second = rightPlayer;
            double ballCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
            double distLeft = Math.abs((leftPlayer.getHitboxX() + leftPlayer.getHitboxWidth() / 2.0) - ballCenterX);
            double distRight = Math.abs((rightPlayer.getHitboxX() + rightPlayer.getHitboxWidth() / 2.0) - ballCenterX);
            if(distRight < distLeft){
                first = rightPlayer;
                second = leftPlayer;
            }

            boolean powerEvent = resolvePlayerBallCollision(first, ball);
            if(!powerEvent){
                resolvePlayerBallCollision(second, ball);
            }

            double prevVelX = ball.getVelX();
            double prevVelY = ball.getVelY();
            leftSoccerGoal.bounceCrossbar(ball);
            rightSoccerGoal.bounceCrossbar(ball);
            if(ball.hasActivePower() && (ball.getVelX() != prevVelX || ball.getVelY() != prevVelY)){
                ball.clearPowerShot();
            }
        }
        removeExpiredFakeBalls();
        checkGoals();
        checkEndConditions();
    }

    /**
     * Triggers a player's power shot on the given ball and applies resolution scaling
     * @param shooter player activating their power shot
     * @param ball ball being shot
     */
    private void triggerPowerShot(Player shooter, Ball ball){
        shooter.consumeAura();
        shooter.getNationality().executePowerShot(ball, shooter, this);
        ball.setVelX(ball.getVelX() * scale);
        ball.setVelY(ball.getVelY() * scale);
    }

    /**
     * Applies an active power shot's damage and status effect to the hit rival and separates the ball
     * @param target player hit by the power shot
     * @param ball ball carrying the active power
     */
    private void applyPowerShotImpact(Player target, Ball ball){
        Nationality power = ball.getActivePower();
        Player shooter = ball.getShooter();
        double incomingVelX = ball.getVelX();

        target.takeDamage(power.calculatePowerDamage(shooter));
        target.applyStatusEffect(power.createStatusEffect());
        ball.clearPowerShot(0.25);

        double pCenterX = target.getHitboxX() + target.getHitboxWidth() / 2.0;
        double bCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        int reboundDir;
        if(Math.abs(incomingVelX) > 10.0){
            reboundDir = (incomingVelX > 0) ? -1 : 1;
        }
        else{
            reboundDir = (bCenterX >= pCenterX) ? 1 : -1;
        }

        double pushX;
        if(reboundDir < 0){
            pushX = target.getHitboxX() - ball.getWidth() - (2.0 * scale);
        }
        else{
            pushX = target.getHitboxX() + target.getHitboxWidth() + (2.0 * scale);
        }
        pushX = Math.max(0.0, Math.min(usableFieldWidth - ball.getWidth(), pushX));

        ball.setPosX(pushX);
        ball.setVelX(reboundDir * 240.0 * scale);
        ball.setVelY(-200.0 * scale);
    }

    /**
     * Resolves collision between a player and a ball, including power shot triggers and hits
     * @param player the player colliding with the ball
     * @param ball the ball involved in the collision
     * @return true if a power shot launch or power shot impact occurred
     */
    private boolean resolvePlayerBallCollision(Player player, Ball ball){
        if(!player.collidesWith(ball)){
            return false;
        }

        if(ball.shouldIgnorePlayer(player)){
            return false;
        }

        if(ball.hasActivePower() && ball.getShooter() != player && !ball.isFake()){
            applyPowerShotImpact(player, ball);
            return true;
        }

        if(player.hasActiveAura() && !player.isParalyzed() && ball.canTriggerPowerShot() && player.getNationality() != null){
            triggerPowerShot(player, ball);
            return true;
        }

        double pCenterX = player.getHitboxX() + player.getHitboxWidth() / 2.0;
        double bCenterX = ball.getHitboxX() + ball.getHitboxWidth() / 2.0;
        int dir = (bCenterX >= pCenterX) ? 1 : -1;

        if(ball.getPosY() + ball.getHeight() <= player.getHitboxY() + player.getHitboxHeight() * 0.35){
            ball.setPosY(player.getHitboxY() - ball.getHeight());
            ball.setVelY(-Math.max(300.0 * scale, Math.abs(ball.getVelY()) * 0.85));
            ball.setVelX(player.getVelX() * 0.8 + (dir * 160.0 * scale));
        }
        else{
            double pushX;
            if(dir > 0){
                pushX = player.getHitboxX() + player.getHitboxWidth();
            }
            else{
                pushX = player.getHitboxX() - ball.getWidth();
            }
            ball.setPosX(pushX);
            ball.setVelX(dir * Math.max(260.0 * scale, Math.abs(player.getVelX()) * 1.25));
            if(player.isJumping()){
                ball.setVelY(-240.0 * scale);
            }
        }
        return false;
    }

    /**
     * Executes a kick for the specified player, damaging the rival or shooting the ball
     * @param playerNum 0 for leftPlayer, 1 for rightPlayer
     */
    public void kickPlayer(int playerNum){
        if(isPaused || thereIsWinner){
            return;
        }
        Player kicker = (playerNum == 0) ? leftPlayer : rightPlayer;
        Player rival = (playerNum == 0) ? rightPlayer : leftPlayer;

        if(!kicker.kick()){
            return;
        }
        if(kicker.isInKickRange(rival)){
            rival.takeDamage(kicker.getKickDamage());
        }
        int dir = kicker.isFacingRight() ? 1 : -1;

        ArrayList<Ball> currentBalls = new ArrayList<>(balls);
        for(Ball ball : currentBalls){
            if(kicker.isInKickRange(ball)){
                if(ball.hasActivePower() && !ball.isFake()){
                    if(ball.getShooter() != kicker){
                        applyPowerShotImpact(kicker, ball);
                    }
                    continue;
                }

                if(kicker.hasActiveAura() && ball.canTriggerPowerShot() && kicker.getNationality() != null){
                    triggerPowerShot(kicker, ball);
                }
                else{
                    ball.setVelX(dir * KICK_BALL_SPEED_X * scale);
                    ball.setVelY(KICK_BALL_SPEED_Y * scale);
                }
            }
        }
    }

    /**
     * Registers a goal and charges the scored-against player's power bar by 25%
     * @param scoredByLeftPlayer true if leftPlayer scored, false if rightPlayer scored
     */
    public void addGoal(boolean scoredByLeftPlayer){
        if(scoredByLeftPlayer){
            leftPlayer.addGoal();
            rightPlayer.chargePowerBar(25.0);
        }
        else{
            rightPlayer.addGoal();
            leftPlayer.chargePowerBar(25.0);
        }
    }

    /**
     * Checks if the real ball entered either goal
     */
    private void checkGoals(){
        for(Ball ball : balls){
            if(ball.isFake()){
                continue;
            }
            if(leftSoccerGoal.isInsideSoccerGoal(ball)){
                addGoal(false);
                resetPositionsToCenter();
                break;
            }
            else if(rightSoccerGoal.isInsideSoccerGoal(ball)){
                addGoal(true);
                resetPositionsToCenter();
                break;
            }
        }
    }

    /**
     * Resets both players and the real ball to fall from the center after a goal
     */
    private void resetPositionsToCenter(){
        double dropY = fieldHeight * 0.35;
        leftPlayer.resetAfterGoal(usableFieldWidth * 0.25, dropY);
        rightPlayer.resetAfterGoal((usableFieldWidth * 0.75) - rightPlayer.getWidth(), dropY);
        Ball mainBall = balls.get(0);
        for(Ball b : balls){
            if(!b.isFake()){
                mainBall = b;
                break;
            }
        }
        balls.clear();
        mainBall.resetPosition((usableFieldWidth - mainBall.getWidth()) / 2.0, fieldHeight * 0.28);
        balls.add(mainBall);
    }

    /**
     * Evaluates if the match has reached its goal target, time limit or sudden death winner
     */
    private void checkEndConditions(){
        if(thereIsWinner){
            return;
        }

        if(isGoalBased){
            if(leftPlayer.getGoals() >= targetGoals){
                finishMatch(0, leftPlayer, rightPlayer);
            }
            else if(rightPlayer.getGoals() >= targetGoals){
                finishMatch(1, rightPlayer, leftPlayer);
            }
        }
        else if(remainingTime <= 0){
            if(leftPlayer.getGoals() > rightPlayer.getGoals()){
                finishMatch(0, leftPlayer, rightPlayer);
            }
            else if(rightPlayer.getGoals() > leftPlayer.getGoals()){
                finishMatch(1, rightPlayer, leftPlayer);
            }
            else{
                isSuddenDeath = true;
            }
        }
    }

    /**
     * Marks that there is a winner, stores winnerNum and awards 100 points to winner and 25 to loser
     * @param winnerNumber 0 for leftPlayer, 1 for rightPlayer
     * @param matchWinner the winning player
     * @param matchLoser the losing player
     */
    private void finishMatch(int winnerNumber, Player matchWinner, Player matchLoser){
        thereIsWinner = true;
        winnerNum = winnerNumber;
        matchWinner.givePoints(100);
        matchLoser.givePoints(25);
    }
}