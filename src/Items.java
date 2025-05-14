import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Items {
    private int x, y;
    private int width, height;
    private boolean active;
    private String type; // "coeur" ou "laser"
    private BufferedImage image;
    private boolean visible;


    public Items(String type) {
        this.type = type;
        this.active = false;

        try {
            if (type.equals("heart")) {
                image = ImageIO.read(new File("src/resources/images/items/heartItem.png"));
            } else if (type.equals("laser")) {
                image = ImageIO.read(new File("src/resources/images/items/laser.png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image != null) {
            width = image.getWidth() / 2;
            height = image.getHeight() / 2;
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();
            image = resizedImage;
        }  else {
            // Valeurs par défaut si image non chargée
            width = 16;
            height = 16;
        }
    }

    public void spawn(int panelWidth, int panelHeight) {
        this.x = (int)(Math.random() * (panelWidth - width));
        int minY = panelHeight / 4;
        int maxY = panelHeight - height;
        this.y = minY + (int)(Math.random() * (maxY - minY));
        this.active = true;

        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) { // 5 clignotements (200ms)
                    visible = !visible;
                    Thread.sleep(100);
                }
                visible = true; // visible en continu après le clignotement
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void draw(Graphics g) {
        if (active && visible && image != null) {
            g.drawImage(image, x, y, null);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public String getType() {
        return type;
    }
}
