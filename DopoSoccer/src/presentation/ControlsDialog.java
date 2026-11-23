package presentation;

import java.awt.*;
import javax.swing.*;

/**
 * Represents the ControlsDialog for both players
 */
public class ControlsDialog extends JDialog{

    private static final String MOVE_TEXT = "Se mueve a los lados";
    private static final String JUMP_TEXT = "Con este botón el jugador salta";
    private static final String KICK_TEXT = "Patea al balón o al otro jugador";
    private static final String POWER_TEXT = "Activa su aura para un tiro especial";

    //ViewPanels for each tab
    private ViewPanel firstPlayerTab;
    private ViewPanel secondPlayerTab;

    //Player 1 control buttons
    private JButton p1UpButton;
    private JButton p1LeftButton;
    private JButton p1DownButton;
    private JButton p1RightButton;
    private JButton p1PowerButton;

    //Player 2 control buttons
    private JButton p2UpButton;
    private JButton p2LeftButton;
    private JButton p2DownButton;
    private JButton p2RightButton;
    private JButton p2PowerButton;

    //GIF and description labels
    private JLabel p1GifLabel;
    private JLabel p1DescriptionLabel;
    private JLabel p2GifLabel;
    private JLabel p2DescriptionLabel;

    /**
     * Creates ControlsDialog
     * @param gui is the JFrame
     */
    public ControlsDialog(DopoSoccerGUI gui){
        super(gui, "Controles", true);
        setLocationRelativeTo(gui);
        prepareView();
        prepareController();
    }

    /**
     * Prepares the main dialog layout and player tabs
     */
    private void prepareView(){
        setSize(740, 440);
        setResizable(false);

        JTabbedPane players = new JTabbedPane();
        players.add("Jugador 1", prepareFirstPlayerPanel());
        players.add("Jugador 2", prepareSecondPlayerPanel());
        add(players);
    }

    /**
     * Prepares the controls panel for Player 1
     * @return configured ViewPanel
     */
    private ViewPanel prepareFirstPlayerPanel(){
        p1DescriptionLabel = new JLabel();
        firstPlayerTab = new ViewPanel();
        firstPlayerTab.setBackgroundImage("resources/ControlsArea/Trophy.png");
        firstPlayerTab.setLayout(new BorderLayout());
        firstPlayerTab.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        p1UpButton = createKeyButton("W", 26, new Dimension(68, 68));
        p1LeftButton = createKeyButton("A", 26, new Dimension(68, 68));
        p1DownButton = createKeyButton("S", 26, new Dimension(68, 68));
        p1RightButton = createKeyButton("D", 26, new Dimension(68, 68));
        p1PowerButton = createKeyButton("Q", 26, new Dimension(68, 68));

        JPanel keysGrid = new JPanel(new GridLayout(3, 3, 8, 8));
        keysGrid.setOpaque(false);

        keysGrid.add(createEmptySlot());
        keysGrid.add(p1UpButton);
        keysGrid.add(createEmptySlot());
        keysGrid.add(p1LeftButton);
        keysGrid.add(p1DownButton);
        keysGrid.add(p1RightButton);
        keysGrid.add(createEmptySlot());
        keysGrid.add(p1PowerButton);
        keysGrid.add(createEmptySlot());

        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(keysGrid);

        p1GifLabel = new JLabel(firstPlayerTab.chargeGif("resources/ControlsArea/mover.gif"), JLabel.CENTER);
        JPanel p1DescriptionPanel = createDescriptionJPanel(MOVE_TEXT, p1DescriptionLabel);

        firstPlayerTab.add(leftWrapper, BorderLayout.WEST);
        firstPlayerTab.add(createRightPreviewArea(p1GifLabel, p1DescriptionPanel), BorderLayout.EAST);
        return firstPlayerTab;
    }

    /**
     * Prepares the controls panel for Player 2
     * @return configured ViewPanel
     */
    private ViewPanel prepareSecondPlayerPanel(){
        p2DescriptionLabel = new JLabel();
        secondPlayerTab = new ViewPanel();
        secondPlayerTab.setBackgroundImage("resources/ControlsArea/Trophy.png");
        secondPlayerTab.setLayout(new BorderLayout());
        secondPlayerTab.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        p2UpButton = createKeyButton("↑", 28, new Dimension(68, 68));
        p2LeftButton = createKeyButton("←", 28, new Dimension(68, 68));
        p2DownButton = createKeyButton("↓", 28, new Dimension(68, 68));
        p2RightButton = createKeyButton("→", 28, new Dimension(68, 68));
        p2PowerButton = createKeyButton("CTRL DERECHO", 18, new Dimension(220, 62));

        JPanel arrowsGrid = new JPanel(new GridLayout(2, 3, 8, 8));
        arrowsGrid.setOpaque(false);

        arrowsGrid.add(createEmptySlot());
        arrowsGrid.add(p2UpButton);
        arrowsGrid.add(createEmptySlot());
        arrowsGrid.add(p2LeftButton);
        arrowsGrid.add(p2DownButton);
        arrowsGrid.add(p2RightButton);

        JPanel keypadContainer = new JPanel(new BorderLayout(0, 10));
        keypadContainer.setOpaque(false);
        keypadContainer.add(arrowsGrid, BorderLayout.CENTER);
        keypadContainer.add(p2PowerButton, BorderLayout.SOUTH);

        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(keypadContainer);

        p2GifLabel = new JLabel(secondPlayerTab.chargeGif("resources/ControlsArea/mover.gif"), JLabel.CENTER);
        JPanel p2DescriptionPanel = createDescriptionJPanel(MOVE_TEXT, p2DescriptionLabel);

        secondPlayerTab.add(leftWrapper, BorderLayout.WEST);
        secondPlayerTab.add(createRightPreviewArea(p2GifLabel, p2DescriptionPanel), BorderLayout.EAST);
        return secondPlayerTab;
    }

    /**
     * Creates the right panel with the GIF on top and the centered white text below
     * @param gifLabel label displaying the GIF
     * @param descriptionPanel panel displaying the centered explanation
     * @return right preview JPanel
     */
    private JPanel createRightPreviewArea(JLabel gifLabel, JPanel descriptionPanel){
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(310, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(gifLabel, BorderLayout.CENTER);
        rightPanel.add(descriptionPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    /**
     * Creates a blue key button with a white line border
     * @param text button text
     * @param fontSize font size
     * @param size button dimension
     * @return configured JButton
     */
    private JButton createKeyButton(String text, int fontSize, Dimension size){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        return button;
    }

    /**
     * Configures the given JLabel and places it centered inside a transparent JPanel
     * @param text initial explanation text
     * @param label the JLabel attribute to configure
     * @return configured JPanel containing the centered label
     */
    private JPanel createDescriptionJPanel(String text, JLabel label){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

        label.setText(text);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 17));

        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a transparent panel for empty grid cells
     * @return transparent JPanel
     */
    private JPanel createEmptySlot(){
        JPanel slot = new JPanel();
        slot.setOpaque(false);
        return slot;
    }

    /**
     * Updates the GIF and explanation text for the selected player tab
     * @param playerNum 1 for Player 1, 2 for Player 2
     * @param gifFileName file name of the GIF
     * @param description explanation text
     */
    private void updateControlPreview(int playerNum, String gifFileName, String description){
        String path = "resources/ControlsArea/" + gifFileName;
        if(playerNum == 1){
            p1GifLabel.setIcon(firstPlayerTab.chargeGif(path));
            p1DescriptionLabel.setText(description);
        }
        else{
            p2GifLabel.setIcon(secondPlayerTab.chargeGif(path));
            p2DescriptionLabel.setText(description);
        }
    }

    /**
     * Prepares the action listeners for all control buttons
     */
    private void prepareController(){
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        p1UpButton.addActionListener(e -> updateControlPreview(1, "saltar.gif", JUMP_TEXT));
        p1LeftButton.addActionListener(e -> updateControlPreview(1, "mover.gif", MOVE_TEXT));
        p1RightButton.addActionListener(e -> updateControlPreview(1, "mover.gif", MOVE_TEXT));
        p1DownButton.addActionListener(e -> updateControlPreview(1, "patear.gif", KICK_TEXT));
        p1PowerButton.addActionListener(e -> updateControlPreview(1, "poder.gif", POWER_TEXT));

        p2UpButton.addActionListener(e -> updateControlPreview(2, "saltar.gif", JUMP_TEXT));
        p2LeftButton.addActionListener(e -> updateControlPreview(2, "mover.gif", MOVE_TEXT));
        p2RightButton.addActionListener(e -> updateControlPreview(2, "mover.gif", MOVE_TEXT));
        p2DownButton.addActionListener(e -> updateControlPreview(2, "patear.gif", KICK_TEXT));
        p2PowerButton.addActionListener(e -> updateControlPreview(2, "poder.gif", POWER_TEXT));
    }
}