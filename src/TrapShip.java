import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TrapShip {
    private int x, y;
    private int width, height;
    private BufferedImage image;
    private boolean active = false;
    private int speed = 40;
    private long spawnTime;

    // Déclaration de la hitbox comme un Polygon
    private Polygon hitbox;

    public TrapShip() {
        try {
            image = ImageIO.read(new File("src/resources/images/Space_Ships/trapship.png"));
            // Réduire la taille de l'image du vaisseau
            image = resizeImage(image, 100, 100);  // Ajuste la taille selon tes préférences
            width = image.getWidth();
            height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Initialiser la hitbox à partir de la taille de l'image
        hitbox = new Polygon();
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image tmp = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public void spawn(int panelWidth, int panelHeight, boolean sideView) {
        active = true;
        spawnTime = System.currentTimeMillis();
        if (sideView) {
            x = -width;
            y = (int)(Math.random() * (panelHeight - height));
        } else {
            x = (int)(Math.random() * (panelWidth - width));
            y = -height;
        }

        // Mettre à jour la hitbox en fonction de la position du vaisseau
        updateHitbox();
    }

    public void update(boolean sideView) {
        if (!active) return;

        if (sideView) {
            x += speed;
        } else {
            y += speed;
        }

        // Mettre à jour la hitbox après le déplacement
        updateHitbox();

        // Désactive le vaisseau une fois qu'il est hors de l'écran
        if (x > 2000 || y > 1500) {  // Valeurs suffisamment grandes
            active = false;
        }
    }

    // Méthode pour mettre à jour la hitbox (Polygon) du vaisseau
    private void updateHitbox() {
        hitbox.reset();  // Réinitialiser la hitbox

        // Ajouter les points pour former la hitbox du vaisseau
        hitbox.addPoint(x, y);  // Coin supérieur gauche
        hitbox.addPoint(x + width, y);  // Coin supérieur droit
        hitbox.addPoint(x + width, y + height);  // Coin inférieur droit
        hitbox.addPoint(x, y + height);  // Coin inférieur gauche
    }

    // Vérifier la collision avec un autre Polygon
    public boolean checkCollision(Polygon other) {
        return hitbox.intersects(other.getBounds2D());
    }

    public void draw(Graphics g) {
        if (active && image != null) {
            g.drawImage(image, x, y, null);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Polygon getHitbox() {
        return hitbox;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
