package presentation;

import domain.DopoSoccer;
import domain.DopoSoccerException;
import domain.Log;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.Map;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Represents DopoSoccer's Graphical User Interface.
 */
public class DopoSoccerGUI extends JFrame{

    private DopoSoccer dopoSoccer;

    //Dimension on screen
    private static final Dimension PREFERRED_DIMENSION_MEDIUM = new Dimension(1024, 576);
    private static final Dimension PREFERRED_DIMENSION_SMALL = new Dimension(800, 450);
    private String screenSize;

    //View JPanels
    private MenuPanel menuPanel;
    private SelectionGameModePanel selectionGameModePanel;
    private SelectCharacterPanel selectCharacterPanel;
    private PlaySoccerPanel playSoccerPanel;
    private StorePanel storePanel;
    //JPlayerStoresPanels
    private PlayerStorePanel firstPlayerStorePanel;
    private PlayerStorePanel secondPlayerStorePanel;

    //Controls
    private ControlsDialog controlsD;

    //Game Timer
    private Timer matchTimer;

    private boolean p1LeftPressed;
    private boolean p1RightPressed;
    private boolean p2LeftPressed;
    private boolean p2RightPressed;

    /**
     * Represents DopoSoccerGUI's constructor
     */
    public DopoSoccerGUI(){
        dopoSoccer = new DopoSoccer();
        prepareView();
        prepareController();
    }

    private void prepareView(){
        setSize(PREFERRED_DIMENSION_MEDIUM);
        setTitle("DOPOSoccer");
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        screenSize = "Mediana";
        menuPanel = new MenuPanel();
        selectionGameModePanel = new SelectionGameModePanel();
        selectCharacterPanel = new SelectCharacterPanel();
        playSoccerPanel = new PlaySoccerPanel();
        storePanel = new StorePanel();
        controlsD = new ControlsDialog(this);

        firstPlayerStorePanel = storePanel.getFirstPlayerStorePanel();
        firstPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(0));
        secondPlayerStorePanel = storePanel.getSecondPlayerStorePanel();
        secondPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(1));
        add(menuPanel);
    }

    private void prepareController(){
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                exitAction();
            }
        });

        prepareControllerSelectionGameModePanel();
        prepareControllerMenuPanel();
        prepareControllerSelectCharacterPanel();
        prepareFirstPlayerStorePanelAction();
        prepareSecondPlayerStorePanelAction();
        prepareControllerPlaySoccerPanel();
    }

    private void prepareControllerPlaySoccerPanel(){
        playSoccerPanel.getPauseButton().addActionListener(e -> pauseMatchAction());
        playSoccerPanel.getPausePanel().getResumeButton().addActionListener(e -> pauseMatchAction());
        playSoccerPanel.getPausePanel().getControlsButton().addActionListener(e -> showControlsAction());
        playSoccerPanel.getPausePanel().getSaveButton().addActionListener(e -> saveAction());
        playSoccerPanel.getPausePanel().getOpenButton().addActionListener(e -> openAction());
        playSoccerPanel.getPausePanel().getExitButton().addActionListener(e -> exitMatchAction());
        playSoccerPanel.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                handleKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e){
                handleKeyReleased(e);
            }
        });

        matchTimer = new Timer(20, e -> updateMatchAction());
    }

    private void handleKeyPressed(KeyEvent e){
        if(playSoccerPanel.isInAnimation()){
            return;
        }
        int code = e.getKeyCode();

        // First player controls:
        if(code == KeyEvent.VK_A){
            p1LeftPressed = true;
            dopoSoccer.movePlayer(0, -1);
        }
        else if(code == KeyEvent.VK_D){
            p1RightPressed = true;
            dopoSoccer.movePlayer(0, 1);
        }
        else if(code == KeyEvent.VK_W){
            dopoSoccer.jumpPlayer(0);
        }
        else if(code == KeyEvent.VK_S){
            dopoSoccer.kickPlayer(0);
        }
        else if(code == KeyEvent.VK_Q){
            dopoSoccer.usePlayerPower(0);
        }

        // Second player controls:
        if(!dopoSoccer.isRightPlayerMachine()){
            if(code == KeyEvent.VK_LEFT){
                p2LeftPressed = true;
                dopoSoccer.movePlayer(1, -1);
            }
            else if(code == KeyEvent.VK_RIGHT){
                p2RightPressed = true;
                dopoSoccer.movePlayer(1, 1);
            }
            else if(code == KeyEvent.VK_UP){
                dopoSoccer.jumpPlayer(1);
            }
            else if(code == KeyEvent.VK_DOWN){
                dopoSoccer.kickPlayer(1);
            }
            else if(code == KeyEvent.VK_CONTROL){
                dopoSoccer.usePlayerPower(1);
            }
        }
    }

    private void handleKeyReleased(KeyEvent e){
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_A){
            p1LeftPressed = false;
            if(p1RightPressed){
                dopoSoccer.movePlayer(0, 1);
            }
            else{
                dopoSoccer.movePlayer(0, 0);
            }
        }
        else if(code == KeyEvent.VK_D){
            p1RightPressed = false;
            if(p1LeftPressed){
                dopoSoccer.movePlayer(0, -1);
            }
            else{
                dopoSoccer.movePlayer(0, 0);
            }
        }
        else if(!dopoSoccer.isRightPlayerMachine()){
            if(code == KeyEvent.VK_LEFT){
                p2LeftPressed = false;
                if(p2RightPressed){
                    dopoSoccer.movePlayer(1, 1);
                }
                else{
                    dopoSoccer.movePlayer(1, 0);
                }
            }
            else if(code == KeyEvent.VK_RIGHT){
                p2RightPressed = false;
                if(p2LeftPressed){
                    dopoSoccer.movePlayer(1, -1);
                }
                else{
                    dopoSoccer.movePlayer(1, 0);
                }
            }
        }
    }

    private void pauseMatchAction(){
        if(playSoccerPanel.isInAnimation()){
            return;
        }
        dopoSoccer.toggleMatchPause();
        boolean paused = dopoSoccer.isCurrentMatchPaused();
        if(paused){
            matchTimer.stop();
        }
        else{
            matchTimer.start();
        }
        playSoccerPanel.showPauseMenu(paused);
    }

    private void exitMatchAction(){
        matchTimer.stop();
        playSoccerPanel.resetMatchAnimations(-1);
        playSoccerPanel.showPauseMenu(false);
        dopoSoccer.clearCurrentMatch();
        changeToSelectCharacterAction();
    }

    private void updateMatchAction(){
        if(playSoccerPanel.isInAnimation()){
            return;
        }
        dopoSoccer.updateMatch(0.02);
        refreshPlaySoccerPanel();

        if(!dopoSoccer.isCurrentMatchFinished() && !dopoSoccer.isCurrentMatchGoalBased() && !dopoSoccer.isCurrentMatchSuddenDeath()){
            boolean triggered = playSoccerPanel.checkAndTriggerHalfTime(
                dopoSoccer.getCurrentMatchRemainingTime(),
                () -> {
                    refreshPlaySoccerPanel();
                    matchTimer.start();
                    playSoccerPanel.requestFocusInWindow();
                }
            );
            if(triggered){
                matchTimer.stop();
                p1LeftPressed = false;
                p1RightPressed = false;
                p2LeftPressed = false;
                p2RightPressed = false;
                dopoSoccer.movePlayer(0, 0);
                dopoSoccer.movePlayer(1, 0);
                return;
            }
        }

        if(dopoSoccer.isCurrentMatchFinished()){
            matchTimer.stop();
            int winnerNum = dopoSoccer.getCurrentMatchWinner();
            firstPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(0));
            secondPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(1));

            if(dopoSoccer.isRightPlayerMachine()){
                String message;
                if(winnerNum == 0){
                    message = "¡Ganó el Jugador 1!\nJugador 1: +100 puntos (Total: " + getPlayerPoints(0) + ")";
                }
                else{
                    message = "¡Ganó la Máquina (" + dopoSoccer.getCurrentMachineProfileName() + ")!\nJugador 1: +25 puntos (Total: " + getPlayerPoints(0) + ")";
                }
                createJOptionPaneMessage(message, "Fin del Partido", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                int loserNum;
                if(winnerNum == 0){
                    loserNum = 1;
                }
                else{
                    loserNum = 0;
                }
                createJOptionPaneMessage(
                    "¡Ganó el Jugador " + (winnerNum + 1) + "!\n" +
                    "Jugador " + (winnerNum + 1) + ": +100 puntos (Total: " + getPlayerPoints(winnerNum) + ")\n" +
                    "Jugador " + (loserNum + 1) + ": +25 puntos (Total: " + getPlayerPoints(loserNum) + ")",
                    "Fin del Partido",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            dopoSoccer.clearCurrentMatch();
            refreshStorePanels();
            changeToSelectCharacterAction();
        }
    }

    private void refreshPlaySoccerPanel(){
        playSoccerPanel.updateBars(
            dopoSoccer.getPlayersCurrentHealth(),
            dopoSoccer.getPlayersMaxHealth(),
            dopoSoccer.getPlayersPowerBars()
        );
        playSoccerPanel.setBallsBounds(dopoSoccer.getAllBallsBounds());
        playSoccerPanel.setBallsSkins(dopoSoccer.getAllBallsSkins());
        playSoccerPanel.updateEntitiesAndScore(
            dopoSoccer.getPlayerBounds(0),
            dopoSoccer.getPlayerBounds(1),
            dopoSoccer.getBallBounds(),
            dopoSoccer.getPlayersSpriteStates(),
            dopoSoccer.getPlayersGoalsNumber()
        );
        if(playSoccerPanel.isInHalfTime()){
            playSoccerPanel.setMatchStatusText("MITAD DE TIEMPO");
        }
        else if(dopoSoccer.isCurrentMatchGoalBased()){
            playSoccerPanel.setMatchStatusText("Goles faltantes: " + dopoSoccer.getCurrentMatchMissingGoals());
        }
        else if(dopoSoccer.isCurrentMatchSuddenDeath()){
            playSoccerPanel.setMatchStatusText("¡MUERTE SÚBITA!");
        }
        else{
            int totalSeconds = dopoSoccer.getCurrentMatchRemainingTime();
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            playSoccerPanel.setMatchStatusText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    /**
     * Resolves the exact window width and height based on the chosen screen size.
     * For "Grande", queries the monitor dimensions via Toolkit minus taskbar insets.
     * @return target Dimension for the match window
     */
    private Dimension getSelectedDimension(){
        if(screenSize.equals("Grande")){
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
            int maxW = screen.width - insets.left - insets.right;
            int maxH = screen.height - insets.top - insets.bottom;

            int targetW = maxW;
            int targetH = (int) Math.round(targetW * 9.0 / 16.0);
            if(targetH > maxH){
                targetH = maxH;
                targetW = (int) Math.round(targetH * 16.0 / 9.0);
            }
            return new Dimension(targetW, targetH);
        }
        else if(screenSize.equals("Mediana")){
            return PREFERRED_DIMENSION_MEDIUM;
        }
        else{
            return PREFERRED_DIMENSION_SMALL;
        }
    }

    private void prepareFirstPlayerStorePanelAction(){
        firstPlayerStorePanel.getBackButton().addActionListener(e -> changeToSelectCharacterAction());
        for(Map.Entry<String, JButton> i: firstPlayerStorePanel.getBuyButtons().entrySet()){
            i.getValue().addActionListener(e -> upgradeStatAction(i.getKey(), 0, firstPlayerStorePanel));
        }
    }

    private void prepareSecondPlayerStorePanelAction(){
        secondPlayerStorePanel.getBackButton().addActionListener(e -> changeToSelectCharacterAction());
        for(Map.Entry<String, JButton> i: secondPlayerStorePanel.getBuyButtons().entrySet()){
            i.getValue().addActionListener(e -> upgradeStatAction(i.getKey(), 1, secondPlayerStorePanel));
        }
    }

    private void prepareControllerSelectCharacterPanel(){
        selectCharacterPanel.getBackButton().addActionListener(e -> changeToSelectionGameModeAction());
        selectCharacterPanel.getStoreButton().addActionListener(e -> changeToStoreAction());
        selectCharacterPanel.getPlayButton().addActionListener(e -> playSoccerAction());
    }

    private void playSoccerAction(){
        int targetValue;
        JTextField number = selectCharacterPanel.getNumberTextField();
        try{
            targetValue = Integer.parseInt(number.getText().trim());
        }
        catch(NumberFormatException e){
            createJOptionPaneMessage(number.getText() + "\nNo es un número válido");
            return;
        }

        setScreenSize(selectCharacterPanel.getSelectedFieldSize());
        boolean goalBased = selectCharacterPanel.isGoalBased();
        String country1 = selectCharacterPanel.getFirstPlayerCountryLabel().getText();
        String country2 = selectCharacterPanel.getSecondPlayerCountryLabel().getText();
        String mode = selectionGameModePanel.getSelectedMode();
        String machineProfile = selectionGameModePanel.getSelectedMachineProfile();

        changeToPlaySoccerAction(mode, machineProfile, targetValue, goalBased, country1, country2);
    }

    private void prepareControllerSelectionGameModePanel(){
        selectionGameModePanel.getBackButton().addActionListener(e -> backFromSelectionGameModeAction());
        selectionGameModePanel.getContinueButton().addActionListener(e -> continueFromSelectionGameModeAction());
    }

    private void continueFromSelectionGameModeAction(){
        if(selectionGameModePanel.isShowingMachines()){
            changeToSelectCharacterAction();
        }
        else if("Jugador VS Máquina".equals(selectionGameModePanel.getSelectedMode())){
            selectionGameModePanel.showMachineProfiles();
        }
        else{
            selectionGameModePanel.setSelectedMachineProfile("");
            changeToSelectCharacterAction();
        }
    }

    private void backFromSelectionGameModeAction(){
        if(selectionGameModePanel.isShowingMachines()){
            selectionGameModePanel.showGameModes();
        }
        else{
            changeToMenuPanelAction();
        }
    }

    private void createJOptionPaneMessage(String message, String title, int option){
        JOptionPane.showMessageDialog(this, message, title, option);
    }

    private void createJOptionPaneMessage(String message){
        JOptionPane.showMessageDialog(this, message);
    }


private void prepareControllerMenuPanel(){
        menuPanel.getStartButton().addActionListener(e -> changeToSelectionGameModeAction());
        menuPanel.getOpenButton().addActionListener(e -> openAction());
        menuPanel.getSaveButton().addActionListener(e -> saveAction());
        menuPanel.getExitButton().addActionListener(e -> exitAction());
        menuPanel.getControlsButton().addActionListener(e -> showControlsAction());
    }

    private void exitAction(){
        int option = JOptionPane.showConfirmDialog(this, "¿Deseas salir?", "Salir", JOptionPane.YES_NO_OPTION);
        if(option == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }

    private void showControlsAction(){
        controlsD.setVisible(true);
    }

    private void openAction(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Partidas de DOPOSoccer (*.dat)", "dat"));
        int result = chooser.showOpenDialog(this);
        if(result != JFileChooser.APPROVE_OPTION){
            return;
        }
        File file = chooser.getSelectedFile();
        if(file == null){
            return;
        }
        try{
            matchTimer.stop();
            dopoSoccer = DopoSoccer.openGame(file);
            refreshStorePanels();
            if(dopoSoccer.hasActiveMatch()){
                resumeLoadedMatchAction();
            }
            else{
                playSoccerPanel.resetMatchAnimations(-1);
                playSoccerPanel.showPauseMenu(false);
                changeToMenuPanelAction();
                createJOptionPaneMessage(
                    "Progreso cargado exitosamente.\nJugador 1: " + getPlayerPoints(0) + " puntos\nJugador 2: " + getPlayerPoints(1) + " puntos",
                    "Abrir",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
        catch(DopoSoccerException e){
            createJOptionPaneMessage(e.getMessage(), "Abrir", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e){
            Log.record(e);
            createJOptionPaneMessage("Ocurrió un error inesperado al abrir el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAction(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Partidas de DOPOSoccer (*.dat)", "dat"));
        int result = chooser.showSaveDialog(this);
        if(result != JFileChooser.APPROVE_OPTION){
            return;
        }
        File file = chooser.getSelectedFile();
        if(file == null){
            return;
        }
        if(!file.getName().toLowerCase().endsWith(".dat")){
            file = new File(file.getAbsolutePath() + ".dat");
        }
        try{
            dopoSoccer.saveGame(file);
            createJOptionPaneMessage("Partida guardada exitosamente.", "Guardar", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(DopoSoccerException e){
            createJOptionPaneMessage(e.getMessage(), "Guardar", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception e){
            Log.record(e);
            createJOptionPaneMessage("Ocurrió un error inesperado al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resumeLoadedMatchAction(){
        dopoSoccer.setCurrentMatchPaused(true);
        int[] dims = dopoSoccer.getCurrentMatchWindowDimensions();
        Dimension matchDimension = new Dimension(dims[0], dims[1]);

        getContentPane().removeAll();
        getContentPane().add(playSoccerPanel);
        setSize(matchDimension);
        setLocationRelativeTo(null);
        revalidate();
        repaint();

        String[] countries = dopoSoccer.getPlayersNationalities();
        playSoccerPanel.restoreLoadedMatchState(
            dopoSoccer.getCurrentMatchInitialTime(),
            dopoSoccer.getCurrentMatchRemainingTime()
        );
        playSoccerPanel.configureInitialBars(
            countries,
            dopoSoccer.getPlayersMaxHealth()
        );
        playSoccerPanel.applyScale(getHeight());
        refreshPlaySoccerPanel();

        p1LeftPressed = false;
        p1RightPressed = false;
        p2LeftPressed = false;
        p2RightPressed = false;

        playSoccerPanel.showPauseMenu(true);
    }
    /**
     * Changes to menu
     */
    private void changeToMenuPanelAction(){
        getContentPane().removeAll();
        getContentPane().add(menuPanel);
        revalidate();
        repaint();
        setSize(PREFERRED_DIMENSION_MEDIUM);
        setLocationRelativeTo(null);
    }

    /**
     * Changes to SelectionGameMode
     */
    private void changeToSelectionGameModeAction(){
        selectionGameModePanel.showGameModes();
        getContentPane().removeAll();
        getContentPane().add(selectionGameModePanel);
        revalidate();
        repaint();
        setSize(PREFERRED_DIMENSION_MEDIUM);
        setLocationRelativeTo(null);
    }
    /**
     * Changes to SelectCharacter
     */
    private void changeToSelectCharacterAction(){
        getContentPane().removeAll();
        getContentPane().add(selectCharacterPanel);
        revalidate();
        repaint();
        setSize(PREFERRED_DIMENSION_MEDIUM);
        setLocationRelativeTo(null);
    }

    private void upgradeStatAction(String stat, int playerNumber, PlayerStorePanel playerStorePanel){
        if(upgradePlayerStat(playerNumber, stat)){
            refreshStorePanels();
        }
    }

    private void refreshStorePanels(){
        String[] stats = {"Speed", "Jump", "Kick", "Survival"};
        firstPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(0));
        secondPlayerStorePanel.setPointsText("Puntos: " + getPlayerPoints(1));
        for(String stat : stats){
            firstPlayerStorePanel.refreshStat(
                stat,
                getStatLevel(0, stat),
                getPlayerUpgradeCost(0, stat)
            );
            secondPlayerStorePanel.refreshStat(
                stat,
                getStatLevel(1, stat),
                getPlayerUpgradeCost(1, stat)
            );
        }
    }

    private void changeToStoreAction(){
        refreshStorePanels();
        getContentPane().removeAll();
        getContentPane().add(storePanel);
        revalidate();
        repaint();
        setSize(PREFERRED_DIMENSION_MEDIUM);
        setLocationRelativeTo(null);
    }

    /**
     * Changes to playSoccer
     */
    private void changeToPlaySoccerAction(String mode, String machineProfile, int targetValue, boolean goalBased, String country1, String country2){
        Dimension matchDimension = getSelectedDimension();
        try{
            dopoSoccer.startMatch(
                mode,
                country1,
                country2,
                machineProfile,
                goalBased,
                targetValue,
                matchDimension.width,
                matchDimension.height
            );
        }
        catch(DopoSoccerException e){
            createJOptionPaneMessage(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        getContentPane().removeAll();
        getContentPane().add(playSoccerPanel);
        setSize(matchDimension);
        if(screenSize.equals("Grande")){
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int usableW = screen.width - insets.left - insets.right;
            int usableH = screen.height - insets.top - insets.bottom;
            int x = insets.left + (usableW - matchDimension.width) / 2;
            int y = insets.top + (usableH - matchDimension.height) / 2;
            setLocation(x, y);
        }
        else{
            setLocationRelativeTo(null);
        }
        revalidate();
        repaint();

        int initialTime;
        if(dopoSoccer.isCurrentMatchGoalBased()){
            initialTime = -1;
        }
        else{
            initialTime = dopoSoccer.getCurrentMatchRemainingTime();
        }
        String[] countries = dopoSoccer.getPlayersNationalities();
        playSoccerPanel.resetMatchAnimations(initialTime);
        playSoccerPanel.configureInitialBars(
            countries,
            dopoSoccer.getPlayersMaxHealth()
        );
        playSoccerPanel.applyScale(getHeight());
        refreshPlaySoccerPanel();

        p1LeftPressed = false;
        p1RightPressed = false;
        p2LeftPressed = false;
        p2RightPressed = false;

        playSoccerPanel.playStartAnimation(
            countries[0],
            countries[1],
            () -> {
                refreshPlaySoccerPanel();
                matchTimer.start();
                playSoccerPanel.requestFocusInWindow();
            }
        );
    }


    /**
     * Sets the screenSize
     * @param size is the new size
     */
    private void setScreenSize(String size){
        screenSize = size;
    }

    /**
     * Returns player's points
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @return player's points
     */
    private int getPlayerPoints(int playerNum){
        return dopoSoccer.getPlayerPoints(playerNum);
    }

    /**
     * Upgrades a player's stat.
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName is the stat's name
     * @return if the upgrade was completed
     */
    private boolean upgradePlayerStat(int playerNum, String statName){
        try{
            dopoSoccer.upgradePlayerStat(playerNum, statName);
            return true;
        }
        catch(DopoSoccerException e){
            createJOptionPaneMessage(e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Return the stat level for an specific stat of an specific player
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName is the stat that will be consulted
     * @return the statLevel
     */
    private int getStatLevel(int playerNum, String statName){
        return dopoSoccer.getPlayerStatLevel(playerNum, statName);
    }

    /**
     * Gives the cost of an specific update for an specific player
     * @param playerNum is the num of the player (Player number 1 is with 0).
     * @param statName if the stat's name.
     * @return the upgrade cost
     */
    private int getPlayerUpgradeCost(int playerNum, String statName){
        return dopoSoccer.getPlayerUpgradeCost(playerNum, statName);
    }

    /**
     * Main method for DopoSoccerGUI
     * @param args command line arguments
     */
    public static void main(String[] args){
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {

            Log.record((Exception) e);

            JOptionPane.showMessageDialog(
                null,
                "Ups. Ocurrió un error inesperado.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });
        DopoSoccerGUI gui = new DopoSoccerGUI();
        gui.setVisible(true);
    }
}