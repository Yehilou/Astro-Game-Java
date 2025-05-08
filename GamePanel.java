

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel {
    private Space_ship spaceShip;
    private BufferedImage spaceBackground;
    private int scrollSpeed = 2;
    private int y1, y2;
    private int x1, x2;
    private boolean sideView = false;
    private boolean initialized = false;
    private boolean inTransition = false;
    private float alphaTransition = 0f;
    private boolean switchToSideView;
    private boolean showWarning = false;

    private final int maxMeteorites = 10;
    private Meteorites[] meteorites = new Meteorites[maxMeteorites];
    private int frameCounter = 0;
    private boolean meteoritesActive = true;

    public void switchView() {
        if (inTransition) return;

        showWarning = true;
        repaint();

        meteoritesActive = false;
        clearMeteorites();

        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            showWarning = false;
            inTransition = true;
            switchToSideView = !sideView;
            alphaTransition = 0f;

            while (alphaTransition < 1f) {
                alphaTransition += 0.05f;
                spaceShip.setTransitionProgress(alphaTransition, switchToSideView);
                repaint();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sideView = switchToSideView;
            spaceShip.setVueChangee(sideView);

            if (sideView) {
                x1 = 0;
                x2 = -getWidth();
            } else {
                y1 = 0;
                y2 = -getHeight();
            }

            while (alphaTransition > 0f) {
                alphaTransition -= 0.05f;
                spaceShip.setTransitionProgress(alphaTransition, switchToSideView);
                repaint();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            spaceShip.resetTransition();
            inTransition = false;

            meteoritesActive = true;
            scheduleNextViewChange();
        }).start();
    }

    private void scheduleNextViewChange() {
        int minDelay = 2 * 60 * 1000;
        int maxDelay = 3 * 60 * 1000;
        int delay = minDelay + (int)(Math.random() * (maxDelay - minDelay));
        new Timer(delay, e -> switchView()).start();
    }

    public GamePanel(JFrame frame) {
        setFocusable(true);
        requestFocusInWindow();

        spaceShip = new Space_ship(frame);
        spaceShip.spaceShipControl(this);

        try {
            spaceBackground = ImageIO.read(new File("src/resources/fondespace.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < maxMeteorites; i++) {
            meteorites[i] = new Meteorites();
        }

        Timer initTimer = new Timer(50, e -> {
            if (getWidth() > 0 && getHeight() > 0 && spaceBackground != null && !initialized) {
                initialized = true;

                y1 = 0;
                y2 = -getHeight();
                x1 = 0;
                x2 = -getWidth();

                new Thread(() -> {
                    while (true) {
                        if (sideView) {
                            x1 += scrollSpeed;
                            x2 += scrollSpeed;
                            if (x1 >= getWidth()) x1 = x2 - getWidth();
                            if (x2 >= getWidth()) x2 = x1 - getWidth();
                        } else {
                            y1 += scrollSpeed;
                            y2 += scrollSpeed;
                            if (y1 >= getHeight()) y1 = y2 - getHeight();
                            if (y2 >= getHeight()) y2 = y1 - getHeight();
                        }

                        frameCounter++;
                        if (meteoritesActive && frameCounter % 60 == 0) {
                            spawnMeteorites();
                        }

                        for (Meteorites m : meteorites) {
                            m.update();

                            verifyIfCollision(spaceShip,meteorites);
                        }

                        // Meteorite collision detection
                        for (int i = 0; i < meteorites.length; i++) {
                            Meteorites m1 = meteorites[i];
                            if (!m1.isActive()) continue;
                            Rectangle r1 = new Rectangle(m1.getX(), m1.getY(), m1.getWidth(), m1.getHeight());

                            for (int j = i + 1; j < meteorites.length; j++) {
                                Meteorites m2 = meteorites[j];
                                if (!m2.isActive()) continue;
                                Rectangle r2 = new Rectangle(m2.getX(), m2.getY(), m2.getWidth(), m2.getHeight());
                                if (r1.intersects(r2)) {
                                    m1.deactivate();
                                    m2.deactivate();
                                    break;
                                }
                            }
                        }

                        repaint();
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

                int firstDelay = 60_000 + (int)(Math.random() * 120_000);
                new Timer(firstDelay, e2 -> switchView()).start();
            }
        });
        initTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (spaceBackground == null || !initialized) return;

        if (sideView) {
            g.drawImage(spaceBackground, x1, 0, getWidth(), getHeight(), null);
            g.drawImage(spaceBackground, x2, 0, getWidth(), getHeight(), null);
        } else {
            g.drawImage(spaceBackground, 0, y1, getWidth(), getHeight(), null);
            g.drawImage(spaceBackground, 0, y2, getWidth(), getHeight(), null);
        }

        for (Meteorites m : meteorites) {
            m.draw(g);


        }

        spaceShip.dessiner(g);

        if (inTransition) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, alphaTransition));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        if (showWarning) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.setColor(Color.RED);
            String warning = "⚠ View change imminent ⚠";
            int strWidth = g2d.getFontMetrics().stringWidth(warning);
            g2d.drawString(warning, (getWidth() - strWidth) / 2, 50);
        }

    }

    private void spawnMeteorites() {
        int activeCount = 0;
        for (Meteorites m : meteorites) {
            if (m.isActive()) activeCount++;
        }

        if (activeCount < maxMeteorites) {
            for (Meteorites m : meteorites) {
                if (!m.isActive()) {
                    m.spawn(getWidth(), getHeight(), sideView, meteorites);
                    break;
                }
            }
        }
    }

    private void clearMeteorites() {
        for (Meteorites m : meteorites) {
            m.deactivate();
        }
    }


    public void verifyIfCollision(Space_ship spaceShip, Meteorites[] meteorites){

        for(Meteorites m : meteorites){
            if(m.isActive() && spaceShip.bounds().intersects(m.bounds())){
                System.out.println("collision");
            }

        }
    }
}
