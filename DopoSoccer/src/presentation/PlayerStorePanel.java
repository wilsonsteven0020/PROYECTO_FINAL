package presentation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a StorePanel
 */
public class PlayerStorePanel extends ViewPanel{

    //JButtons
    private JButton back;
    private HashMap<String, JButton> buyButtons;

    //ArrayLists with upgrades stats
    private HashMap<String, ArrayList<JPanel>> upgradesStats;

    //JLabel
    private JLabel points;
    //Contains all prices
    private HashMap<String, JLabel> prices;

    /**
     * Creates a new StorePanel
    */
    public PlayerStorePanel(){
        prepareView();
    }

    private void prepareView(){
        setBackgroundImage("resources/StoreArea/storeBackground.png"); 
        setLayout(new BorderLayout());
        add(prepareNorthArea(), BorderLayout.NORTH);
        add(prepareCenterArea(), BorderLayout.CENTER);
    }

    private JPanel prepareCenterArea(){
        JPanel panel = new JPanel();
        prices = new HashMap<>();
        upgradesStats = new HashMap<>();
        buyButtons = new HashMap<>();
        panel.setLayout(new GridLayout(2, 2, 75,50));
        panel.setOpaque(false);
        buyButtons.put("Speed", new JButton("Comprar"));
        buyButtons.put("Jump", new JButton("Comprar"));
        buyButtons.put("Kick", new JButton("Comprar"));
        buyButtons.put("Survival", new JButton("Comprar"));
        upgradesStats.put("Speed", createArrayListWithJPanels());
        upgradesStats.put("Jump", createArrayListWithJPanels());
        upgradesStats.put("Kick", createArrayListWithJPanels());
        upgradesStats.put("Survival", createArrayListWithJPanels());
        prices.put("Speed", new JLabel("500", JLabel.CENTER));
        prices.put("Jump", new JLabel("500", JLabel.CENTER));
        prices.put("Kick", new JLabel("500", JLabel.CENTER));
        prices.put("Survival", new JLabel("500", JLabel.CENTER));
        panel.add(mergeInfoAndBuyZones(createInfoStore("Velocidad:", prices.get("Speed")),prepareBuyZones(upgradesStats.get("Speed"), prepareBuyButton(buyButtons.get("Speed")))));
        panel.add(mergeInfoAndBuyZones(createInfoStore("Salto:    ", prices.get("Jump")),prepareBuyZones(upgradesStats.get("Jump"), prepareBuyButton(buyButtons.get("Jump")))));
        panel.add(mergeInfoAndBuyZones(createInfoStore("Patada:   ", prices.get("Kick")),prepareBuyZones(upgradesStats.get("Kick"), prepareBuyButton(buyButtons.get("Kick")))));
        panel.add(mergeInfoAndBuyZones(createInfoStore("Vida:     ", prices.get("Survival")), prepareBuyZones(upgradesStats.get("Survival"), prepareBuyButton(buyButtons.get("Survival")))));
        
        panel.setBorder(BorderFactory.createEmptyBorder(40,50,50,50));

        return panel;
    }

    private JPanel mergeInfoAndBuyZones(JPanel info, JPanel buyZones){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        panel.add(info, BorderLayout.NORTH);
        panel.add(buyZones, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createInfoStore(String word, JLabel article){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(word, JLabel.LEFT);
        label.setForeground(Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
        article.setForeground(Color.WHITE);
        article.setFont(new Font("Arial", Font.BOLD, 25));
        article.setPreferredSize(new Dimension(110, 0));
        article.setBorder(BorderFactory.createEmptyBorder(0,15,0,0));
        panel.add(label, BorderLayout.WEST);
        panel.add(article, BorderLayout.EAST);
        return panel;
    }

    private JPanel prepareNorthArea(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("TIENDA", JLabel.CENTER);
        title.setForeground(Color.BLACK);
        title.setFont(new Font("Arial", Font.BOLD, 40));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10,110,0,0));

        points = new JLabel("Puntos: 0", JLabel.CENTER);
        points.setForeground(Color.WHITE);
        points.setFont(new Font("Arial", Font.BOLD, 25));
        points.setPreferredSize(new Dimension(200,60));

        JPanel pointsPanel = new JPanel();
        pointsPanel.setOpaque(false);
        pointsPanel.setLayout(new BorderLayout());
        pointsPanel.add(points, BorderLayout.CENTER);
        pointsPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,40));
        
        panel.add(titlePanel, BorderLayout.CENTER);
        panel.add(pointsPanel, BorderLayout.EAST);
        panel.add(prepareBackButton(), BorderLayout.WEST);
        panel.setBorder(BorderFactory.createEmptyBorder(30,0,10,0));
        return panel;
    }

    public JButton getBackButton(){
        return back;
    }
   
    private JPanel prepareBackButton(){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        back = new JButton("Volver");
        back.setFont(new Font("Arial", Font.BOLD, 15));
        back.setForeground(Color.WHITE);
        back.setBackground(Color.RED);
        back.setPreferredSize(new Dimension(100,40));
        back.setFocusPainted(false);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,0,0));
        panel.add(back);
        return panel;
    }

    private ArrayList<JPanel> createArrayListWithJPanels(){
        ArrayList<JPanel> arrayList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            JPanel label = new JPanel();
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            label.setBackground(Color.WHITE);
            arrayList.add(label);
        }
        return arrayList;
    }

    private JPanel prepareBuyButton(JButton button){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setBackground(Color.YELLOW);
        button.setPreferredSize(new Dimension(140,40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(button);
        return panel;
    }

    private JPanel prepareBuyZones(ArrayList<JPanel> arrayList, JPanel buttonZone){
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());
        panel.add(buttonZone, BorderLayout.EAST);
        buttonZone.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridLayout(1,10,0,10));
        for(int i=0; i <10; i++){
            centerPanel.add(arrayList.get(i));
        }
        arrayList.get(0).setBackground(Color.GREEN);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    public void setPointsText(String text){
        points.setText(text);
    }

    public void changeUpgradeStatsColor(String stat, int change){
        upgradesStats.get(stat).get(change).setBackground(Color.GREEN);
    }
    public void setPricesText(String stat, String text){
        prices.get(stat).setText(text);
    }

    public HashMap<String, JButton> getBuyButtons(){
        return buyButtons;
    }
    /**
     * Synchronizes a single stat row (green/white boxes, price label and buy button)
     * @param stat stat key ("Speed", "Jump", "Kick", "Survival")
     * @param level current stat level (1 to 10)
     * @param upgradeCost cost of the next upgrade
     */
    public void refreshStat(String stat, int level, int upgradeCost){
        ArrayList<JPanel> boxes = upgradesStats.get(stat);
        for(int i = 0; i < boxes.size(); i++){
            if(i < level){
                boxes.get(i).setBackground(Color.GREEN);
            }
            else{
                boxes.get(i).setBackground(Color.WHITE);
            }
        }
        JButton buyButton = buyButtons.get(stat);
        if(level >= 10){
            buyButton.setText("Máximo");
            buyButton.setEnabled(false);
            prices.get(stat).setText("");
        }
        else{
            buyButton.setText("Comprar");
            buyButton.setEnabled(true);
            prices.get(stat).setText(Integer.toString(upgradeCost));
        }
    }

    
}