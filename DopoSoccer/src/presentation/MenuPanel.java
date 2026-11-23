package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Represents a MenuPanel
 */
public class MenuPanel extends ViewPanel{

    //Buttons
    private JButton start;
    private JButton open;
    private JButton save;
    private JButton controls;
    private JButton exit;

    /**
     * Creates a new MenuPanel
     */
    public MenuPanel(){
        prepareView();
    }

    private void prepareView(){
        setLayout(new BorderLayout());
        add(prepareLeftArea(), BorderLayout.WEST);
        add(prepareRightArea(), BorderLayout.EAST);
        setBackgroundImage("resources/HomeArea/background.png");
    }

    private JPanel prepareLeftArea(){
        JPanel panel = new JPanel();
        start = createButton("Empezar");
        open = createButton("Abrir");
        save = createButton("Guardar");
        controls = createButton("Controles");
        exit = createButton("Salir");

        panel.setBorder(BorderFactory.createEmptyBorder(145, 40, 35, 0));
        panel.setLayout(new GridLayout(5, 1, 0, 10));
        panel.add(start);
        panel.add(open);
        panel.add(save);
        panel.add(controls);
        panel.add(exit);
        panel.setOpaque(false);
        return panel;
    }

    private JButton createButton(String phrase){
        JButton button = new JButton(phrase);
        button.setFont(new Font("Arial", Font.BOLD, 40));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setPreferredSize(new Dimension(300, 60));
        button.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel prepareRightArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(450, 0, 40, 20));
        JLabel credits = new JLabel("Hecho por: D. GARZÓN y W. MENDIVELSO");
        credits.setBackground(Color.BLUE);
        credits.setForeground(Color.WHITE);
        credits.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(credits);
        return panel;
    }

    public JButton getStartButton(){
        return start;
    }

    public JButton getOpenButton(){
        return open;
    }

    public JButton getSaveButton(){
        return save;
    }

    public JButton getExitButton(){
        return exit;
    }

    public JButton getControlsButton(){
        return controls;
    }
}