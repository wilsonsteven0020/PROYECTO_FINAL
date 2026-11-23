package test;

import domain.DopoSoccer;
import domain.DopoSoccerException;


import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;


public class DopoSoccerTest {
    private DopoSoccer dopoSoccer;
    @Before
    public void setUp(){
        dopoSoccer = new DopoSoccer();
    }

    /**
     * Should players points start with zero. 
     */
    @Test
    public void shouldPlayerPointsStartWithZero(){
        assertEquals(0, dopoSoccer.getPlayerPoints(0));
        assertEquals(0, dopoSoccer.getPlayerPoints(1));
    }

    /**
     * Should players points be greater if you give them points
     */
    @Test
    public void shouldPlayerPointsBeGreaterIfPointsAreGiven(){
        dopoSoccer.givePoints(0,500);
        dopoSoccer.givePoints(1,1000);
        assertEquals(500, dopoSoccer.getPlayerPoints(0));
        assertEquals(1000, dopoSoccer.getPlayerPoints(1));    
    }

    /**
     * Should stat be upgraded if you update different stats for both players
     */
    @Test
    public void shouldStatBeUpgradedIfPlayersHaveStatUpdates(){
        dopoSoccer.givePoints(0,500);
        dopoSoccer.givePoints(1,1000);
        try{
            assertEquals(500, dopoSoccer.getPlayerUpgradeCost(0, "Speed"));
            dopoSoccer.upgradePlayerStat(0,"Speed");
            dopoSoccer.getPlayerStatLevel(0, "Speed");
            assertEquals(0, dopoSoccer.getPlayerPoints(0));
            
            assertEquals(500, dopoSoccer.getPlayerUpgradeCost(1, "Kick"));
            dopoSoccer.upgradePlayerStat(1,"Kick");
            dopoSoccer.getPlayerStatLevel(1, "Kick");
            assertEquals(500, dopoSoccer.getPlayerPoints(1));
        }
        catch(DopoSoccerException e){
            fail("It throw an exception");
        }
    }

    /**
     * Should throw an exception if there's no enough points to buy an update.
     */
    @Test
    public void shouldThrowAnExceptionIfThereIsNoEnoughPointsForAnUpdate(){
        try{
            dopoSoccer.upgradePlayerStat(0,"Speed");
            fail("It didn't throw an exception");
        }
        catch(DopoSoccerException e){
            assertEquals(DopoSoccerException.NOT_ENOUGH_POINTS,e.getMessage());
        }
    }

    /**
     * Should match be created correctly in both modes.
     */
    @Test
    public void shouldMatchBeCreatedCorrectlyInBothModes(){
        try{
            dopoSoccer.startMatch("Por tiempo", "Corea del Sur", "Camerún", "", true, 1, 1920, 1080);
            dopoSoccer.startMatch("Por goles", "Nigeria", "Estados Unidos", "", false, 5, 1024, 576);
        }
        catch(DopoSoccerException e){
            fail("It throw an exception");
        }
    }

    /**
     * Should not create a match if target value (the value that will have the time or targetGoals) is less or equals to zero
     */
    @Test
    public void shouldNotCreateAMatchIfTargetValueIsZeroOrLess(){
        try{
            dopoSoccer.startMatch("Por tiempo", "Corea del Sur", "Camerún", "", true, 0, 1920, 1080);
            fail("It didn't throw an exception");
        }
        catch(DopoSoccerException e){
            assertEquals(DopoSoccerException.SLOWER_OR_EQUALS_TO_ZERO, e.getMessage());
        }
    }

    /**
     * Should give correct remaining time, player goals and nationalities if match has started.
     */
    @Test
    public void shouldGiveCorrectInitialConditionsIfMatchHasStarted(){
        try{
            dopoSoccer.startMatch("Por tiempo", "Corea del Sur", "Camerún", "", false, 5, 800, 450);
            assertEquals(5*60, dopoSoccer.getCurrentMatchRemainingTime());
            int[] currentGoals = dopoSoccer.getPlayersGoalsNumber();
            assertEquals(0, currentGoals[0]);
            assertEquals(0, currentGoals[1]);
            String[] currentNationalities = dopoSoccer.getPlayersNationalities();
            assertEquals("Corea del Sur", currentNationalities[0]);
            assertEquals("Camerún", currentNationalities[1]);
            int[] currentStartPowerBars = dopoSoccer.getPlayersPowerBars();
            assertEquals(0, currentStartPowerBars[0]);
            assertEquals(0, currentStartPowerBars[1]);
            int [] currentHealth = dopoSoccer.getPlayersCurrentHealth();
            int [] maxHealth = dopoSoccer.getPlayersMaxHealth();
            assertEquals(maxHealth[0], currentHealth[0]);
            assertEquals(maxHealth[1], currentHealth[1]);
        }
        catch(DopoSoccerException e){
            fail("It throw an exception");
        }
    }

    /**
     * Should verifies that upgrading a stat deducts points and doubles the next upgrade cost
     */
    @Test
    public void shouldUpgradeStatAndDoubleCostWhenEnoughPoints() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.givePoints(0, 2000);

        assertEquals(500, game.getPlayerUpgradeCost(0, "Kick"));
        game.upgradePlayerStat(0, "Kick");

        assertEquals(2, game.getPlayerStatLevel(0, "Kick"));
        assertEquals(1500, game.getPlayerPoints(0));
        assertEquals(1000, game.getPlayerUpgradeCost(0, "Kick"));

        game.upgradePlayerStat(0, "Kick");
        assertEquals(3, game.getPlayerStatLevel(0, "Kick"));
        assertEquals(500, game.getPlayerPoints(0));
        assertEquals(2000, game.getPlayerUpgradeCost(0, "Kick"));
    }

    /**
     * Should verifies that upgrading a stat without enough points throws DopoSoccerException
     */
    @Test
    public void shouldThrowExceptionWhenUpgradingWithoutEnoughPoints() {
        DopoSoccer game = new DopoSoccer();
        game.givePoints(0, 300);

        assertThrows(DopoSoccerException.class, () -> {
            game.upgradePlayerStat(0, "Speed");
        });
        assertEquals(1, game.getPlayerStatLevel(0, "Speed"));
        assertEquals(300, game.getPlayerPoints(0));
    }

    /**
     * Should verifies that starting a match with zero or negative target value throws DopoSoccerException
     */
    @Test
    public void shouldThrowExceptionWhenStartingMatchWithInvalidTargetValue() {
        DopoSoccer game = new DopoSoccer();

        assertThrows(DopoSoccerException.class, () -> {
            game.startMatch("Jugador VS Jugador", "Corea del Sur", "Camerún", "", true, 0, 1024, 576);
        });
        assertThrows(DopoSoccerException.class, () -> {
            game.startMatch("Jugador VS Jugador", "Corea del Sur", "Camerún", "", false, -2, 1920, 1080);
        });
    }

    /**
     * Should verifies that pausing a match stops environment updates until resumed
     */
    @Test
    public void shouldNotUpdateMatchTimeOrPowerBarsWhilePaused() throws DopoSoccerException {
        DopoSoccer game = new DopoSoccer();
        game.startMatch("Jugador VS Jugador", "Nigeria", "Estados Unidos", "", false, 2, 1024, 576);

        game.toggleMatchPause();
        assertTrue(game.isCurrentMatchPaused());

        int initialTime = game.getCurrentMatchRemainingTime();
        game.updateMatch(5.0);

        assertEquals(initialTime, game.getCurrentMatchRemainingTime());
        assertEquals(0, game.getPlayersPowerBars()[0]);
    }

    /**
     * Should player manage the stats and costs
     */
    @Test
    public void shouldManagePlayerStatsAndCostsThroughFacade() {
        DopoSoccer game = new DopoSoccer();
        assertEquals(1, game.getPlayerStatLevel(0, "Speed"));
        assertEquals(500, game.getPlayerUpgradeCost(0, "Speed"));
        game.givePoints(0, 600);
        try {
            game.upgradePlayerStat(0, "Speed");
            assertEquals(2, game.getPlayerStatLevel(0, "Speed"));
        } catch (DopoSoccerException e) {
            fail("Should not throw exception with enough points");
        }
    }

    /**
     * Should match start with differents ScreenSize
     */
    @Test
    public void shouldStartDifferentScreenSizeMatches() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startMatch("PvP", "Corea del Sur", "Camerún", "", true, 5, 1920, 1080);
            assertEquals(5, game.getCurrentMatchMissingGoals());
            assertFalse(game.isCurrentMatchPaused());
            game.startMatch("PvP", "Nigeria", "Estados Unidos", "", false, 2, 800, 450);
            assertEquals(120, game.getCurrentMatchRemainingTime());
        } catch (DopoSoccerException e) {
            fail("Match start failed: " + e.getMessage());
        }
    }

    /**
     * Should match handle controls.
     */
    @Test
    public void shouldHandleMatchControlsAndGetters() {
        try {
            DopoSoccer game = new DopoSoccer();
            game.startMatch("PvP", "Corea del Sur", "Nigeria", "", true, 3, 1024, 576);
            
            assertFalse(game.isCurrentMatchPaused());
            game.toggleMatchPause();
            assertTrue(game.isCurrentMatchPaused());
            game.toggleMatchPause(); 

            String[] nationalities = game.getPlayersNationalities();
            assertNotNull(nationalities);
            assertEquals(2, nationalities.length);

            int[] powerBars = game.getPlayersPowerBars();
            assertEquals(2, powerBars.length);

            int[] healths = game.getPlayersCurrentHealth();
            assertEquals(2, healths.length);

            int[] maxHealths = game.getPlayersMaxHealth();
            assertEquals(2, maxHealths.length);

            int[] goals = game.getPlayersGoalsNumber();
            assertEquals(2, goals.length);

        
            game.movePlayer(0, 1);
            game.jumpPlayer(0);
            game.kickPlayer(0);
            game.usePlayerPower(0);


            assertNotNull(game.getPlayerBounds(0));
            assertNotNull(game.getBallBounds());
            assertFalse(game.getAllBallsBounds().isEmpty());
            assertNotNull(game.getPlayersSpriteStates());

            assertFalse(game.isCurrentMatchFinished());
            assertFalse(game.isCurrentMatchSuddenDeath());
            assertEquals(-1, game.getCurrentMatchWinner());

        } catch (DopoSoccerException e) {
            fail("Exception thrown in match controls test: " + e.getMessage());
        }
    }   

    /**
     * Should start Player vs Machine matches with every machine profile and run updates
     */
    @Test
    public void shouldStartMachineMatchesWithEveryProfile() {
        String[] profiles = {"Ofensiva", "Defensiva", "Experta", "Torpe", null};
        try {
            for (String profile : profiles) {
                dopoSoccer.startMatch("Jugador VS Máquina", "Corea del Sur", "Camerún", profile, true, 3, 1024, 576);
                assertTrue(dopoSoccer.isRightPlayerMachine());
                for (int i = 0; i < 15; i++) {
                    dopoSoccer.updateMatch(0.1);
                }
            }
            dopoSoccer.startMatch("Jugador VS Jugador", "Nigeria", "Estados Unidos", "", true, 3, 1024, 576);
            assertFalse(dopoSoccer.isRightPlayerMachine());
        } catch (DopoSoccerException e) {
            fail("It throw an exception: " + e.getMessage());
        }
    }

    /**
     * Should activate sudden death when a time-based match ends in a tie
     */
    @Test
    public void shouldActivateSuddenDeathWhenTimeEndsInTie() {
        try {
            dopoSoccer.startMatch("Jugador VS Jugador", "Corea del Sur", "Camerún", "", false, 1, 1024, 576);
            dopoSoccer.updateMatch(61.0);
            assertTrue(dopoSoccer.isCurrentMatchSuddenDeath());
            assertFalse(dopoSoccer.isCurrentMatchFinished());
        } catch (DopoSoccerException e) {
            fail("It throw an exception: " + e.getMessage());
        }
    }

    /**
     * Should save and open a game preserving player points and match state
     */
    @Test
    public void shouldSaveAndOpenGameCorrectly() {
        File tempFile = new File("testDopoSoccerSave.dat");
        try {
            dopoSoccer.givePoints(0, 750);
            dopoSoccer.startMatch("Jugador VS Máquina", "Corea del Sur", "Nigeria", "Ofensiva", true, 3, 1024, 576);
            dopoSoccer.saveGame(tempFile);
            assertTrue(tempFile.exists());

            DopoSoccer loaded = DopoSoccer.openGame(tempFile);
            assertNotNull(loaded);
            assertEquals(750, loaded.getPlayerPoints(0));
            assertTrue(loaded.isRightPlayerMachine());
            assertEquals("Corea del Sur", loaded.getPlayersNationalities()[0]);
            assertEquals("Nigeria", loaded.getPlayersNationalities()[1]);
        } catch (DopoSoccerException e) {
            fail("It throw an exception: " + e.getMessage());
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    /**
     * Should throw exceptions and record log when saving or opening invalid files
     */
    @Test
    public void shouldNotSaveOrOpenInvalidFiles() {
        assertThrows(DopoSoccerException.class, () -> {
            dopoSoccer.saveGame(null);
        });
        assertThrows(DopoSoccerException.class, () -> {
            DopoSoccer.openGame(null);
        });
        assertThrows(DopoSoccerException.class, () -> {
            DopoSoccer.openGame(new File("nonExistentDopoSoccerFile.dat"));
        });

        File corruptFile = new File("testCorruptDopoSoccer.dat");
        try {
            java.io.FileWriter writer = new java.io.FileWriter(corruptFile);
            writer.write("not a serialized object");
            writer.close();
            DopoSoccer.openGame(corruptFile);
            fail("It didn't throw an exception");
        } catch (DopoSoccerException e) {
            assertEquals(DopoSoccerException.OPEN_ERROR, e.getMessage());
        } catch (java.io.IOException e) {
            fail("Could not prepare corrupt test file");
        } finally {
            if (corruptFile.exists()) {
                corruptFile.delete();
            }
        }
    }

    /**
     * Should manage active match lifecycle, metadata getters and clearCurrentMatch
     */
    @Test
    public void shouldManageMatchLifecycleAndMetadataThroughFacade() throws DopoSoccerException {
        assertFalse(dopoSoccer.hasActiveMatch());
        assertEquals(-1, dopoSoccer.getCurrentMatchInitialTime());
        assertEquals(1024, dopoSoccer.getCurrentMatchWindowDimensions()[0]);
        assertTrue(dopoSoccer.getAllBallsSkins().isEmpty());
        assertTrue(dopoSoccer.getAllBallsBounds().isEmpty());
        assertFalse(dopoSoccer.isRightPlayerMachine());

        dopoSoccer.startMatch("Jugador VS Máquina", "Corea del Sur", "Camerún", "Experta", false, 2, 1280, 720);
        assertTrue(dopoSoccer.hasActiveMatch());
        assertFalse(dopoSoccer.isCurrentMatchGoalBased());
        assertEquals(120, dopoSoccer.getCurrentMatchInitialTime());
        assertEquals(1280, dopoSoccer.getCurrentMatchWindowDimensions()[0]);
        assertEquals(720, dopoSoccer.getCurrentMatchWindowDimensions()[1]);
        assertEquals("Experta", dopoSoccer.getCurrentMachineProfileName());
        assertFalse(dopoSoccer.getAllBallsSkins().isEmpty());

        dopoSoccer.setCurrentMatchPaused(true);
        assertTrue(dopoSoccer.isCurrentMatchPaused());

        dopoSoccer.clearCurrentMatch();
        assertFalse(dopoSoccer.hasActiveMatch());
        assertEquals(-1, dopoSoccer.getCurrentMatchWinner());
    }
}