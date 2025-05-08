import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class Space_ship {
    int x, y, speed;
    Image spaceShipFace;
    boolean vueChangee = false;
    Image spaceShipSide;
    private float scale = 1f;
    private int transitionOffsetX = 0;
    private int transitionOffsetY = 0;
    private JFrame frame;

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

    public Space_ship(JFrame frame) {
        this.frame = frame;
        speed = 20;
        x = (frame.getWidth() - 70) / 2 - 30;
        y = (int) (frame.getHeight() * 0.75);

        try {
            spaceShipFace = ImageIO.read(new File("src/resources/SpaceShip_face.png"));
            spaceShipSide = ImageIO.read(new File("src/resources/SpaceShip_side.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void spaceShipControl(JPanel jPanel) {
        jPanel.setFocusable(true);
        jPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int panelWidth = jPanel.getWidth();
                int panelHeight = jPanel.getHeight();

                if (!vueChangee) {
                    if (key == KeyEvent.VK_LEFT && x - speed >= 0) {
                        x -= speed;
                    } else if (key == KeyEvent.VK_RIGHT && x + speed + 79 <= panelWidth) { // 80px de large
                        x += speed;
                    } else if (key == KeyEvent.VK_UP && y - speed >= panelHeight / 2) {
                        y -= speed;
                    } else if (key == KeyEvent.VK_DOWN && y + speed + 79 <= panelHeight) {
                        y += speed;
                    }
                } else {
                    if (key == KeyEvent.VK_LEFT && x - speed >= panelWidth / 2) {
                        x -= speed;
                    } else if (key == KeyEvent.VK_RIGHT && x + speed + 99 <= panelWidth) { // 100px de large
                        x += speed;
                    } else if (key == KeyEvent.VK_UP && y - speed >= 0) {
                        y -= speed;
                    } else if (key == KeyEvent.VK_DOWN && y + speed + 49 <= panelHeight) {
                        y += speed;
                    }
                }

                jPanel.repaint();
            }
        });
    }

    private void repositionIfOutOfZone() {
        int panelWidth = frame.getWidth();
        int panelHeight = frame.getHeight();

        if (vueChangee) {
            if (x < panelWidth / 2) {
                x = panelWidth - 100;
            }
        } else {
            if (y < panelHeight / 2) {
                y = panelHeight - 80;
            }
        }
    }

    public void dessiner(Graphics g) {
        Image imageToDraw;
        int width;
        int height;

        if (vueChangee) {
            imageToDraw = spaceShipSide;
            width = 100;
            height = 50;
        } else {
            imageToDraw = spaceShipFace;
            width = 80;
            height = 80;
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


    public Rectangle bounds(){
        int width;
        int heigth;

        if(vueChangee){
            width = 100;
            heigth = 50;
        }else{
            width = 80;
            heigth = 80;


        }
        return new Rectangle(x,y,width,heigth);

    }

}
