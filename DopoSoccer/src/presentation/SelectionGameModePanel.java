package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a SelectionGameModePanel
 */
public class SelectionGameModePanel extends ViewPanel{

    //Icon dimensions for standard JButtons
    private static final int MODE_ICON_WIDTH = 512;
    private static final int MODE_ICON_HEIGHT = 390;
    private static final int MACHINE_ICON_WIDTH = 512;
    private static final int MACHINE_ICON_HEIGHT = 195;

    //Panels for switching between game modes and machine profiles using BorderLayout
    private JPanel centerPanel;
    private JPanel modesPanel;
    private JPanel machinesPanel;

    //Title
    private JLabel title;

    //Mode icons (color and grayscale)
    private ImageIcon pvspColorIcon;
    private ImageIcon pvspNoColorIcon;
    private ImageIcon pvsmColorIcon;
    private ImageIcon pvsmNoColorIcon;

    //Machine profile icons (color and grayscale)
    private ImageIcon offensiveColorIcon;
    private ImageIcon offensiveNoColorIcon;
    private ImageIcon defensiveColorIcon;
    private ImageIcon defensiveNoColorIcon;
    private ImageIcon clumsyColorIcon;
    private ImageIcon clumsyNoColorIcon;
    private ImageIcon expertColorIcon;
    private ImageIcon expertNoColorIcon;

    //JButtons
    private JButton pvsp;
    private JButton pvsm;
    private JButton offensive;
    private JButton defensive;
    private JButton clumsy;
    private JButton expert;
    private JButton back;
    private JButton continueButton;

    //Selection state
    private boolean showingMachines;
    private String selectedMode;
    private String selectedMachineProfile;

    /**
     * Creates a new SelectionGameModePanel
     */
    public SelectionGameModePanel(){
        showingMachines = false;
        selectedMode = "Jugador VS Jugador";
        selectedMachineProfile = "Ofensiva";
        prepareView();
        prepareController();
    }

    private void prepareView(){
        setBackgroundImage("resources/SelectionGameModeArea/Trophy.png");
        prepareModeImages();
        prepareMachineImages();
        setLayout(new BorderLayout());
        add(prepareTitle(), BorderLayout.NORTH);
        add(prepareCenterArea(), BorderLayout.CENTER);
        add(prepareSouthArea(), BorderLayout.SOUTH);
    }

    private void prepareModeImages(){
        pvspColorIcon = chargeIcon("resources/SelectionGameModeArea/PvsPColor.png", MODE_ICON_WIDTH, MODE_ICON_HEIGHT);
        pvspNoColorIcon = chargeIcon("resources/SelectionGameModeArea/PvsPNoColor.png", MODE_ICON_WIDTH, MODE_ICON_HEIGHT);
        pvsmColorIcon = chargeIcon("resources/SelectionGameModeArea/PvsMColor.png", MODE_ICON_WIDTH, MODE_ICON_HEIGHT);
        pvsmNoColorIcon = chargeIcon("resources/SelectionGameModeArea/PvsMNoColor.png", MODE_ICON_WIDTH, MODE_ICON_HEIGHT);
    }

    private void prepareMachineImages(){
        offensiveColorIcon = chargeIcon("resources/SelectionGameModeArea/OfensivaColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        offensiveNoColorIcon = chargeIcon("resources/SelectionGameModeArea/OfensivaNoColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        defensiveColorIcon = chargeIcon("resources/SelectionGameModeArea/DefensivaColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        defensiveNoColorIcon = chargeIcon("resources/SelectionGameModeArea/DefensivaNoColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        clumsyColorIcon = chargeIcon("resources/SelectionGameModeArea/TorpeColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        clumsyNoColorIcon = chargeIcon("resources/SelectionGameModeArea/TorpeNoColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        expertColorIcon = chargeIcon("resources/SelectionGameModeArea/ExpertoColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
        expertNoColorIcon = chargeIcon("resources/SelectionGameModeArea/ExpertoNoColor.png", MACHINE_ICON_WIDTH, MACHINE_ICON_HEIGHT);
    }

    private JPanel prepareTitle(){
        JPanel panel = new JPanel(new FlowLayout());
        panel.setOpaque(false);
        title = new JLabel("SELECCIONA EL MODO DE JUEGO", JLabel.CENTER);
        title.setForeground(new Color(224, 184, 95));
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        panel.add(title);
        return panel;
    }

    private JPanel prepareCenterArea(){
        modesPanel = prepareOptionButtons();
        machinesPanel = prepareMachineButtons();
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(modesPanel, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel prepareOptionButtons(){
        pvsp = createImageButton(pvspColorIcon, "Jugador VS Jugador");
        pvsp.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 1, Color.BLACK));

        pvsm = createImageButton(pvsmNoColorIcon, "Jugador VS Máquina");
        pvsm.setBorder(BorderFactory.createMatteBorder(2, 1, 2, 0, Color.BLACK));

        JPanel panel = new JPanel(new GridLayout(1, 2, 0, 0));
        panel.setOpaque(false);
        panel.add(pvsp);
        panel.add(pvsm);

        return panel;
    }

    private JPanel prepareMachineButtons(){
        offensive = createImageButton(offensiveColorIcon, "Ofensiva");
        offensive.setBorder(BorderFactory.createMatteBorder(2, 0, 1, 1, Color.BLACK));

        defensive = createImageButton(defensiveNoColorIcon, "Defensiva");
        defensive.setBorder(BorderFactory.createMatteBorder(2, 1, 1, 0, Color.BLACK));

        clumsy = createImageButton(clumsyNoColorIcon, "Torpe");
        clumsy.setBorder(BorderFactory.createMatteBorder(1, 0, 2, 1, Color.BLACK));

        expert = createImageButton(expertNoColorIcon, "Experta");
        expert.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 0, Color.BLACK));

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 0, 0));
        gridPanel.setOpaque(false);
        gridPanel.add(offensive);
        gridPanel.add(defensive);
        gridPanel.add(clumsy);
        gridPanel.add(expert);

        return gridPanel;
    }

    private JPanel prepareSouthArea(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panel.setOpaque(false);

        back = createActionButton("Volver", Color.RED);
        continueButton = createActionButton("Continuar", new Color(39, 174, 96));

        panel.add(back);
        panel.add(continueButton);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return panel;
    }

    private void prepareController(){
        pvsp.addActionListener(e -> selectGameMode("Jugador VS Jugador"));
        pvsm.addActionListener(e -> selectGameMode("Jugador VS Máquina"));

        offensive.addActionListener(e -> selectMachineProfile("Ofensiva"));
        defensive.addActionListener(e -> selectMachineProfile("Defensiva"));
        clumsy.addActionListener(e -> selectMachineProfile("Torpe"));
        expert.addActionListener(e -> selectMachineProfile("Experta"));
    }

    private void selectGameMode(String mode){
        selectedMode = mode;
        updateModeSelectionImages();
    }

    private void selectMachineProfile(String profile){
        selectedMachineProfile = profile;
        updateMachineSelectionImages();
    }

    private void updateModeSelectionImages(){
        if(pvsp == null || pvsm == null){
            return;
        }
        if("Jugador VS Máquina".equals(selectedMode)){
            pvsp.setIcon(pvspNoColorIcon);
            pvsm.setIcon(pvsmColorIcon);
        }
        else{
            pvsp.setIcon(pvspColorIcon);
            pvsm.setIcon(pvsmNoColorIcon);
        }
    }

    private void updateMachineSelectionImages(){
        if(offensive == null || defensive == null || clumsy == null || expert == null){
            return;
        }
        offensive.setIcon(offensiveNoColorIcon);
        defensive.setIcon(defensiveNoColorIcon);
        clumsy.setIcon(clumsyNoColorIcon);
        expert.setIcon(expertNoColorIcon);

        if("Ofensiva".equals(selectedMachineProfile)){
            offensive.setIcon(offensiveColorIcon);
        }
        else if("Defensiva".equals(selectedMachineProfile)){
            defensive.setIcon(defensiveColorIcon);
        }
        else if("Torpe".equals(selectedMachineProfile)){
            clumsy.setIcon(clumsyColorIcon);
        }
        else if("Experta".equals(selectedMachineProfile)){
            expert.setIcon(expertColorIcon);
        }
    }

    /**
     * Shows the game mode selection images (PvsP / PvsM)
     */
    public void showGameModes(){
        showingMachines = false;
        title.setText("SELECCIONA EL MODO DE JUEGO");
        continueButton.setVisible(true);
        updateModeSelectionImages();
        centerPanel.removeAll();
        centerPanel.add(modesPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Shows the 4 machine profile selection images
     */
    public void showMachineProfiles(){
        showingMachines = true;
        title.setText("SELECCIONA EL TIPO DE MÁQUINA");
        if(selectedMachineProfile == null || selectedMachineProfile.isEmpty()){
            selectedMachineProfile = "Ofensiva";
        }
        continueButton.setVisible(true);
        updateMachineSelectionImages();
        centerPanel.removeAll();
        centerPanel.add(machinesPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    public boolean isShowingMachines(){
        return showingMachines;
    }

    public String getSelectedMode(){
        return selectedMode;
    }

    public void setSelectedMode(String selectedMode){
        this.selectedMode = selectedMode;
        updateModeSelectionImages();
    }

    public String getSelectedMachineProfile(){
        return selectedMachineProfile;
    }

    public void setSelectedMachineProfile(String selectedMachineProfile){
        this.selectedMachineProfile = selectedMachineProfile;
        updateMachineSelectionImages();
    }

    public JButton getBackButton(){
        return back;
    }

    public JButton getContinueButton(){
        return continueButton;
    }

    public JButton getPvspButton(){
        return pvsp;
    }

    public JButton getPvsmButton(){
        return pvsm;
    }

    public JButton[] getMachineButtons(){
        return new JButton[]{offensive, defensive, clumsy, expert};
    }

    private JButton createImageButton(ImageIcon icon, String tooltip){
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createActionButton(String text, Color backgroundColor){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setPreferredSize(new Dimension(200, 55));
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}