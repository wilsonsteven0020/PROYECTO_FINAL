package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a PlaySoccerPanel
 */
public class PlaySoccerPanel extends ViewPanel{

    private static final double BASE_HEIGHT = 576.0;
    private static final int BASE_FLAG_WIDTH = 36;
    private static final int BASE_FLAG_HEIGHT = 26;
    private static final String ANIMATIONS_PATH = "resources/PlaySoccerZone/animations/";
    private static final int ANIMATION_FPS = 25;
    private static final int FRAME_DELAY_MS = 1000 / ANIMATION_FPS;
    private static final int PRELOAD_FRAMES = 10;

    //Panels to scale layouts and borders
    private JPanel northPanel;
    private JPanel firstPlayerPanel;
    private JPanel secondPlayerPanel;
    private JPanel firstPlayerHeader;
    private JPanel secondPlayerHeader;
    private JPanel centerAreaPanel;

    //JLabels for text
    private JLabel firstPlayerLabel;
    private JLabel secondPlayerLabel;
    private JLabel matchStatusLabel;
    private JLabel scoreLabel;

    //Dedicated JLabels exclusively for the flags
    private JLabel firstPlayerFlag;
    private JLabel secondPlayerFlag;

    //Balls
    private int[] ballBounds;
    private Image ballImage;
    private ArrayList<int[]> ballsBounds;
    private ArrayList<String> ballsSkins;

    //Current countries and sprite states
    private String[] currentCountries;
    private String[] playersSpriteStates;
    private int[] firstPlayerBounds;
    private int[] secondPlayerBounds;
    private HashMap<String, Image> spriteCache;

    //JProgressBars
    private JProgressBar firstPlayerHealthBar;
    private JProgressBar firstPlayerPowerBar;
    private JProgressBar secondPlayerHealthBar;
    private JProgressBar secondPlayerPowerBar;

    //JButtons
    private JButton pauseButton;

    //PausePanel
    private PausePanel pausePanel;

    //Frame-sequence animation attributes
    private boolean inStartAnimation;
    private int initialTime;
    private boolean halfTimeTriggered;
    private boolean inHalfTime;
    private boolean playbackStarted;
    private Timer animationTimer;
    private Image[] bgFrames;
    private Image[] player1Frames;
    private Image[] player2Frames;
    private int currentFrameIndex;
    private int totalFrames;

    /**
     * Creates a new PlaySoccerPanel
     */
    public PlaySoccerPanel(){
        currentCountries = new String[]{"CoreaDelSur", "CoreaDelSur"};
        playersSpriteStates = new String[]{"Normal", "Normal"};
        ballBounds = new int[]{0, 0, 36, 36};
        ballImage = new ImageIcon("resources/PlaySoccerZone/Pelota.png").getImage();
        firstPlayerBounds = new int[]{0, 0, 90, 90};
        secondPlayerBounds = new int[]{0, 0, 90, 90};
        spriteCache = new HashMap<>();
        ballsBounds = new ArrayList<>();
        ballsSkins = new ArrayList<>();
        inStartAnimation = false;
        initialTime = -1;
        halfTimeTriggered = false;
        inHalfTime = false;
        playbackStarted = false;
        currentFrameIndex = 0;
        totalFrames = 0;
        prepareView();
    }

    private void prepareView(){
        setBackgroundImage("resources/PlaySoccerZone/field.png");
        setLayout(new BorderLayout());
        setFocusable(true);
        pausePanel = new PausePanel();
        pausePanel.setVisible(false);
        add(prepareNorthArea(), BorderLayout.NORTH);
        add(pausePanel, BorderLayout.CENTER);

        // Automatically rescales whenever the JFrame changes resolution
        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e){
                if(getHeight() > 0){
                    applyScale(getHeight());
                }
            }
        });
    }

    private JPanel prepareNorthArea(){
        northPanel = new JPanel();
        northPanel.setOpaque(false);
        northPanel.setLayout(new BorderLayout());
        northPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        northPanel.add(prepareFirstPlayerArea(), BorderLayout.WEST);
        northPanel.add(prepareCenterArea(), BorderLayout.CENTER);
        northPanel.add(prepareSecondPlayerArea(), BorderLayout.EAST);

        return northPanel;
    }

    private JPanel prepareFirstPlayerArea(){
        firstPlayerPanel = new JPanel();
        firstPlayerPanel.setOpaque(false);
        firstPlayerPanel.setLayout(new GridLayout(3, 1, 0, 5));

        firstPlayerHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        firstPlayerHeader.setOpaque(false);

        firstPlayerLabel = new JLabel("Jugador 1", JLabel.LEFT);
        firstPlayerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        firstPlayerLabel.setForeground(Color.BLACK);

        firstPlayerFlag = new JLabel();
        firstPlayerFlag.setHorizontalAlignment(JLabel.CENTER);

        firstPlayerHeader.add(firstPlayerLabel);
        firstPlayerHeader.add(firstPlayerFlag);

        firstPlayerHealthBar = createProgressBar(0, 100, new Color(46, 204, 113));
        firstPlayerHealthBar.setValue(100);
        firstPlayerHealthBar.setString("Vida: 100 / 100");

        firstPlayerPowerBar = createProgressBar(0, 100, new Color(255, 165, 0));
        firstPlayerPowerBar.setValue(0);
        firstPlayerPowerBar.setString("Poder: 0%");

        firstPlayerPanel.add(firstPlayerHeader);
        firstPlayerPanel.add(firstPlayerHealthBar);
        firstPlayerPanel.add(firstPlayerPowerBar);

        return firstPlayerPanel;
    }

    private JPanel prepareSecondPlayerArea(){
        secondPlayerPanel = new JPanel();
        secondPlayerPanel.setOpaque(false);
        secondPlayerPanel.setLayout(new GridLayout(3, 1, 0, 5));

        secondPlayerHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        secondPlayerHeader.setOpaque(false);

        secondPlayerFlag = new JLabel();
        secondPlayerFlag.setHorizontalAlignment(JLabel.CENTER);

        secondPlayerLabel = new JLabel("Jugador 2", JLabel.RIGHT);
        secondPlayerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        secondPlayerLabel.setForeground(Color.BLACK);

        secondPlayerHeader.add(secondPlayerFlag);
        secondPlayerHeader.add(secondPlayerLabel);

        secondPlayerHealthBar = createProgressBar(0, 100, new Color(46, 204, 113));
        secondPlayerHealthBar.setValue(100);
        secondPlayerHealthBar.setString("Vida: 100 / 100");

        secondPlayerPowerBar = createProgressBar(0, 100, new Color(255, 165, 0));
        secondPlayerPowerBar.setValue(0);
        secondPlayerPowerBar.setString("Poder: 0%");

        secondPlayerPanel.add(secondPlayerHeader);
        secondPlayerPanel.add(secondPlayerHealthBar);
        secondPlayerPanel.add(secondPlayerPowerBar);

        return secondPlayerPanel;
    }

    private JPanel prepareCenterArea(){
        centerAreaPanel = new JPanel();
        centerAreaPanel.setOpaque(false);
        centerAreaPanel.setLayout(new GridLayout(3, 1, 0, 3));

        matchStatusLabel = new JLabel("00:00", JLabel.CENTER);
        matchStatusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        matchStatusLabel.setForeground(Color.WHITE);

        scoreLabel = new JLabel("0 - 0", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(Color.WHITE);

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setOpaque(false);
        pauseButton = new JButton("Pausar");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        pauseButton.setForeground(Color.BLACK);
        pauseButton.setBackground(Color.YELLOW);
        pauseButton.setPreferredSize(new Dimension(120, 35));
        pauseButton.setFocusPainted(false);
        pauseButton.setFocusable(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonWrapper.add(pauseButton);

        centerAreaPanel.add(matchStatusLabel);
        centerAreaPanel.add(scoreLabel);
        centerAreaPanel.add(buttonWrapper);

        return centerAreaPanel;
    }

    private JProgressBar createProgressBar(int min, int max, Color foregroundColor){
        JProgressBar bar = new JProgressBar(min, max);
        bar.setPreferredSize(new Dimension(220, 22));
        bar.setStringPainted(true);
        bar.setFont(new Font("Arial", Font.BOLD, 13));
        bar.setForeground(foregroundColor);
        bar.setBackground(new Color(45, 45, 45));
        bar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return bar;
    }

    private String formatCountryKey(String country){
        if(country == null){
            return "CoreaDelSur";
        }
        return country.trim().replace(" del ", "Del").replace(" ", "");
    }

    private String getFlagPath(String country){
        return "resources/PlaySoccerZone/" + formatCountryKey(country) + "Flag.png";
    }

    private Image getPlayerSprite(String country, String stateSuffix){
        String countryKey = formatCountryKey(country);
        String path = "resources/PlaySoccerZone/" + countryKey + stateSuffix + ".png";
        if(!spriteCache.containsKey(path)){
            spriteCache.put(path, new ImageIcon(path).getImage());
        }
        return spriteCache.get(path);
    }

    private File[] listFrameFiles(String folderPath){
        File dir = new File(folderPath);
        File[] files = dir.listFiles();
        if(files == null){
            return new File[0];
        }
        Arrays.sort(files);
        ArrayList<File> valid = new ArrayList<>();
        for(File file : files){
            String name = file.getName().toLowerCase();
            if(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")){
                valid.add(file);
            }
        }
        return valid.toArray(new File[0]);
    }

    private Image[] createFrameArray(File[] files){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image[] frames = new Image[files.length];
        for(int i = 0; i < files.length; i++){
            frames[i] = toolkit.createImage(files[i].getPath());
        }
        return frames;
    }

    private void prepareImageAt(Image[] frames, int index){
        if(frames != null && index >= 0 && index < frames.length && frames[index] != null){
            Toolkit.getDefaultToolkit().prepareImage(frames[index], -1, -1, null);
        }
    }

    private void prepareFrame(int index){
        prepareImageAt(bgFrames, index);
        prepareImageAt(player1Frames, index);
        prepareImageAt(player2Frames, index);
    }

    private boolean isImageReady(Image[] frames, int index){
        if(frames == null || index < 0 || index >= frames.length || frames[index] == null){
            return true;
        }
        int status = Toolkit.getDefaultToolkit().checkImage(frames[index], -1, -1, null);
        return (status & (ALLBITS | ERROR | ABORT)) != 0;
    }

    private boolean isFrameReady(int index){
        return isImageReady(bgFrames, index)
            && isImageReady(player1Frames, index)
            && isImageReady(player2Frames, index);
    }

    private boolean areFramesReady(int fromIndex, int count){
        for(int i = fromIndex; i < fromIndex + count; i++){
            if(!isFrameReady(i)){
                return false;
            }
        }
        return true;
    }

    private void flushFrameAt(Image[] frames, int index){
        if(frames != null && index >= 0 && index < frames.length){
            if(frames[index] != null){
                frames[index].flush();
                frames[index] = null;
            }
        }
    }

    private void flushFrameArray(Image[] frames){
        if(frames != null){
            for(int i = 0; i < frames.length; i++){
                if(frames[i] != null){
                    frames[i].flush();
                    frames[i] = null;
                }
            }
        }
    }

    private void clearAnimationFrames(){
        flushFrameArray(bgFrames);
        flushFrameArray(player1Frames);
        flushFrameArray(player2Frames);
        bgFrames = null;
        player1Frames = null;
        player2Frames = null;
        currentFrameIndex = 0;
        totalFrames = 0;
        playbackStarted = false;
    }

    private void drawFrameAt(Graphics g, Image[] frames, int index){
        if(frames != null && index >= 0 && index < frames.length){
            Image img = frames[index];
            if(img != null && isImageReady(frames, index)){
                g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    private void startSequenceAnimation(File[] bgFiles, File[] p1Files, File[] p2Files, Runnable onFinish){
        if(animationTimer != null && animationTimer.isRunning()){
            animationTimer.stop();
        }
        clearAnimationFrames();

        totalFrames = Math.max(bgFiles.length, Math.max(p1Files.length, p2Files.length));
        if(totalFrames == 0){
            if(onFinish != null){
                onFinish.run();
            }
            return;
        }

        bgFrames = createFrameArray(bgFiles);
        player1Frames = createFrameArray(p1Files);
        player2Frames = createFrameArray(p2Files);
        currentFrameIndex = 0;
        playbackStarted = false;

        final int initialPreload = Math.min(PRELOAD_FRAMES, totalFrames);
        for(int i = 0; i < initialPreload; i++){
            prepareFrame(i);
        }

        animationTimer = new Timer(FRAME_DELAY_MS, e -> {
            if(!playbackStarted){
                if(isFrameReady(0)){
                    repaint();
                }
                if(!areFramesReady(0, initialPreload)){
                    return;
                }
                playbackStarted = true;
                return;
            }

            if(currentFrameIndex + 1 >= totalFrames){
                animationTimer.stop();
                clearAnimationFrames();
                if(onFinish != null){
                    onFinish.run();
                }
                return;
            }

            if(!isFrameReady(currentFrameIndex + 1)){
                return;
            }

            flushFrameAt(bgFrames, currentFrameIndex);
            flushFrameAt(player1Frames, currentFrameIndex);
            flushFrameAt(player2Frames, currentFrameIndex);
            currentFrameIndex++;
            prepareFrame(currentFrameIndex + PRELOAD_FRAMES - 1);
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        if(inStartAnimation){
            drawFrameAt(g, bgFrames, currentFrameIndex);
            drawFrameAt(g, player1Frames, currentFrameIndex);
            drawFrameAt(g, player2Frames, currentFrameIndex);
            return;
        }

        if(inHalfTime){
            drawFrameAt(g, bgFrames, currentFrameIndex);
        }

        if(currentCountries == null || currentCountries.length < 2){
            return;
        }
        Image p1Image = getPlayerSprite(currentCountries[0], playersSpriteStates[0]);
        Image p2Image = getPlayerSprite(currentCountries[1], playersSpriteStates[1]);

        if(p1Image != null && firstPlayerBounds != null){
            // Flip Player 1 horizontally so it faces right toward Player 2
            g.drawImage(
                p1Image,
                firstPlayerBounds[0] + firstPlayerBounds[2],
                firstPlayerBounds[1],
                -firstPlayerBounds[2],
                firstPlayerBounds[3],
                this
            );
        }
        if(p2Image != null && secondPlayerBounds != null){
            g.drawImage(
                p2Image,
                secondPlayerBounds[0],
                secondPlayerBounds[1],
                secondPlayerBounds[2],
                secondPlayerBounds[3],
                this
            );
        }
        if(ballsBounds != null && !ballsBounds.isEmpty()){
            for(int i = 0; i < ballsBounds.size(); i++){
                int[] bBounds = ballsBounds.get(i);
                String skinName = "Normal";
                if(ballsSkins != null && i < ballsSkins.size()){
                    skinName = ballsSkins.get(i);
                }
                Image currentBallImage = getBallSprite(skinName);
                if(currentBallImage != null){
                    g.drawImage(currentBallImage, bBounds[0], bBounds[1], bBounds[2], bBounds[3], this);
                }
            }
        }
        else if(ballImage != null && ballBounds != null){
            g.drawImage(ballImage, ballBounds[0], ballBounds[1], ballBounds[2], ballBounds[3], this);
        }

        if(inHalfTime){
            double scale;
            if(getHeight() > 0){
                scale = getHeight() / BASE_HEIGHT;
            }
            else{
                scale = 1.0;
            }
            String msg = "MITAD DE TIEMPO";
            Font halfTimeFont = new Font("Arial", Font.BOLD, (int) Math.round(48 * scale));
            g.setFont(halfTimeFont);
            FontMetrics fm = g.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(msg)) / 2;
            int textY = (getHeight() + fm.getAscent()) / 2;

            g.setColor(Color.BLACK);
            g.drawString(msg, textX + 3, textY + 3);
            g.setColor(Color.YELLOW);
            g.drawString(msg, textX, textY);
        }
    }

    /**
     * Resets both start and half-time animations and stores the initial match time
     * @param initialTime initial match duration in seconds (-1 if goal-based)
     */
    public void resetMatchAnimations(int initialTime){
        this.initialTime = initialTime;
        this.halfTimeTriggered = false;
        this.inHalfTime = false;
        this.inStartAnimation = false;
        if(animationTimer != null && animationTimer.isRunning()){
            animationTimer.stop();
        }
        clearAnimationFrames();
        northPanel.setVisible(true);
        pauseButton.setEnabled(true);
        setBackgroundImage("resources/PlaySoccerZone/field.png");
    }

    /**
     * Plays startAnimation with player1<Country> and player2<Country> frame sequences layered on top
     * @param firstCountry player 1 nationality name
     * @param secondCountry player 2 nationality name
     * @param onStartAnimationEnd action executed when the intro animation finishes
     */
    public void playStartAnimation(String firstCountry, String secondCountry, Runnable onStartAnimationEnd){
        File[] bgFiles = listFrameFiles(ANIMATIONS_PATH + "startAnimation");
        File[] p1Files = listFrameFiles(ANIMATIONS_PATH + "player1" + formatCountryKey(firstCountry));
        File[] p2Files = listFrameFiles(ANIMATIONS_PATH + "player2" + formatCountryKey(secondCountry));

        if(bgFiles.length == 0 && p1Files.length == 0 && p2Files.length == 0){
            if(onStartAnimationEnd != null){
                onStartAnimationEnd.run();
            }
            return;
        }

        inStartAnimation = true;
        northPanel.setVisible(false);
        pauseButton.setEnabled(false);
        revalidate();
        repaint();

        startSequenceAnimation(bgFiles, p1Files, p2Files, () -> {
            inStartAnimation = false;
            northPanel.setVisible(true);
            pauseButton.setEnabled(true);
            setBackgroundImage("resources/PlaySoccerZone/field.png");
            revalidate();
            repaint();
            if(onStartAnimationEnd != null){
                onStartAnimationEnd.run();
            }
        });
    }

    /**
     * Checks if the match reached half of its initial time and triggers the destroyedFieldAnimation sequence
     * @param currentRemainingTime current remaining seconds in the match
     * @param onHalfTimeEnd action executed right when the animation finishes
     * @return true if half-time just started or is currently playing
     */
    public boolean checkAndTriggerHalfTime(int currentRemainingTime, Runnable onHalfTimeEnd){
        if(inHalfTime){
            return true;
        }
        if(initialTime > 0 && !halfTimeTriggered && currentRemainingTime <= initialTime / 2){
            halfTimeTriggered = true;

            File[] bgFiles = listFrameFiles(ANIMATIONS_PATH + "destroyedFieldAnimation");
            if(bgFiles.length == 0){
                setBackgroundImage("resources/PlaySoccerZone/destroyedField.png");
                repaint();
                return false;
            }

            inHalfTime = true;
            pauseButton.setEnabled(false);
            matchStatusLabel.setText("MITAD DE TIEMPO");
            repaint();

            startSequenceAnimation(bgFiles, new File[0], new File[0], () -> {
                inHalfTime = false;
                pauseButton.setEnabled(true);
                setBackgroundImage("resources/PlaySoccerZone/destroyedField.png");
                repaint();
                if(onHalfTimeEnd != null){
                    onHalfTimeEnd.run();
                }
            });
            return true;
        }
        return false;
    }

    /**
     * Indicates if the half-time animation is currently active
     * @return true if in half-time animation
     */
    public boolean isInHalfTime(){
        return inHalfTime;
    }

    /**
     * Indicates if any full-screen match animation (start or half-time) is currently playing
     * @return true if an animation is active
     */
    public boolean isInAnimation(){
        return inStartAnimation || inHalfTime;
    }

    /**
     * Sets the bounds of all active balls to be drawn on the field
     * @param ballsBounds list of ball bounds
     */
    public void setBallsBounds(ArrayList<int[]> ballsBounds){
        this.ballsBounds = ballsBounds;
    }

    /**
     * Sets the skin names of all active balls to be drawn on the field
     * @param ballsSkins list of ball skin names
     */
    public void setBallsSkins(ArrayList<String> ballsSkins){
        this.ballsSkins = ballsSkins;
    }

    public void setPlayerFlag(int playerNum, String country){
        if(country == null){
            return;
        }
        currentCountries[playerNum] = country;
        double scale;
        if(getHeight() > 0){
            scale = getHeight() / BASE_HEIGHT;
        }
        else{
            scale = 1.0;
        }
        int flagW = Math.min(90, Math.max(18, (int) Math.round(BASE_FLAG_WIDTH * scale)));
        int flagH = Math.min(90, Math.max(14, (int) Math.round(BASE_FLAG_HEIGHT * scale)));

        ImageIcon flagIcon = chargeIcon(getFlagPath(country), flagW, flagH);
        if(playerNum == 0){
            firstPlayerFlag.setIcon(flagIcon);
            firstPlayerFlag.setPreferredSize(new Dimension(flagW, flagH));
        }
        else{
            secondPlayerFlag.setIcon(flagIcon);
            secondPlayerFlag.setPreferredSize(new Dimension(flagW, flagH));
        }
    }

    public void applyScale(int currentHeight){
        double scale = currentHeight / BASE_HEIGHT;

        int topPad = (int) Math.round(15 * scale);
        int sidePad = (int) Math.round(25 * scale);
        int bottomPad = (int) Math.round(10 * scale);
        northPanel.setBorder(BorderFactory.createEmptyBorder(topPad, sidePad, bottomPad, sidePad));

        int gridGap = Math.max(2, (int) Math.round(4 * scale));
        firstPlayerPanel.setLayout(new GridLayout(3, 1, 0, gridGap));
        secondPlayerPanel.setLayout(new GridLayout(3, 1, 0, gridGap));
        centerAreaPanel.setLayout(new GridLayout(3, 1, 0, gridGap));

        int headerGap = Math.max(4, (int) Math.round(10 * scale));
        firstPlayerHeader.setLayout(new FlowLayout(FlowLayout.LEFT, headerGap, 0));
        secondPlayerHeader.setLayout(new FlowLayout(FlowLayout.RIGHT, headerGap, 0));

        Font playerFont = new Font("Arial", Font.BOLD, (int) Math.round(20 * scale));
        firstPlayerLabel.setFont(playerFont);
        secondPlayerLabel.setFont(playerFont);

        if(currentCountries != null && currentCountries.length >= 2){
            setPlayerFlag(0, currentCountries[0]);
            setPlayerFlag(1, currentCountries[1]);
        }

        matchStatusLabel.setFont(new Font("Arial", Font.BOLD, (int) Math.round(22 * scale)));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int) Math.round(28 * scale)));

        pauseButton.setFont(new Font("Arial", Font.BOLD, (int) Math.round(16 * scale)));
        pauseButton.setPreferredSize(new Dimension(
            (int) Math.round(120 * scale),
            (int) Math.round(35 * scale)
        ));

        Dimension barDim = new Dimension(
            (int) Math.round(220 * scale),
            (int) Math.round(22 * scale)
        );
        Font barFont = new Font("Arial", Font.BOLD, (int) Math.round(13 * scale));
        int barBorder = Math.max(1, (int) Math.round(2 * scale));

        JProgressBar[] bars = {
            firstPlayerHealthBar, firstPlayerPowerBar,
            secondPlayerHealthBar, secondPlayerPowerBar
        };
        for(JProgressBar bar : bars){
            bar.setPreferredSize(barDim);
            bar.setFont(barFont);
            bar.setBorder(BorderFactory.createLineBorder(Color.WHITE, barBorder));
        }

        pausePanel.applyScale(scale);
        revalidate();
        repaint();
    }

    public void configureInitialBars(String[] countries, int[] maxHealths){
        this.currentCountries = countries;
        setPlayerFlag(0, countries[0]);
        setPlayerFlag(1, countries[1]);

        firstPlayerHealthBar.setMaximum(maxHealths[0]);
        firstPlayerHealthBar.setValue(maxHealths[0]);
        firstPlayerHealthBar.setString("Vida: " + maxHealths[0] + " / " + maxHealths[0]);

        secondPlayerHealthBar.setMaximum(maxHealths[1]);
        secondPlayerHealthBar.setValue(maxHealths[1]);
        secondPlayerHealthBar.setString("Vida: " + maxHealths[1] + " / " + maxHealths[1]);

        firstPlayerPowerBar.setValue(0);
        firstPlayerPowerBar.setString("Poder: 0%");
        secondPlayerPowerBar.setValue(0);
        secondPlayerPowerBar.setString("Poder: 0%");
        scoreLabel.setText("0 - 0");
        showPauseMenu(false);
    }

    public void updateBars(int[] currentHealths, int[] maxHealths, int[] powerBars){
        firstPlayerHealthBar.setValue(currentHealths[0]);
        firstPlayerHealthBar.setString("Vida: " + currentHealths[0] + " / " + maxHealths[0]);

        secondPlayerHealthBar.setValue(currentHealths[1]);
        secondPlayerHealthBar.setString("Vida: " + currentHealths[1] + " / " + maxHealths[1]);

        firstPlayerPowerBar.setValue(powerBars[0]);
        firstPlayerPowerBar.setString("Poder: " + powerBars[0] + "%");

        secondPlayerPowerBar.setValue(powerBars[1]);
        secondPlayerPowerBar.setString("Poder: " + powerBars[1] + "%");
    }

    public void updateEntitiesAndScore(int[] p1Bounds, int[] p2Bounds, int[] ballBounds, String[] spriteStates, int[] goals){
        this.firstPlayerBounds = p1Bounds;
        this.secondPlayerBounds = p2Bounds;
        this.ballBounds = ballBounds;
        this.playersSpriteStates = spriteStates;
        this.scoreLabel.setText(goals[0] + " - " + goals[1]);
        repaint();
    }

    public void showPauseMenu(boolean paused){
        pauseButton.setVisible(!paused);
        pausePanel.setVisible(paused);
        if(!paused){
            requestFocusInWindow();
        }
        revalidate();
        repaint();
    }

    public void setMatchStatusText(String text){
        matchStatusLabel.setText(text);
    }

    public JButton getPauseButton(){
        return pauseButton;
    }

    public PausePanel getPausePanel(){
        return pausePanel;
    }

    public JLabel getFirstPlayerFlag(){
        return firstPlayerFlag;
    }

    public JLabel getSecondPlayerFlag(){
        return secondPlayerFlag;
    }

    /**
     * Returns the image corresponding to the given ball skin
     * @param skinName "Normal" or the country name of the active power
     * @return Image to draw for the ball
     */
    private Image getBallSprite(String skinName){
        if(skinName == null || skinName.equals("Normal")){
            return ballImage;
        }
        String countryKey = formatCountryKey(skinName);
        String path = "resources/PlaySoccerZone/Pelota" + countryKey + ".png";
        if(!spriteCache.containsKey(path)){
            File skinFile = new File(path);
            if(skinFile.exists()){
                spriteCache.put(path, new ImageIcon(path).getImage());
            }
            else{
                return ballImage;
            }
        }
        return spriteCache.get(path);
    }

    /**
     * Restores visual animation and background state when loading a saved match
     * @param initialTime initial duration in seconds (-1 if goal-based)
     * @param currentRemainingTime remaining seconds at the moment the match was saved
     */
    public void restoreLoadedMatchState(int initialTime, int currentRemainingTime){
        resetMatchAnimations(initialTime);
        if(initialTime > 0 && currentRemainingTime <= initialTime / 2){
            this.halfTimeTriggered = true;
            setBackgroundImage("resources/PlaySoccerZone/destroyedField.png");
            repaint();
        }
    }
}