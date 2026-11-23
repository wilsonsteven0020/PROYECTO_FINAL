package presentation;
import javax.swing.*;
import java.awt.*;


/**
 * Represents a StorePanel
 */
public class StorePanel extends ViewPanel{

    //JPlayerStoresPanels
    private PlayerStorePanel firstPlayerStorePanel;
    private PlayerStorePanel secondPlayerStorePanel;

    public StorePanel(){
        prepareView();
    }

    private void prepareView(){
        setBackgroundImage("resources/SelectCharacterZone/Background.png");  
        setLayout(new BorderLayout());
        firstPlayerStorePanel = new PlayerStorePanel();
        secondPlayerStorePanel = new PlayerStorePanel();
        JTabbedPane players = new JTabbedPane();
        players.add("Jugador 1", firstPlayerStorePanel);
        players.add("Jugador 2", secondPlayerStorePanel);
        add(players, BorderLayout.CENTER);
    }

    public PlayerStorePanel getFirstPlayerStorePanel(){
        return firstPlayerStorePanel;
    }

    public PlayerStorePanel getSecondPlayerStorePanel(){
        return secondPlayerStorePanel;
    }

}