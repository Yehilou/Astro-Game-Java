import java.awt.*;

public class MiniLaser {
    private int x, y;
    private final int speed = 10;
    private final int width = 4, height = 12;
    private boolean active = true;
    private boolean sideview;

    public MiniLaser(int x, int y ,  boolean sideview) {
        this.x = x;
        this.y = y;
        this.sideview = sideview;
    }

    public void update() {
       if (sideview){
           x -= speed;
       } else {
           y -= speed;

       }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.CYAN);
        g2.fillRect(x, y, width, height);
    }

    public boolean isOffScreen() {
        return y + height < 0;
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public void setSideView(boolean sideView) {
        this.sideview = sideView; // Permet de changer la direction même après création
    }
}
