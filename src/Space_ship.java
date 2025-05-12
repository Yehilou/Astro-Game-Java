import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Space_ship {
    int x, y, speed;
    Image spaceShipFace;
    Image spaceShipSide;
    boolean vueChangee = false;
    private float scale = 1f;
    private int transitionOffsetX = 0;
    private int transitionOffsetY = 0;
    private JFrame frame;
    private final Set<Integer> keysPressed = new HashSet<>();

    public Space_ship(JFrame frame) {
        this.frame = frame;
        speed = 5;
        x = (frame.getWidth() - 100) / 2 - 3;
        y = (int) (frame.getHeight() * 0.75);

        try {
            spaceShipFace = ImageIO.read(new File("src/resources/images/SpaceShip_face.png"));
            spaceShipSide = ImageIO.read(new File("src/resources/images/SpaceShip_side.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVueChangee(boolean v) {
        vueChangee = v;
        repositionIfOutOfZone();
    }

    public void setTransitionProgress(float progress, boolean versCote) {
        scale = 1f - Math.abs(0.5f - progress) * 0.4f;
        if (versCote) {
            transitionOffsetX = (int) (30 * (progress - 0.5f));
            transitionOffsetY = 0;
        } else {
            transitionOffsetY = (int) (30 * (progress - 0.5f));
            transitionOffsetX = 0;
        }
    }

    public void resetTransition() {
        scale = 1f;
        transitionOffsetX = 0;
        transitionOffsetY = 0;
    }

    public void spaceShipControl(JPanel jPanel) {
        jPanel.setFocusable(true);
        jPanel.requestFocusInWindow();

        jPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });
    }

    public void updatePosition(JPanel panel) {
        if (!GamePanel.gameStarted) return;

        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // Dimensions du vaisseau avant mise à l'échelle
        int baseWidth = vueChangee ? 120 : 100;
        int baseHeight = vueChangee ? 60 : 100;

        // Appliquer l'échelle actuelle
        int scaledWidth = (int) (baseWidth * scale);
        int scaledHeight = (int) (baseHeight * scale);

        // Limite inférieure de Y (475) que tu veux
        int lowerLimit = 475;

        // Contrôles pour la vue face
        if (!vueChangee) {
            // Déplacement horizontal
            if (keysPressed.contains(KeyEvent.VK_LEFT) && x - speed + 2 >= 0)
                x -= speed;
            if (keysPressed.contains(KeyEvent.VK_RIGHT) && x + speed + scaledWidth <= panelWidth)
                x += speed;

            // Déplacement vertical
            if (keysPressed.contains(KeyEvent.VK_UP) && y - speed >= panelHeight / 2)
                y -= speed;

            // Limiter le mouvement vers le bas (empêcher de descendre au-delà de 475)
            if (keysPressed.contains(KeyEvent.VK_DOWN) && y + scaledHeight - 90 <= lowerLimit) {
                y += speed;
            }
        } else {
            // Contrôles pour la vue côté
            if (keysPressed.contains(KeyEvent.VK_LEFT) && x - speed >= panelWidth / 2)
                x -= speed;
            if (keysPressed.contains(KeyEvent.VK_RIGHT) && x + speed + scaledWidth <= panelWidth)
                x += speed;

            if (keysPressed.contains(KeyEvent.VK_UP) && y - speed >= 0)
                y -= speed;

            // Limiter le mouvement vers le bas (empêcher de descendre au-delà de 475)
            if (keysPressed.contains(KeyEvent.VK_DOWN) && y + scaledHeight - 95 <= lowerLimit) {
                y += speed;
            }
        }

        // Ajouter un log pour vérifier les valeurs des positions
        System.out.println("y: " + y + ", scaledHeight: " + scaledHeight);
    }





    private void repositionIfOutOfZone() {
        int panelWidth = frame.getWidth();
        int panelHeight = frame.getHeight();

        if (vueChangee) {
            if (x < panelWidth / 2) x = panelWidth - 120;
        } else {
            if (y < panelHeight / 2) y = panelHeight - 100;
        }
    }

    public void dessiner(Graphics g) {
        g.setColor(Color.GREEN);
        Rectangle r = bounds();
        g.drawRect(r.x, r.y, r.width, r.height);

        Image imageToDraw;
        int width, height;

        if (vueChangee) {
            imageToDraw = spaceShipSide;
            width = 120;
            height = 60;
        } else {
            imageToDraw = spaceShipFace;
            width = 100;
            height = 100;
        }

        if (imageToDraw != null) {
            Graphics2D g2d = (Graphics2D) g;
            int drawX = x + transitionOffsetX;
            int drawY = y + transitionOffsetY;
            int drawW = (int) (width * scale);
            int drawH = (int) (height * scale);
            g2d.drawImage(imageToDraw, drawX, drawY, drawW, drawH, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, 50, 50);
        }
    }

    public Rectangle bounds() {
        int width = vueChangee ? 90 : 70;
        int height = vueChangee ? 40 : 70;
        int offsetX = 15;
        int offsetY = vueChangee ? 10 : 15;
        return new Rectangle(x + offsetX, y + offsetY, width, height);
    }

    public int getHeight() {
        return spaceShipFace != null ? spaceShipFace.getHeight(null) : 100; // valeur de secours
    }
}
