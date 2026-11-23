package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the pause menu panel shown in the center of the match
 */
public class PausePanel extends JPanel{

    //Components to scale
    private JPanel box;
    private JLabel title;

    //JButtons
    private JButton resumeButton;
    private JButton controlsButton;
    private JButton saveButton;
    private JButton openButton;
    private JButton exitButton;

    /**
     * Creates a new PausePanel
     */
    public PausePanel(){
        prepareView();
    }

    /**
     * Prepares the visual components of the pause menu
     */
    private void prepareView(){
        setOpaque(false);
        setLayout(new GridBagLayout());

        box = new JPanel();
        box.setLayout(new GridLayout(6, 1, 0, 10));
        box.setBackground(new Color(0, 0, 0, 210));

        title = new JLabel("JUEGO EN PAUSA", JLabel.CENTER);
        title.setForeground(Color.YELLOW);

        resumeButton = createMenuButton("Reanudar", Color.BLUE, Color.WHITE);
        controlsButton = createMenuButton("Controles", new Color(46, 204, 113), Color.BLACK);
        saveButton = createMenuButton("Guardar", Color.YELLOW, Color.BLACK);
        openButton = createMenuButton("Abrir", Color.CYAN, Color.BLACK);
        exitButton = createMenuButton("Salir", Color.RED, Color.WHITE);

        box.add(title);
        box.add(resumeButton);
        box.add(controlsButton);
        box.add(saveButton);
        box.add(openButton);
        box.add(exitButton);

        add(box);
        applyScale(1.0);
    }

    /**
     * Creates a styled menu button
     * @param text button label
     * @param bgColor background color
     * @param fgColor foreground text color
     * @return configured JButton
     */
    private JButton createMenuButton(String text, Color bgColor, Color fgColor){
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Scales all fonts, button sizes, gaps and borders according to the JFrame size
     * @param scale scale factor relative to the medium resolution (576p)
     */
    public void applyScale(double scale){
        int vGap = Math.max(4, (int) Math.round(10 * scale));
        box.setLayout(new GridLayout(6, 1, 0, vGap));

        int borderThickness = Math.max(2, (int) Math.round(3 * scale));
        int padV = (int) Math.round(16 * scale);
        int padH = (int) Math.round(40 * scale);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, borderThickness),
            BorderFactory.createEmptyBorder(padV, padH, padV, padH)
        ));

        title.setFont(new Font("Arial", Font.BOLD, (int) Math.round(24 * scale)));

        Font buttonFont = new Font("Arial", Font.BOLD, (int) Math.round(18 * scale));
        Dimension buttonDim = new Dimension(
            (int) Math.round(220 * scale),
            (int) Math.round(38 * scale)
        );

        JButton[] buttons = {resumeButton, controlsButton, saveButton, openButton, exitButton};
        for(JButton btn : buttons){
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonDim);
        }

        revalidate();
        repaint();
    }

    /**
     * Returns the resume match button
     * @return resume JButton
     */
    public JButton getResumeButton(){
        return resumeButton;
    }

    /**
     * Returns the controls dialog button
     * @return controls JButton
     */
    public JButton getControlsButton(){
        return controlsButton;
    }

    /**
     * Returns the save match button
     * @return save JButton
     */
    public JButton getSaveButton(){
        return saveButton;
    }

    /**
     * Returns the open match button
     * @return open JButton
     */
    public JButton getOpenButton(){
        return openButton;
    }

    /**
     * Returns the exit match button
     * @return exit JButton
     */
    public JButton getExitButton(){
        return exitButton;
    }
}