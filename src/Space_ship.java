import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class Space_ship {
    // Déclaration des variables pour la position, la vitesse et les images du vaisseau
    int x, y, speed;
    Image spaceShipFace; // Image du vaisseau vu de face
    boolean vueChangee = false; // Indicateur de la vue (face ou côté)
    Image spaceShipSide; // Image du vaisseau vu de côté
    private float scale = 1f; // Facteur d'échelle pour l'animation de transition
    private int transitionOffsetX = 0; // Décalage horizontal pendant la transition
    private int transitionOffsetY = 0; // Décalage vertical pendant la transition
    private JFrame frame; // Référence à la fenêtre du jeu

    // Méthode pour changer la vue du vaisseau (face ou côté)
    public void setVueChangee(boolean v) {
        vueChangee = v; // Modifie l'état de la vue
        repositionIfOutOfZone(); // Vérifie si la position du vaisseau est valide après le changement de vue
    }

    // Méthode pour ajuster les paramètres de transition entre les vues
    public void setTransitionProgress(float progress, boolean versCote) {
        scale = 1f - Math.abs(0.5f - progress) * 0.4f; // Ajuste l'échelle du vaisseau selon le progrès de la transition
        if (versCote) {
            // Si la vue est vers le côté, ajuste l'offset horizontal
            transitionOffsetX = (int) (30 * (progress - 0.5f));
            transitionOffsetY = 0; // Pas de décalage vertical pour la vue de côté
        } else {
            // Si la vue est vers la face, ajuste l'offset vertical
            transitionOffsetY = (int) (30 * (progress - 0.5f));
            transitionOffsetX = 0; // Pas de décalage horizontal pour la vue de face
        }
    }

    // Réinitialise la transition en revenant à la vue initiale (sans échelle ni décalage)
    public void resetTransition() {
        scale = 1f; // Réinitialise l'échelle
        transitionOffsetX = 0; // Réinitialise le décalage horizontal
        transitionOffsetY = 0; // Réinitialise le décalage vertical
    }

    // Constructeur de la classe Space_ship
    public Space_ship(JFrame frame) {
        this.frame = frame; // Stocke la référence à la fenêtre principale du jeu
        speed = 20; // Définit la vitesse du vaisseau
        // Initialisation de la position du vaisseau à 3 pixels à gauche du centre de la fenêtre
        x = (frame.getWidth() - 100) / 2 - 3;  // largeur 100, déplacer à droite
        y = (int) (frame.getHeight() * 0.75); // Place le vaisseau à 75 % de la hauteur de la fenêtre

        // Chargement des images du vaisseau (face et côté)
        try {
            spaceShipFace = ImageIO.read(new File("src/resources/images/SpaceShip_face.png"));
            if (spaceShipFace == null) {
                System.out.println("Erreur : spaceShipFace est null !");
            }
            spaceShipSide = ImageIO.read(new File("src/resources/images/SpaceShip_side.png"));
        } catch (IOException e) {
            e.printStackTrace(); // Affiche l'erreur si le fichier n'est pas trouvé
        }
    }

    // Méthode pour gérer le contrôle du vaisseau par l'utilisateur
    public void spaceShipControl(JPanel jPanel) {
        jPanel.setFocusable(true); // Permet à la fenêtre de recevoir les entrées clavier
        jPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!GamePanel.gameStarted) return; // Bloque les touches si le jeu n'a pas commencé

                int key = e.getKeyCode(); // Récupère la touche pressée
                int panelWidth = jPanel.getWidth(); // Largeur du panneau de jeu
                int panelHeight = jPanel.getHeight(); // Hauteur du panneau de jeu

                // Si la vue n'a pas changé (vue de face)
                if (!vueChangee) {
                    // Contrôles du vaisseau en vue de face
                    if (key == KeyEvent.VK_LEFT && x - speed >= 0) { // Si la touche gauche est pressée et qu'on ne sort pas du bord gauche
                        x -= speed; // Déplace le vaisseau à gauche
                    } else if (key == KeyEvent.VK_RIGHT && x + speed + 99 <= panelWidth) { // Si la touche droite est pressée et qu'on ne sort pas du bord droit
                        x += speed; // Déplace le vaisseau à droite
                    } else if (key == KeyEvent.VK_UP && y - speed >= panelHeight / 2) { // Si la touche haut est pressée et qu'on ne sort pas du bord supérieur
                        y -= speed; // Déplace le vaisseau vers le haut
                    } else if (key == KeyEvent.VK_DOWN && y + speed + 99 <= panelHeight) { // Si la touche bas est pressée et qu'on ne sort pas du bord inférieur
                        y += speed; // Déplace le vaisseau vers le bas
                    }
                } else {
                    // Si la vue a changé (vue de côté), les contrôles sont légèrement ajustés
                    if (key == KeyEvent.VK_LEFT && x - speed >= panelWidth / 2) { // Ne pas sortir du côté gauche
                        x -= speed; // Déplace le vaisseau à gauche
                    } else if (key == KeyEvent.VK_RIGHT && x + speed + 119 <= panelWidth) { // Ne pas sortir du côté droit
                        x += speed; // Déplace le vaisseau à droite
                    } else if (key == KeyEvent.VK_UP && y - speed >= 0) { // Ne pas sortir du bord supérieur
                        y -= speed; // Déplace le vaisseau vers le haut
                    } else if (key == KeyEvent.VK_DOWN && y + speed + 59 <= panelHeight) { // Ne pas sortir du bord inférieur
                        y += speed; // Déplace le vaisseau vers le bas
                    }
                }

                jPanel.repaint(); // Repeint le panneau de jeu après le déplacement
            }
        });
    }

    // Méthode pour repositionner le vaisseau si sa position est en dehors de la zone de jeu
    private void repositionIfOutOfZone() {
        int panelWidth = frame.getWidth(); // Largeur du panneau
        int panelHeight = frame.getHeight(); // Hauteur du panneau

        if (vueChangee) {
            if (x < panelWidth / 2) { // Si le vaisseau est trop à gauche en vue de côté
                x = panelWidth - 120; // Le repositionne à droite
            }
        } else {
            if (y < panelHeight / 2) { // Si le vaisseau est trop haut en vue de face
                y = panelHeight - 100; // Le repositionne en bas
            }
        }
    }

    // Méthode pour dessiner le vaisseau dans la fenêtre
    public void dessiner(Graphics g) {
        g.setColor(Color.GREEN);
        Rectangle r = bounds();
        g.drawRect(r.x, r.y, r.width, r.height);
        Image imageToDraw; // Image à dessiner
        int width; // Largeur du vaisseau
        int height; // Hauteur du vaisseau

        // Sélectionne l'image et les dimensions en fonction de la vue
        if (vueChangee) {
            imageToDraw = spaceShipSide; // Image de côté
            width = 120; // Largeur de l'image de côté
            height = 60; // Hauteur de l'image de côté
        } else {
            imageToDraw = spaceShipFace; // Image de face
            width = 100; // Largeur de l'image de face
            height = 100; // Hauteur de l'image de face
        }

        // Dessine l'image du vaisseau
        if (imageToDraw != null) {
            Graphics2D g2d = (Graphics2D) g;
            int drawX = x + transitionOffsetX; // Position horizontale avec les décalages de transition
            int drawY = y + transitionOffsetY; // Position verticale avec les décalages de transition
            int drawW = (int) (width * scale); // Largeur ajustée selon l'échelle
            int drawH = (int) (height * scale); // Hauteur ajustée selon l'échelle
            g2d.drawImage(imageToDraw, drawX, drawY, drawW, drawH, null); // Dessine l'image
        } else {
            g.setColor(Color.RED); // Si l'image n'est pas disponible, dessine un carré rouge
            g.fillRect(x, y, 50, 50); // Dessine un carré rouge pour représenter le vaisseau
        }
    }

    // Méthode pour obtenir les limites du vaisseau (utilisé pour les collisions)
    public Rectangle bounds() {
        int width = vueChangee ? 90 : 70; // largeur réduite pour être plus proche de l’image réelle
        int height = vueChangee ? 40 : 70;
        int offsetX = 15;
        int offsetY = vueChangee ? 10 : 15;
        return new Rectangle(x + offsetX, y + offsetY, width, height);
    }



}
