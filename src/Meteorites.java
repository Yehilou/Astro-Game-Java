import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Meteorites {
    // Attributs pour la position, la taille et l'état de la météorite
    public int x, y; // Position
    public int width, height; // Dimensions de la météorite
    private int speed; // Vitesse de déplacement
    private boolean active = false; // Si la météorite est active (visible à l'écran)
    private static final int SCREEN_HEIGHT = 800; // Hauteur de l'écran pour la vue de face
    private BufferedImage image; // Image de la météorite
    private static BufferedImage meteoriteImage; // Image statique partagée pour toutes les météorites

    // Variables pour la rotation de la météorite
    private double angle = 0; // Angle de rotation
    private double rotationSpeed; // Vitesse de rotation
    private boolean sideView = false; // Indicateur pour la vue de côté ou de face

    // Chargement de l'image de la météorite (effectué une seule fois pour toutes les météorites)
    static {
        try {
            meteoriteImage = ImageIO.read(new File("src/resources/images/meteorites.png"));
        } catch (IOException e) {
            e.printStackTrace(); // Gestion des erreurs de chargement d'image
        }
    }

    // Constructeur de la météorite, charge l'image
    public Meteorites() {
        this.image = meteoriteImage;
    }

    // Mise à jour de la position de la météorite
    public void update() {
        if (!GamePanel.gameStarted) return; // Si le jeu n'est pas lancé, ne fait rien

        if (active) {
            if (sideView) {
                moveSideView(); // Déplacer selon la vue de côté
            } else {
                moveFaceView(); // Déplacer selon la vue de face
            }

            // Mise à jour de l'angle pour la rotation
            angle += rotationSpeed;
            if (angle > 2 * Math.PI) angle -= 2 * Math.PI; // Garde l'angle entre 0 et 2π
        }
    }

    // Déplace la météorite dans la vue de côté (horizontalement)
    private void moveSideView() {
        x += speed;
        if (x > 1300) active = false; // Désactive la météorite si elle dépasse l'écran
    }

    // Déplace la météorite dans la vue de face (verticalement)
    private void moveFaceView() {
        y += speed;
        if (y > SCREEN_HEIGHT) active = false; // Désactive la météorite si elle dépasse l'écran
    }

    // Dessine la météorite avec rotation
    public void draw(Graphics g) {
        if (active && image != null) {
            Graphics2D g2d = (Graphics2D) g; // Utilise Graphics2D pour plus de contrôles
            AffineTransform oldTransform = g2d.getTransform(); // Sauvegarde l'état initial du graphique

            // Calcul du centre de la météorite pour appliquer la rotation autour de ce point
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            g2d.translate(centerX, centerY); // Déplace le centre de rotation
            g2d.rotate(angle); // Applique la rotation
            g2d.drawImage(image, -width / 2, -height / 2, width, height, null); // Dessine l'image
            g2d.setTransform(oldTransform); // Restaure l'état graphique original
        }
    }

    // Crée une nouvelle météorite avec une position aléatoire et vérifie les collisions avec les autres
    public void spawn(int screenWidth, int screenHeight, boolean sideView, Meteorites[] others) {
        if (!GamePanel.gameStarted) return; // Si le jeu n'est pas lancé, ne fait rien

        this.sideView = sideView; // Enregistre si la vue est de côté ou de face

        for (int attempt = 0; attempt < 50; attempt++) {
            this.width = 80 + (int)(Math.random() * 51); // Taille aléatoire de la météorite
            this.height = this.width; // Rendre la météorite carrée

            // Positionnement de la météorite en fonction de la vue
            if (sideView) {
                this.x = -width; // Position hors de l'écran à gauche
                this.y = (int)(Math.random() * (screenHeight - height)); // Position aléatoire en hauteur
            } else {
                this.x = (int)(Math.random() * (screenWidth - width)); // Position aléatoire en largeur
                this.y = -height; // Position hors de l'écran en haut
            }

            // Calcul de la vitesse en fonction de la taille de la météorite
            float sizeRatio = (float)(width - 80) / 50f;
            this.speed = Math.max(2, (int)(2 + (1 - sizeRatio) * 3));

            // Définition de la vitesse de rotation
            if (width <= 100) {
                this.rotationSpeed = 0.1f; // Météorites petites et légères tournent rapidement
            } else {
                this.rotationSpeed = 0.04f; // Météorites plus grandes tournent plus lentement
            }

            // Vérification des collisions avec d'autres météorites
            Rectangle newBounds = new Rectangle(x - 30, y - 30, width + 60, height + 60);
            boolean collision = false;

            for (Meteorites other : others) {
                if (other != this && other.active) { // Ignore cette météorite et vérifie les autres
                    Rectangle otherBounds = new Rectangle(other.x - 30, other.y - 30, other.width + 60, other.height + 60);
                    if (newBounds.intersects(otherBounds)) {
                        collision = true; // Collision détectée
                        break;
                    }
                }
            }

            if (!collision) {
                this.active = true; // Si pas de collision, active la météorite
                return;
            }
        }

        this.active = false; // Si après 50 tentatives aucune position valide, la météorite reste inactive
    }

    // Retourne un Rectangle pour la détection de collisions, avec une réduction pour rendre la détection plus précise
    public Rectangle bounds() {
        int reducedWidth = (int)(width * 0.85);
        int reducedHeight = (int)(height * 0.85);
        int offsetX = (width - reducedWidth) / 2;
        int offsetY = (height - reducedHeight) / 2;
        return new Rectangle(x + offsetX, y + offsetY, reducedWidth, reducedHeight);
    }

    // Accesseurs et mutateurs pour l'état de la météorite
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false; // Désactive la météorite
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
