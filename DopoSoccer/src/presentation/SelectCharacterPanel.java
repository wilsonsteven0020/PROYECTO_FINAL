package presentation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Represents a SelectCharacterPanel
 */
public class SelectCharacterPanel extends ViewPanel{

    //JButtons
    private JButton back;
    private JButton play;
    private JButton leftButtonFirstPlayer;
    private JButton rightButtonFirstPlayer;
    private JButton leftButtonSecondPlayer;
    private JButton rightButtonSecondPlayer;
    private JButton store;
    private JButton timeButton;
    private JButton goalsButton;
    private JButton smallFieldButton;
    private JButton mediumFieldButton;
    private JButton largeFieldButton;

    //JProgressBar
    private JProgressBar fieldSizeBar;

    //JLabel
    private JLabel matchTypeText;
    private JLabel fieldSizeLabel;
    private JLabel firstPlayerImage;
    private JLabel secondPlayerImage;
    private JLabel firstPlayerCountry;
    private JLabel secondPlayerCountry;

    //JTextField
    private JTextField number;

    //ImageIcons
    private ArrayList<ImageIcon> firstPlayer;
    private ArrayList<ImageIcon> smallIconsFirstPlayer;
    private ArrayList<ImageIcon> secondPlayer;
    private ArrayList<ImageIcon> smallIconsSecondPlayer;
    private ImageIcon clockColorIcon;
    private ImageIcon clockNoColorIcon;
    private ImageIcon ballColorIcon;
    private ImageIcon ballNoColorIcon;
    private ImageIcon greenArrowIcon;
    private ImageIcon orangeArrowIcon;
    private ImageIcon redArrowIcon;
    private ImageIcon grayArrowIcon;

    //Actual character
    private int firstP;
    private int secondP;

    //Match mode state (false = Por tiempo, true = Por goles)
    private boolean goalBased;

    //Selected field size ("Pequeña", "Mediana", "Grande")
    private String selectedFieldSize;

    //Country names
    private ArrayList<String> countries;

    /**
     * Creates a new SelectCharacterPanel
     */
    public SelectCharacterPanel(){
        prepareView();
        prepareController();
    }

    private void prepareView(){
        setBackgroundImage("resources/SelectCharacterZone/Background.png");
        preparePlayersCharactersImages();
        prepareMatchTypeIcons();
        prepareFieldSizeIcons();
        setLayout(new BorderLayout());
        add(prepareNorthArea(), BorderLayout.NORTH);
        add(prepareCentralArea(), BorderLayout.CENTER);
        add(prepareSouthArea(), BorderLayout.SOUTH);
        add(prepareFirstPlayerArea(), BorderLayout.WEST);
        add(prepareSecondPlayerArea(), BorderLayout.EAST);
    }

    private void preparePlayersCharactersImages(){
        firstP = 0;
        secondP = 0;
        firstPlayer = new ArrayList<>();
        secondPlayer = new ArrayList<>();
        smallIconsFirstPlayer = new ArrayList<>();
        smallIconsSecondPlayer = new ArrayList<>();

        for(int i = 1; i < 5; i++){
            firstPlayer.add(chargeIcon("resources/SelectCharacterZone/Character0" + Integer.toString(i) + ".png", 140, 140));
            secondPlayer.add(chargeIcon("resources/SelectCharacterZone/Character0" + Integer.toString(i) + ".png", 140, 140));
            smallIconsFirstPlayer.add(chargeIcon("resources/SelectCharacterZone/Character0" + Integer.toString(i) + ".png", 50, 50));
            smallIconsSecondPlayer.add(chargeIcon("resources/SelectCharacterZone/Character0" + Integer.toString(i) + ".png", 50, 50));
        }

        countries = new ArrayList<>();
        countries.add("Corea Del Sur");
        countries.add("Camerún");
        countries.add("Nigeria");
        countries.add("Estados Unidos");
    }

    private void prepareMatchTypeIcons(){
        goalBased = false;
        clockColorIcon = chargeIcon("resources/SelectCharacterZone/RelojColor.png", 60, 60);
        clockNoColorIcon = chargeIcon("resources/SelectCharacterZone/RelojNoColor.png", 60, 60);
        ballColorIcon = chargeIcon("resources/SelectCharacterZone/PelotaSimboloColor.png", 60, 60);
        ballNoColorIcon = chargeIcon("resources/SelectCharacterZone/PelotaSimboloNoColor.png", 60, 60);
    }

    private void prepareFieldSizeIcons(){
        selectedFieldSize = "Mediana";
        greenArrowIcon = chargeIcon("resources/SelectCharacterZone/FlechaVerde.png", 38, 38);
        orangeArrowIcon = chargeIcon("resources/SelectCharacterZone/FlechaNaranja.png", 38, 38);
        redArrowIcon = chargeIcon("resources/SelectCharacterZone/FlechaRoja.png", 38, 38);
        grayArrowIcon = chargeIcon("resources/SelectCharacterZone/FlechaGris.png", 38, 38);
    }

    private JPanel prepareSecondPlayerArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        leftButtonSecondPlayer = createChangeCharacterButton("←", 2);
        rightButtonSecondPlayer = createChangeCharacterButton("→", 2);

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setOpaque(false);
        leftButtonPanel.add(leftButtonSecondPlayer);
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(160, 0, 20, 20));
        panel.add(leftButtonPanel, BorderLayout.WEST);

        JPanel rightButtonPanel = new JPanel();
        rightButtonPanel.setOpaque(false);
        rightButtonPanel.add(rightButtonSecondPlayer);
        rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(160, 20, 20, 0));
        panel.add(rightButtonPanel, BorderLayout.EAST);

        secondPlayerImage = new JLabel();
        secondPlayerImage.setIcon(secondPlayer.get(0));
        secondPlayerImage.setBorder(BorderFactory.createEmptyBorder(75, 0, 0, 0));
        panel.add(secondPlayerImage, BorderLayout.CENTER);

        panel.add(prepareSecondPlayerCountryPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel prepareFirstPlayerArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        leftButtonFirstPlayer = createChangeCharacterButton("←", 1);
        rightButtonFirstPlayer = createChangeCharacterButton("→", 1);

        JPanel leftButtonPanel = new JPanel();
        leftButtonPanel.setOpaque(false);
        leftButtonPanel.add(leftButtonFirstPlayer);
        leftButtonPanel.setBorder(BorderFactory.createEmptyBorder(160, 0, 20, 20));
        panel.add(leftButtonPanel, BorderLayout.WEST);

        JPanel rightButtonPanel = new JPanel();
        rightButtonPanel.setOpaque(false);
        rightButtonPanel.add(rightButtonFirstPlayer);
        rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(160, 20, 20, 0));
        panel.add(rightButtonPanel, BorderLayout.EAST);

        firstPlayerImage = new JLabel();
        firstPlayerImage.setIcon(firstPlayer.get(0));
        firstPlayerImage.setBorder(BorderFactory.createEmptyBorder(75, 0, 0, 0));
        panel.add(firstPlayerImage, BorderLayout.CENTER);

        panel.add(prepareFirstPlayerCountryPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel prepareFirstPlayerCountryPanel(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.setBackground(Color.WHITE);
        firstPlayerCountry = new JLabel(countries.get(0), JLabel.CENTER);
        firstPlayerCountry.setFont(new Font("Arial", Font.BOLD, 35));
        firstPlayerCountry.setForeground(Color.WHITE);
        panel.add(firstPlayerCountry);
        return panel;
    }

    private JPanel prepareSecondPlayerCountryPanel(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.setBackground(Color.WHITE);
        secondPlayerCountry = new JLabel(countries.get(0), JLabel.CENTER);
        secondPlayerCountry.setFont(new Font("Arial", Font.BOLD, 35));
        secondPlayerCountry.setForeground(Color.WHITE);
        panel.add(secondPlayerCountry);
        return panel;
    }

    private JPanel prepareCentralArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2),
            BorderFactory.createEmptyBorder(10, 10, 12, 10)
        ));

        JPanel matchTypePanel = new JPanel();
        matchTypePanel.setOpaque(false);
        matchTypePanel.setLayout(new BorderLayout(0, 6));

        JPanel matchTitlesPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        matchTitlesPanel.setOpaque(false);

        JLabel titleMatchTypeText = new JLabel("Selecciona el tipo de partido:", JLabel.CENTER);
        titleMatchTypeText.setForeground(Color.WHITE);
        titleMatchTypeText.setFont(new Font("Arial", Font.BOLD, 25));

        matchTypeText = new JLabel("¿Cuánto tiempo? (En minutos)", JLabel.CENTER);
        matchTypeText.setForeground(Color.WHITE);
        matchTypeText.setFont(new Font("Arial", Font.BOLD, 20));

        matchTitlesPanel.add(titleMatchTypeText);
        matchTitlesPanel.add(matchTypeText);

        matchTypePanel.add(matchTitlesPanel, BorderLayout.NORTH);
        matchTypePanel.add(prepareNumberArea(), BorderLayout.CENTER);

        panel.add(matchTypePanel, BorderLayout.NORTH);
        panel.add(prepareFieldSizeArea(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel prepareFieldSizeArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 6));

        JPanel titlesPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlesPanel.setOpaque(false);

        JLabel titleFieldSizeText = new JLabel("Selecciona el tamaño de la cancha:", JLabel.CENTER);
        titleFieldSizeText.setForeground(Color.WHITE);
        titleFieldSizeText.setFont(new Font("Arial", Font.BOLD, 22));

        fieldSizeLabel = new JLabel("Mediana", JLabel.CENTER);
        fieldSizeLabel.setForeground(Color.WHITE);
        fieldSizeLabel.setFont(new Font("Arial", Font.BOLD, 25));

        titlesPanel.add(titleFieldSizeText);
        titlesPanel.add(fieldSizeLabel);

        // Selector box with 3 arrows aligned directly over 0%, 50% and 100% of the progress bar
        JPanel selectorWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        selectorWrapper.setOpaque(false);

        JPanel selectorBox = new JPanel(new BorderLayout(0, 4));
        selectorBox.setOpaque(false);

        JPanel arrowsPanel = new JPanel(new BorderLayout());
        arrowsPanel.setOpaque(false);
        arrowsPanel.setPreferredSize(new Dimension(300, 40));

        smallFieldButton = createArrowButton(grayArrowIcon, "Pequeña");
        mediumFieldButton = createArrowButton(orangeArrowIcon, "Mediana");
        largeFieldButton = createArrowButton(grayArrowIcon, "Grande");

        JPanel centerArrowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerArrowPanel.setOpaque(false);
        centerArrowPanel.add(mediumFieldButton);

        arrowsPanel.add(smallFieldButton, BorderLayout.WEST);
        arrowsPanel.add(centerArrowPanel, BorderLayout.CENTER);
        arrowsPanel.add(largeFieldButton, BorderLayout.EAST);

        fieldSizeBar = new JProgressBar(0, 100);
        fieldSizeBar.setValue(50);
        fieldSizeBar.setStringPainted(false);
        fieldSizeBar.setForeground(new Color(255, 165, 0));
        fieldSizeBar.setBackground(new Color(45, 45, 45));
        fieldSizeBar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        fieldSizeBar.setPreferredSize(new Dimension(262, 18));

        JPanel barPanel = new JPanel(new BorderLayout());
        barPanel.setOpaque(false);
        barPanel.setBorder(BorderFactory.createEmptyBorder(0, 19, 0, 19));
        barPanel.add(fieldSizeBar, BorderLayout.CENTER);

        selectorBox.add(arrowsPanel, BorderLayout.NORTH);
        selectorBox.add(barPanel, BorderLayout.SOUTH);
        selectorWrapper.add(selectorBox);

        panel.add(titlesPanel, BorderLayout.NORTH);
        panel.add(selectorWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JButton createArrowButton(ImageIcon icon, String tooltip){
        JButton button = new JButton(icon);
        button.setToolTipText(tooltip);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(38, 38));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel prepareNumberArea(){
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 45, 5));
        panel.setOpaque(false);

        timeButton = createMatchTypeButton(clockColorIcon);
        timeButton.setToolTipText("Por tiempo");

        number = new JTextField(3);
        number.setHorizontalAlignment(JTextField.CENTER);

        goalsButton = createMatchTypeButton(ballNoColorIcon);
        goalsButton.setToolTipText("Por goles");

        panel.add(timeButton);
        panel.add(number);
        panel.add(goalsButton);
        return panel;
    }

    private JPanel prepareSouthArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        play = createButton("JUGAR");
        panel.add(play);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        return panel;
    }

    private void prepareController(){
        preparePlayersAreaController();
        timeButton.addActionListener(e -> selectMatchTypeAction(false));
        goalsButton.addActionListener(e -> selectMatchTypeAction(true));
        smallFieldButton.addActionListener(e -> selectFieldSizeAction("Pequeña"));
        mediumFieldButton.addActionListener(e -> selectFieldSizeAction("Mediana"));
        largeFieldButton.addActionListener(e -> selectFieldSizeAction("Grande"));
    }

    private void selectMatchTypeAction(boolean isGoalMode){
        goalBased = isGoalMode;
        if(goalBased){
            timeButton.setIcon(clockNoColorIcon);
            goalsButton.setIcon(ballColorIcon);
            matchTypeText.setText("¿Cuántos goles?");
        }
        else{
            timeButton.setIcon(clockColorIcon);
            goalsButton.setIcon(ballNoColorIcon);
            matchTypeText.setText("¿Cuánto tiempo? (En minutos)");
        }
    }

    private void selectFieldSizeAction(String size){
        selectedFieldSize = size;
        fieldSizeLabel.setText(size);
        if(size.equals("Pequeña")){
            smallFieldButton.setIcon(greenArrowIcon);
            mediumFieldButton.setIcon(grayArrowIcon);
            largeFieldButton.setIcon(grayArrowIcon);
            fieldSizeBar.setForeground(new Color(46, 204, 113));
            fieldSizeBar.setValue(0);
        }
        else if(size.equals("Mediana")){
            smallFieldButton.setIcon(grayArrowIcon);
            mediumFieldButton.setIcon(orangeArrowIcon);
            largeFieldButton.setIcon(grayArrowIcon);
            fieldSizeBar.setForeground(new Color(255, 165, 0));
            fieldSizeBar.setValue(50);
        }
        else{
            smallFieldButton.setIcon(grayArrowIcon);
            mediumFieldButton.setIcon(grayArrowIcon);
            largeFieldButton.setIcon(redArrowIcon);
            fieldSizeBar.setForeground(new Color(231, 76, 60));
            fieldSizeBar.setValue(100);
        }
    }

    public boolean isGoalBased(){
        return goalBased;
    }

    public String getSelectedFieldSize(){
        return selectedFieldSize;
    }

    public JLabel getFirstPlayerCountryLabel(){
        return firstPlayerCountry;
    }

    public JLabel getSecondPlayerCountryLabel(){
        return secondPlayerCountry;
    }

    public JTextField getNumberTextField(){
        return number;
    }

    public JButton getPlayButton(){
        return play;
    }

    public JButton getBackButton(){
        return back;
    }

    public JButton getStoreButton(){
        return store;
    }

    private void preparePlayersAreaController(){
        leftButtonFirstPlayer.addActionListener(e -> previousCharacterFirstPlayer());
        rightButtonFirstPlayer.addActionListener(e -> nextCharacterFirstPlayer());
        leftButtonSecondPlayer.addActionListener(e -> previousCharacterSecondPlayer());
        rightButtonSecondPlayer.addActionListener(e -> nextCharacterSecondPlayer());
    }

    private void nextCharacterSecondPlayer(){
        leftButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(secondP));
        secondP = (secondP + 1) % 4;
        secondPlayerImage.setIcon(secondPlayer.get(secondP));
        secondPlayerCountry.setText(countries.get(secondP));
        if(secondP == 3){
            rightButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(0));
        }
        else{
            rightButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(secondP + 1));
        }
    }

    private void previousCharacterSecondPlayer(){
        rightButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(secondP));
        if(secondP - 1 < 0){
            secondP = 3;
        }
        else{
            secondP--;
        }
        secondPlayerImage.setIcon(secondPlayer.get(secondP));
        secondPlayerCountry.setText(countries.get(secondP));
        if(secondP == 0){
            leftButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(3));
        }
        else{
            leftButtonSecondPlayer.setIcon(smallIconsSecondPlayer.get(secondP - 1));
        }
    }

    private void nextCharacterFirstPlayer(){
        leftButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(firstP));
        firstP = (firstP + 1) % 4;
        firstPlayerImage.setIcon(firstPlayer.get(firstP));
        firstPlayerCountry.setText(countries.get(firstP));
        if(firstP == 3){
            rightButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(0));
        }
        else{
            rightButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(firstP + 1));
        }
    }

    private void previousCharacterFirstPlayer(){
        rightButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(firstP));
        if(firstP - 1 < 0){
            firstP = 3;
        }
        else{
            firstP--;
        }
        firstPlayerImage.setIcon(firstPlayer.get(firstP));
        firstPlayerCountry.setText(countries.get(firstP));
        if(firstP == 0){
            leftButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(3));
        }
        else{
            leftButtonFirstPlayer.setIcon(smallIconsFirstPlayer.get(firstP - 1));
        }
    }

    private JPanel prepareNorthArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("SELECCIONA LOS PERSONAJES", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 40));
        title.setBorder(BorderFactory.createEmptyBorder(20, 40, 0, 40));
        panel.add(title, BorderLayout.CENTER);
        panel.add(prepareBackButton(), BorderLayout.WEST);
        panel.add(prepareStoreButton(), BorderLayout.EAST);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return panel;
    }

    private JPanel prepareStoreButton(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        store = new JButton("Tienda");
        store.setFont(new Font("Arial", Font.BOLD, 15));
        store.setForeground(Color.BLACK);
        store.setBackground(Color.YELLOW);
        store.setPreferredSize(new Dimension(100, 40));
        store.setFocusPainted(false);
        store.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 20));
        panel.add(store);
        return panel;
    }

    private JPanel prepareBackButton(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        back = new JButton("Volver");
        back.setFont(new Font("Arial", Font.BOLD, 15));
        back.setForeground(Color.WHITE);
        back.setBackground(Color.RED);
        back.setPreferredSize(new Dimension(100, 40));
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
        panel.add(back);
        return panel;
    }

    private JButton createButton(String phrase){
        JButton button = new JButton(phrase);
        button.setFont(new Font("Arial", Font.BOLD, 30));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setPreferredSize(new Dimension(300, 80));
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createMatchTypeButton(ImageIcon icon){
        JButton button = new JButton(icon);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(60, 60));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createChangeCharacterButton(String command, int player){
        JButton button = new JButton();
        if(command.equals("←")){
            if(player == 1){
                button.setIcon(smallIconsFirstPlayer.get(3));
            }
            else{
                button.setIcon(smallIconsSecondPlayer.get(3));
            }
        }
        else{
            if(player == 1){
                button.setIcon(smallIconsFirstPlayer.get(1));
            }
            else{
                button.setIcon(smallIconsSecondPlayer.get(1));
            }
        }

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(50, 50));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

}