package presentation;
import javax.swing.*;
import java.awt.*;

public class ViewPanel extends JPanel{

    //Background image
    private Image backgroundImage;

    public void setBackgroundImage(String path){
        if(backgroundImage != null){
            backgroundImage.flush();
        }
        backgroundImage = createImage(path);
    }

    private Image createImage(String path){
        return new ImageIcon(path).getImage();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        if(backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public ImageIcon chargeIcon(String path, int width, int height){
        ImageIcon original = new ImageIcon(path);
        Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Charges an animated GIF icon keeping its original frames and animation loop
     * @param path file path of the GIF resource
     * @return animated ImageIcon
     */
    public ImageIcon chargeGif(String path){
        return new ImageIcon(path);
    }
}