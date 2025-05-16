import java.awt.*;
import java.util.ArrayList;

public class LaserManager {
    private ArrayList<MiniLaser> lasers = new ArrayList<>();

    private long lastShootTime = 0; // Temps du dernier tir
    private final long shootCooldown = 1000; // 1000 ms = 1 seconde

    public void shoot(int x, int y , boolean sideview) {
        if (GamePanel.canShoot) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= shootCooldown) {
                lasers.add(new MiniLaser(x, y , sideview));
                lastShootTime = currentTime; // Met à jour le dernier tir
            }
        }
    }

    public void update() {
        for (int i = 0; i < lasers.size(); i++) {
            lasers.get(i).update();
            if (lasers.get(i).isOffScreen()) {
                lasers.remove(i);
                i--;
            }
        }
    }

    public void draw(Graphics2D g2) {
        for (MiniLaser l : lasers) {
            l.draw(g2);
        }
    }

    public ArrayList<MiniLaser> getLasers() {
        return lasers;
    }
    public void updateLaserDirection(boolean sideView) {
        for (MiniLaser l : lasers) {
            l.setSideView(sideView);
        }
    }
}
