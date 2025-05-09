

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Meteorites {
    public int x, y;
    public int width, height;
    private int speed;
    private boolean active = false;
    private static final int SCREEN_HEIGHT = 800;
    private BufferedImage image;
    private static BufferedImage meteoriteImage;

    private double angle = 0;
    private double rotationSpeed;
    private boolean sideView = false;

    static {
        try {
            meteoriteImage = ImageIO.read(new File("src/resources/images/meteorites.png"));
            if (meteoriteImage == null) {
                System.out.println("Erreur : mtete est null !");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Meteorites() {
        this.image = meteoriteImage;
    }

    public void update() {
        if (active) {
            if (sideView) {
                x += speed;
                if (x > 1300) active = false;
            } else {
                y += speed;
                if (y > SCREEN_HEIGHT) active = false;
            }

            angle += rotationSpeed;
            if (angle > 2 * Math.PI) angle -= 2 * Math.PI;
        }
    }

    public void draw(Graphics g) {

        if (active && image != null) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldTransform = g2d.getTransform();

            int centerX = x + width / 2;
            int centerY = y + height / 2;

            g2d.translate(centerX, centerY);
            g2d.rotate(angle);
            g2d.drawImage(image, -width / 2, -height / 2, width, height, null);
            g2d.setTransform(oldTransform);
        }
    }

    public void spawn(int screenWidth, int screenHeight, boolean sideView, Meteorites[] others) {
        System.out.println("Météorite activée : (" + x + ", " + y + ")");

        this.sideView = sideView;

        for (int attempt = 0; attempt < 50; attempt++) {
            this.width = 80 + (int)(Math.random() * 51);
            this.height = this.width;

            if (sideView) {
                this.x = -width;
                this.y = (int)(Math.random() * (screenHeight - height));
            } else {
                this.x = (int)(Math.random() * (screenWidth - width));
                this.y = -height;
            }

            float sizeRatio = (float)(width - 80) / 50f;
            this.speed = Math.max(2, (int)(2 + (1 - sizeRatio) * 3));

            if (width <= 100) {
                this.rotationSpeed = 0.1f;
            } else {
                this.rotationSpeed = 0.04f;
            }

            Rectangle newBounds = new Rectangle(x - 30, y - 30, width + 60, height + 60);
            boolean collision = false;

            for (Meteorites other : others) {
                if (other != this && other.active) {
                    Rectangle otherBounds = new Rectangle(other.x - 30, other.y - 30, other.width + 60, other.height + 60);
                    if (newBounds.intersects(otherBounds)) {
                        collision = true;
                        break;
                    }
                }
            }

            if (!collision) {
                this.active = true;
                return;
            }
        }

        this.active = false;
    }

    public Rectangle bounds(){
        return new Rectangle(x,y,width,height);
    }



    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }






}
