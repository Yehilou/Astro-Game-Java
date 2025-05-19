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

    private boolean invulnerable = false;
    private long invulnerableStartTime = 0;
    private final int INVULNERABLE_DURATION = 2000; // en millisecondes
    private boolean visible = true; // pour clignoter
    private long lastInvulnerableTime = 0;
    private final int BLINK_INTERVAL = 200;

    public Space_ship(JFrame frame) {
        this.frame = frame;
        speed = 5;
        x = (frame.getWidth() - 100) / 2 - 3;
        y = (int) (frame.getHeight() * 0.75);

        try {
            spaceShipFace = ImageIO.read(new File("src/resources/images/Space_Ships/SpaceShip_face.png"));
            spaceShipSide = ImageIO.read(new File("src/resources/images/Space_Ships/SpaceShip_side.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVueChange(boolean v) {
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


        if (invulnerable) {
            long elapsedTime = System.currentTimeMillis() - invulnerableStartTime;
            if (elapsedTime > INVULNERABLE_DURATION) {
                invulnerable = false;  // Désactive l'invulnérabilité après 2 secondes
            } else {
                // Clignotement : alterner la visibilité du vaisseau toutes les 200 ms
                long blinkTime = elapsedTime / BLINK_INTERVAL;
                if (blinkTime % 2 == 0) {
                    visible = true;
                } else {
                    visible = false;
                }
            }
        }


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
        if (keysPressed.contains(KeyEvent.VK_LEFT) && x - speed + 2 >= 0) {
            x -= speed;
        }
        if (keysPressed.contains(KeyEvent.VK_RIGHT) && x + speed + scaledWidth <= panelWidth) {
            x += speed;
        }

        // Vertical
        if (keysPressed.contains(KeyEvent.VK_UP) && y - speed >= 50) {
            y -= speed;
        }

        if (keysPressed.contains(KeyEvent.VK_DOWN) && y + scaledHeight - 95 <= lowerLimit) {
            y += speed;
        }
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
        // Gérer l'invulnérabilité et le clignotement
        if (invulnerable) {
            long elapsedTime = System.currentTimeMillis() - invulnerableStartTime;
            if (elapsedTime > INVULNERABLE_DURATION) {
                invulnerable = false;
                visible = true; // ✅ Forcer la visibilité quand invulnérabilité terminée
            } else {
                // Clignotement toutes les 200 ms
                long blinkTime = elapsedTime / BLINK_INTERVAL;
                visible = (blinkTime % 2 == 0);
            }
        } else {
            visible = true; // ✅ Ajoute cette ligne pour garantir la visibilité après l'invulnérabilité
        }



        // Dessiner le vaisseau uniquement s'il est visible
        if (visible) {
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


    }



    public Polygon getPolygon() {
        int width = vueChangee ? 120 : 100;
        int height = vueChangee ? 60 : 100;

        int drawX = x + transitionOffsetX;
        int drawY = y + transitionOffsetY;

        int scaledWidth = (int) (width * scale);
        int scaledHeight = (int) (height * scale);

        int[] xPoints;
        int[] yPoints;

        if (!vueChangee) {
            xPoints = new int[] {
                    drawX + scaledWidth / 2,                      // 0 : sommet (centre)
                    drawX + 5 * scaledWidth / 8,                  // 1 : pente droite cockpit
                    drawX + scaledWidth - scaledWidth / 6,        // 2 : aile droite
                    drawX + 3 * scaledWidth / 4,                  // 3 : bas droit
                    drawX + scaledWidth / 4,                      // 4 : bas gauche
                    drawX + scaledWidth / 6,                      // 5 : aile gauche
                    drawX + 3 * scaledWidth / 8,                  // 6 : pente gauche cockpit
                    drawX + scaledWidth / 2                       // 7 : retour sommet (ferme le polygon)
            };

            yPoints = new int[] {
                    drawY,                                        // 0 : sommet
                    drawY + scaledHeight / 5,                     // 1 : pente droite cockpit
                    drawY + scaledHeight / 3,                     // 2 : aile droite
                    drawY + 9 * scaledHeight / 10,                // 3 : bas droit
                    drawY + 9 * scaledHeight / 10,                // 4 : bas gauche
                    drawY + scaledHeight / 3,                     // 5 : aile gauche
                    drawY + scaledHeight / 5,                     // 6 : pente gauche cockpit
                    drawY                                         // 7 : (même que 0, ferme la forme)
            };
        } else {
            // VUE CÔTÉ — profil en forme de capsule/fusée
            xPoints = new int[] {
                    drawX + scaledWidth / 16,                       // 0 : nez très avant
                    drawX + scaledWidth / 3,                        // 1 : montée cockpit
                    drawX + 2 * scaledWidth / 3,                    // 2 : sommet cockpit (reculé)
                    drawX + 14 * scaledWidth / 16,                  // 3 : haut arrière (va plus loin)
                    drawX + 15 * scaledWidth / 16,                  // 4 : bord moteur arrière (nouveau point)
                    drawX + 13 * scaledWidth / 16,                  // 5 : bas arrière
                    drawX + scaledWidth / 3,                        // 6 : bas cockpit
                    drawX + scaledWidth / 16                        // 7 : avant bas
            };

            yPoints = new int[] {
                    drawY + scaledHeight / 2,                       // 0 : nez
                    drawY + scaledHeight / 3,                       // 1 : montée cockpit
                    drawY + scaledHeight / 4,                       // 2 : haut cockpit
                    drawY + scaledHeight / 3,                       // 3 : haut arrière
                    drawY + scaledHeight / 2,                       // 4 : extrémité arrière (moteur)
                    drawY + 2 * scaledHeight / 3,                   // 5 : bas moteur
                    drawY + 3 * scaledHeight / 4,                   // 6 : bas cockpit
                    drawY + scaledHeight / 2                        // 7 : avant bas
            };

        }

        return new Polygon(xPoints, yPoints, xPoints.length);
    }




    public int getHeight() {
        return spaceShipFace != null ? spaceShipFace.getHeight(null) : 100; // valeur de secours
    }


    public void setInvulnerable() {
        invulnerable = true;
        invulnerableStartTime = System.currentTimeMillis();
    }

    public boolean isInvulnerable() {
        if (invulnerable && System.currentTimeMillis() - invulnerableStartTime >= INVULNERABLE_DURATION) {
            invulnerable = false;
        }
        return invulnerable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getWidth() {
        int width = vueChangee ? 120 : 100;
        return (int) (width * scale);
    }

}
