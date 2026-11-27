package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents the end-of-match result panel shown over the field
 */
public class MatchResultPanel extends JPanel{

    private static final int BASE_SPRITE_SIZE = 135;

    //Layouts kept as fields so resizing updates gaps without losing BorderLayout regions
    private FlowLayout mainLayout;
    private BorderLayout boxLayout;
    private BorderLayout centerLayout;
    private GridLayout infoLayout;

    //Container and labels
    private JPanel box;
    private JPanel centerPanel;
    private JPanel infoPanel;
    private JPanel southPanel;
    private JLabel titleLabel;
    private JLabel scoreLabel;
    private JLabel winnerLabel;
    private JLabel pointsLabel;
    private JLabel firstPlayerSpriteLabel;
    private JLabel secondPlayerSpriteLabel;

    //JButtons
    private JButton continueButton;

    //Stored match data to rescale sprites cleanly
    private String firstPlayerCountry;
    private String secondPlayerCountry;
    private int winnerNum;
    private double currentScale;

    /**
     * Creates a new MatchResultPanel
     */
    public MatchResultPanel(){
        this.firstPlayerCountry = "Corea del Sur";
        this.secondPlayerCountry = "Corea del Sur";
        this.winnerNum = 0;
        this.currentScale = 1.0;
        prepareView();
    }

    /**
     * Prepares the visual components using only FlowLayout, BorderLayout and GridLayout
     */
    private void prepareView(){
        setOpaque(false);
        mainLayout = new FlowLayout(FlowLayout.CENTER, 0, 55);
        setLayout(mainLayout);

        boxLayout = new BorderLayout(20, 15);
        box = new JPanel(boxLayout){
            @Override
            protected void paintComponent(Graphics g){
                g.setColor(new Color(0, 0, 0, 215));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        box.setOpaque(false);

        // NORTH: Title
        titleLabel = new JLabel("¡FIN DEL PARTIDO!", JLabel.CENTER);
        titleLabel.setForeground(Color.YELLOW);

        // WEST & EAST: Player sprites
        firstPlayerSpriteLabel = new JLabel();
        firstPlayerSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        firstPlayerSpriteLabel.setVerticalAlignment(JLabel.CENTER);

        secondPlayerSpriteLabel = new JLabel();
        secondPlayerSpriteLabel.setHorizontalAlignment(JLabel.CENTER);
        secondPlayerSpriteLabel.setVerticalAlignment(JLabel.CENTER);

        // CENTER: Final score + winner & points info
        centerLayout = new BorderLayout(0, 8);
        centerPanel = new JPanel(centerLayout);
        centerPanel.setOpaque(false);

        scoreLabel = new JLabel("0 - 0", JLabel.CENTER);
        scoreLabel.setForeground(Color.WHITE);

        infoLayout = new GridLayout(2, 1, 0, 4);
        infoPanel = new JPanel(infoLayout);
        infoPanel.setOpaque(false);

        winnerLabel = new JLabel("", JLabel.CENTER);
        winnerLabel.setForeground(Color.YELLOW);

        pointsLabel = new JLabel("", JLabel.CENTER);
        pointsLabel.setForeground(Color.WHITE);

        infoPanel.add(winnerLabel);
        infoPanel.add(pointsLabel);

        centerPanel.add(scoreLabel, BorderLayout.CENTER);
        centerPanel.add(infoPanel, BorderLayout.SOUTH);

        // SOUTH: Continue button
        southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        southPanel.setOpaque(false);

        continueButton = new JButton("Continuar");
        continueButton.setBackground(Color.YELLOW);
        continueButton.setForeground(Color.BLACK);
        continueButton.setFocusPainted(false);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        southPanel.add(continueButton);

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(firstPlayerSpriteLabel, BorderLayout.WEST);
        box.add(centerPanel, BorderLayout.CENTER);
        box.add(secondPlayerSpriteLabel, BorderLayout.EAST);
        box.add(southPanel, BorderLayout.SOUTH);

        add(box);
        applyScale(1.0);
    }

    /**
     * Updates the panel with the final match score, sprites and points summary
     * @param countries country names of player 1 and player 2
     * @param goals final goals of player 1 and player 2
     * @param winnerNum 0 if player 1 won, 1 if player 2 won
     * @param winnerText text announcing the winner
     * @param pointsText HTML or plain text summarizing points earned
     */
    public void configureResult(String[] countries, int[] goals, int winnerNum, String winnerText, String pointsText){
        if(countries != null && countries.length >= 2){
            this.firstPlayerCountry = countries[0];
            this.secondPlayerCountry = countries[1];
        }
        this.winnerNum = winnerNum;

        scoreLabel.setText(goals[0] + " - " + goals[1]);
        winnerLabel.setText(winnerText);
        pointsLabel.setText(pointsText);
        updateSprites();
        revalidate();
        repaint();
    }

    /**
     * Scales fonts, borders, margins, button and sprites according to the JFrame height scale
     * @param scale scale factor relative to 576p
     */
    public void applyScale(double scale){
        this.currentScale = scale;

        int topMargin = (int) Math.round(55 * scale);
        mainLayout.setVgap(topMargin);

        int hGap = Math.max(8, (int) Math.round(24 * scale));
        int vGap = Math.max(6, (int) Math.round(14 * scale));
        boxLayout.setHgap(hGap);
        boxLayout.setVgap(vGap);
        centerLayout.setVgap(Math.max(4, (int) Math.round(8 * scale)));
        infoLayout.setVgap(Math.max(2, (int) Math.round(4 * scale)));

        int borderThickness = Math.max(2, (int) Math.round(3 * scale));
        int padV = (int) Math.round(22 * scale);
        int padH = (int) Math.round(35 * scale);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, borderThickness),
            BorderFactory.createEmptyBorder(padV, padH, padV, padH)
        ));

        titleLabel.setFont(new Font("Arial", Font.BOLD, (int) Math.round(32 * scale)));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int) Math.round(52 * scale)));
        winnerLabel.setFont(new Font("Arial", Font.BOLD, (int) Math.round(18 * scale)));
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, (int) Math.round(14 * scale)));

        continueButton.setFont(new Font("Arial", Font.BOLD, (int) Math.round(20 * scale)));
        continueButton.setPreferredSize(new Dimension(
            (int) Math.round(200 * scale),
            (int) Math.round(44 * scale)
        ));

        updateSprites();
        revalidate();
        repaint();
    }

    /**
     * Loads and assigns the winner (AuraNormal) and loser (Paralizado) sprites
     */
    private void updateSprites(){
        int spriteSize = Math.max(64, (int) Math.round(BASE_SPRITE_SIZE * currentScale));

        String p1Suffix;
        String p2Suffix;
        if(winnerNum == 0){
            p1Suffix = "AuraNormal";
            p2Suffix = "Paralizado";
        }
        else{
            p1Suffix = "Paralizado";
            p2Suffix = "AuraNormal";
        }

        firstPlayerSpriteLabel.setIcon(loadSpriteIcon(firstPlayerCountry, p1Suffix, spriteSize, true));
        secondPlayerSpriteLabel.setIcon(loadSpriteIcon(secondPlayerCountry, p2Suffix, spriteSize, false));

        Dimension spriteDim = new Dimension(spriteSize, spriteSize);
        firstPlayerSpriteLabel.setPreferredSize(spriteDim);
        secondPlayerSpriteLabel.setPreferredSize(spriteDim);
    }

    /**
     * Formats the country name to match resource filenames in PlaySoccerZone
     * @param country country name
     * @return formatted key
     */
    private String formatCountryKey(String country){
        if(country == null){
            return "CoreaDelSur";
        }
        return country.trim().replace(" del ", "Del").replace(" ", "");
    }

    /**
     * Loads, scales and optionally flips a player sprite horizontally
     * @param country player country name
     * @param stateSuffix sprite state suffix (ex: "AuraNormal", "Paralizado")
     * @param size target width and height in pixels
     * @param flipHorizontal true to mirror horizontally so player 1 faces right
     * @return scaled ImageIcon
     */
    private ImageIcon loadSpriteIcon(String country, String stateSuffix, int size, boolean flipHorizontal){
        String path = "resources/PlaySoccerZone/" + formatCountryKey(country) + stateSuffix + ".png";
        Image rawImage = new ImageIcon(path).getImage();
        BufferedImage canvas = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if(flipHorizontal){
            g2d.drawImage(rawImage, size, 0, -size, size, null);
        }
        else{
            g2d.drawImage(rawImage, 0, 0, size, size, null);
        }
        g2d.dispose();
        return new ImageIcon(canvas);
    }

    /**
     * Returns the continue button
     * @return continue JButton
     */
    public JButton getContinueButton(){
        return continueButton;
    }
}