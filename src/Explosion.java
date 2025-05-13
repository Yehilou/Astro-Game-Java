import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Explosion {
    private static final int Frame_Delay = 5;
    private static BufferedImage[] explosionFrame;
    private Polygon hitbox;


    private int x, y, width, heigth;
    private int currentFrame = 0;
    private int timer = 0;
    private boolean active = true;

    static {
        try {
            explosionFrame = new BufferedImage[5];
            for (int i = 0; i < explosionFrame.length; i++) {
                explosionFrame[i] = ImageIO.read(new File("src/resources/images/Explosions/explosion_frame_" + i + ".png"));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Explosion(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = height;

        int[] xPoints = {
                x + width / 2, x + 3 * width / 4, x + width,
                x + 3 * width / 4, x + width / 2, x + width / 4,
                x, x + width / 4
        };
        int[] yPoints = {
                y, y + height / 4, y + height / 2,
                y + 3 * height / 4, y + height, y + 3 * height / 4,

                y + height / 2, y + height / 4
        };
        hitbox = new Polygon(xPoints, yPoints, xPoints.length);

    }


    public void update(){
        if(!active) return;
        timer++;

        if(timer % Frame_Delay == 0){
            currentFrame++;

            if(currentFrame >= explosionFrame.length){
                active = false;
            }
        }
    }

    public void draw(Graphics g){
        if(active && currentFrame < explosionFrame.length){
            g.drawImage(explosionFrame[currentFrame], x, y, width, heigth, null);
        }
    }



    public boolean isActive() {
        return active;
    }

    public boolean collidesWith(Rectangle shipBounds) {
        if (!active) return false;

        int x = shipBounds.x;
        int y = shipBounds.y;
        int w = shipBounds.width;
        int h = shipBounds.height;

        // Teste les 4 coins du vaisseau
        return hitbox.contains(x, y) ||
                hitbox.contains(x + w, y) ||
                hitbox.contains(x, y + h) ||
                hitbox.contains(x + w, y + h);
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
        return heigth;
    }


    public Ellipse2D getHitbox() {
        int shrink = 30; // Réduction en pixels (ajuste selon le rendu)
        int adjustedWidth = width - shrink;
        int adjustedHeight = heigth - shrink;
        int centerX = x + width / 2;
        int centerY = y + heigth / 2;

        return new Ellipse2D.Double(
                centerX - adjustedWidth / 2,
                centerY - adjustedHeight / 2,
                adjustedWidth,
                adjustedHeight
        );
    }




}
