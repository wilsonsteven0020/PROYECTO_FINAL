package domain;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents DopoSoccer game.
 */
public class DopoSoccer implements Serializable{

    private ArrayList<Player> players;
    private HashMap<String, Nationality> nationalities;
    private Match currentMatch;
    private String currentMachineProfileName;

    public DopoSoccer(){
        players = new ArrayList<>();
        players.add(new Player(1,0,1,1));
        players.add(new Player(3,0,1,1));
        nationalities = new HashMap<>();
        nationalities.put("Corea del Sur", new SouthKorea());
        nationalities.put("Corea Del Sur", new SouthKorea());
        nationalities.put("Camerún", new Cameroon());
        nationalities.put("Nigeria", new Nigeria());
        nationalities.put("Estados Unidos", new UnitedStates());
        currentMachineProfileName = "";
    }

    /**
     * Saves the game
     * @param file is the file where the game will be saved.
     * @throws DopoSoccerException - SAVE_ERROR
     */
    public void saveGame(File file) throws DopoSoccerException{
        if(file == null){
            throw new DopoSoccerException(DopoSoccerException.SAVE_ERROR);
        }
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))){
            out.writeObject(this);
        }
        catch(IOException e){
            Log.record(e);
            throw new DopoSoccerException(DopoSoccerException.SAVE_ERROR);
        }
    }

    /**
     * Opens a saved game from file
     * @param file is the file where the game will be opened.
     * @return the loaded DopoSoccer instance
     * @throws DopoSoccerException - OPEN_ERROR
     */
    public static DopoSoccer openGame(File file) throws DopoSoccerException{
        if(file == null || !file.exists()){
            throw new DopoSoccerException(DopoSoccerException.OPEN_ERROR);
        }
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            Object loaded = in.readObject();
            if(!(loaded instanceof DopoSoccer)){
                throw new DopoSoccerException(DopoSoccerException.OPEN_ERROR);
            }
            return (DopoSoccer) loaded;
        }
        catch(DopoSoccerException e){
            throw e;
        }
        catch(IOException | ClassNotFoundException e){
            Log.record(e);
            throw new DopoSoccerException(DopoSoccerException.OPEN_ERROR);
        }
    }

    /**
     * Returns player's points
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @return player's points
     */
    public int getPlayerPoints(int playerNum){
        return players.get(playerNum).getPoints();
    }

    /**
     * Gives points to an specific player
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param points are the number of points that will be given
     */
    public void givePoints(int playerNum, int points){
        players.get(playerNum).givePoints(points);
    }

    /**
     * Upgrades a player's stat.
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName is the stat's name
     * @throws DopoSoccerException - NOT_ENOUGH_POINTS 
     */
    public void upgradePlayerStat(int playerNum, String statName) throws DopoSoccerException{
        players.get(playerNum).upgradeStat(statName);
    }

    /**
     * Return the stat level for an specific stat of an specific player
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName is the stat that will be consulted
     * @return the statLevel
     */
    public int getPlayerStatLevel(int playerNum, String statName){
        return players.get(playerNum).getStatLevel(statName);
    }

    /**
     * Gives the cost of an specific update for an specific player
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName if the stat's name.
     * @return the upgrade cost
     */
    public int getPlayerUpgradeCost(int playerNum, String statName){
        return players.get(playerNum).getUpgradeCost(statName);
    }

    /**
     * Creates a new Match
     * @param mode is the gameMode
     * @param p1Country is the first player country name.
     * @param p2Country is the second player country name.
     * @param machineProfile if the gameMode is pvm
     * @param isGoalBased if the match is by goals.
     * @param targetValue it represents the value that the user must put, it represents goals or time.
     * @param fieldWidth width of the field in pixels
     * @param fieldHeight height of the field in pixels
     * @throws DopoSoccerException - SLOWER_OR_EQUALS_TO_ZERO
     */
    public void startMatch(String mode, String p1Country, String p2Country, String machineProfile, boolean isGoalBased, int targetValue, int fieldWidth, int fieldHeight) throws DopoSoccerException{
        if(targetValue <= 0){
            throw new DopoSoccerException(DopoSoccerException.SLOWER_OR_EQUALS_TO_ZERO);
        }
        players.get(0).setNationality(nationalities.get(p1Country));
        players.get(1).setNationality(nationalities.get(p2Country));

        Player player1 = players.get(0);
        Player player2 = players.get(1);

        if(mode.equals("Jugador VS Máquina")){
            MachineProfile profile = createMachineProfile(machineProfile);
            player2 = new MachinePlayer(players.get(1), profile);
            currentMachineProfileName = machineProfile;
        }
        else{
            currentMachineProfileName = "";
        }

     currentMatch = new Match(player1, player2, isGoalBased, targetValue, fieldWidth, fieldHeight);
    }
    /**
     * Returns the active player in the current match, or the stored player if no match is active
     * @param playerNum 0 for left player, 1 for right player
     * @return active Player instance
     */
    private Player getMatchPlayer(int playerNum){
        if(currentMatch != null){
            if(playerNum == 0){
                return currentMatch.getLeftPlayer();
            }
            else{
                return currentMatch.getRightPlayer();
            }
        }
        return players.get(playerNum);
    }
    /**
     * Clears the current match reference when exiting or finishing a match
     */
    public void clearCurrentMatch(){
        currentMatch = null;
    }

    /**
     * Indicates if there is an active unfinished match stored
     * @return true if a match is currently in progress
     */
    public boolean hasActiveMatch(){
        return currentMatch != null && !currentMatch.isThereWinner();
    }

    /**
     * Sets the pause state of the current match
     * @param paused true to pause, false to resume
     */
    public void setCurrentMatchPaused(boolean paused){
        if(currentMatch != null){
            currentMatch.setPaused(paused);
        }
    }

    /**
     * Returns the initial duration in seconds of the current match (-1 if goal-based)
     * @return initial duration in seconds
     */
    public int getCurrentMatchInitialTime(){
        if(currentMatch == null){
            return -1;
        }
        return currentMatch.getInitialTime();
    }

    /**
     * Returns [width, height] of the window used when the current match was created
     * @return window dimensions
     */
    public int[] getCurrentMatchWindowDimensions(){
        if(currentMatch == null){
            return new int[]{1024, 576};
        }
        return new int[]{currentMatch.getWindowWidth(), currentMatch.getWindowHeight()};
    }

    /**
     * Returns the selected AI profile name for the current match
     * @return machine profile name
     */
    public String getCurrentMachineProfileName(){
        if(currentMachineProfileName == null){
            return "";
        }
        return currentMachineProfileName;
    }

    /**
     * Returns current match's remaining time
     * @return returns current match's remaining time
     */
    public int getCurrentMatchRemainingTime(){
        return currentMatch.getRemainingTime();
    }

    /**
     * Returns current players goals number
     * @return current players goals number
     */
    public int[] getPlayersGoalsNumber(){
        int[] goals = new int[2];
        goals[0] = getMatchPlayer(0).getGoals();
        goals[1] = getMatchPlayer(1).getGoals();
        return goals;
    }

    /**
     * Returns players nationalities
     * @return players nationalities
     */
    public String[] getPlayersNationalities(){
        String[] nationalities = new String[2];
        nationalities[0] = getMatchPlayer(0).getIdentificaction();
        nationalities[1] = getMatchPlayer(1).getIdentificaction();
        return nationalities;
    }

    /**
     * Gets the players power bars
     * @return players power bars
     */
    public int[] getPlayersPowerBars(){
        return new int[]{(int)getMatchPlayer(0).getPowerBar(),(int)getMatchPlayer(1).getPowerBar()};
    }

    /**
     * Gets the players current health
     * @return players current health
     */
    public int[] getPlayersCurrentHealth(){
        return new int[]{(int)getMatchPlayer(0).getCurrentHealth(),(int)getMatchPlayer(1).getCurrentHealth()};
    }

    /**
     * Gets the players max health
     * @return players max health
     */
    public int[] getPlayersMaxHealth(){
        return new int[]{(int)getMatchPlayer(0).getMaxHealth(),(int)getMatchPlayer(1).getMaxHealth()};
    }

    /**
     * Updates de current match's envorinment
     * @param dt is the time it will be updated
     */
    public void updateMatch(double dt){
        currentMatch.updateEnvironment(dt);
    }

    /**
     * Toggles pause on the current match
     */
    public void toggleMatchPause(){
        if(currentMatch != null){
            currentMatch.togglePause();
        }
    }

    /**
     * Returns if the current match is paused
     * @return true if paused
     */
    public boolean isCurrentMatchPaused(){
        return currentMatch != null && currentMatch.isPaused();
    }

    /**
     * Returns if the current match is goal based
     * @return true if goal based
     */
    public boolean isCurrentMatchGoalBased(){
        return currentMatch != null && currentMatch.isGoalBased();
    }

    /**
     * Returns missing goals to finish the current match
     * @return missing goals
     */
    public int getCurrentMatchMissingGoals(){
        return currentMatch.getMissingGoals();
    }

    public void movePlayer(int playerNum, int direction){
        if(currentMatch != null){
            currentMatch.movePlayer(playerNum, direction);
        }
    }

    public void jumpPlayer(int playerNum){
        if(currentMatch != null){
            currentMatch.jumpPlayer(playerNum);
        }
    }

    public void kickPlayer(int playerNum){
        if(currentMatch != null){
            currentMatch.kickPlayer(playerNum);
        }
    }

    public void usePlayerPower(int playerNum){
        if(currentMatch != null){
            currentMatch.usePlayerPower(playerNum);
        }
    }

    /**
     * Returns [x, y, width, height] for the requested player
     */
    public int[] getPlayerBounds(int playerNum){
        Player p = getMatchPlayer(playerNum);
        return new int[]{
            (int) Math.round(p.getPosX()),
            (int) Math.round(p.getPosY()),
            p.getWidth(),
            p.getHeight()
        };
    }

    /**
     * Returns [x, y, width, height] for the main ball
     */
    public int[] getBallBounds(){
        if(currentMatch == null || currentMatch.getBalls().isEmpty()){
            return new int[]{0, 0, 36, 36};
        }
        Ball b = currentMatch.getBalls().get(0);
        return new int[]{
            (int) Math.round(b.getPosX()),
            (int) Math.round(b.getPosY()),
            b.getWidth(),
            b.getHeight()
        };
    }

    /**
     * Indicates if the current match already has a winner
     * @return true if there is a winner
     */
    public boolean isCurrentMatchFinished(){
        return currentMatch != null && currentMatch.isThereWinner();
    }

    /**
     * Indicates if the current match is in sudden death
     * @return true if sudden death is active
     */
    public boolean isCurrentMatchSuddenDeath(){
        return currentMatch != null && currentMatch.isSuddenDeath();
    }

    /**
     * Returns the winner player index (0 or 1) of the current match
     * @return winner player number or -1
     */
    public int getCurrentMatchWinner(){
        if(currentMatch != null){
            return currentMatch.getWinnerNum();
        }
        else{
            return -1;
        }
    }    
    /**
     * Returns the current sprite state suffix for both players
     */
    public String[] getPlayersSpriteStates(){
        return new String[]{
            getMatchPlayer(0).getSpriteState(),
            getMatchPlayer(1).getSpriteState()
        };
    }

    /**
     * Returns a list of [x, y, width, height] arrays for all active balls in the match
     * @return list of ball bounds
     */
    public ArrayList<int[]> getAllBallsBounds(){
        ArrayList<int[]> allBounds = new ArrayList<>();
        if(currentMatch == null){
            return allBounds;
        }
        for(Ball b : currentMatch.getBalls()){
            allBounds.add(new int[]{
                (int) Math.round(b.getPosX()),
                (int) Math.round(b.getPosY()),
                b.getWidth(),
                b.getHeight()
            });
        }
        return allBounds;
    }

    /**
     * Returns a list of skin names for all active balls in the match
     * @return list of ball skin names aligned with getAllBallsBounds
     */
    public ArrayList<String> getAllBallsSkins(){
        ArrayList<String> allSkins = new ArrayList<>();
        if(currentMatch == null){
            return allSkins;
        }
        for(Ball b : currentMatch.getBalls()){
            allSkins.add(b.getSkinName());
        }
        return allSkins;
    }

/**
     * Creates a MachineProfile depending on the selected name
     * @param profileName is the selected profile
     * @return the MachineProfile
     */
    private MachineProfile createMachineProfile(String profileName){
        if(profileName == null){
            return new ClumsyProfile();
        }
        if(profileName.equals("Ofensiva")){
            return new OffensiveProfile();
        }
        else if(profileName.equals("Defensiva")){
            return new DefensiveProfile();
        }
        else if(profileName.equals("Experta")){
            return new ExpertProfile();
        }
        else{
            return new ClumsyProfile();
        }
    }

    /**
     * Indicates if the right player in the current match is controlled by AI
     * @return true if right player is a MachinePlayer
     */
    public boolean isRightPlayerMachine(){
        if(currentMatch == null){
            return false;
        }
        return currentMatch.getRightPlayer().isMachine();
    }

}