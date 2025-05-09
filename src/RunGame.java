//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.io.File;
//import java.io.IOException;
//
//public class RunGame {
//    private WindowGame windowGame;
//    private int scrollSpeed = 2;
//    private int backgroundY = 0;
//    private Image backgroundImage;
//    private boolean gameStarted = false;
//
//    private Space_ship spaceShip;
//    private final int maxMeteorites = 10;
//    private Meteorites[] meteorites = new Meteorites[maxMeteorites];
//
//    public RunGame(WindowGame windowGame) {
//        this.windowGame = windowGame;
//
//        // Charger l'image de fond
////        try {
////            backgroundImage = ImageIO.read(new File("src/resources/fondEspace.jpg"));
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        // Créer les météorites
//        for (int i = 0; i < maxMeteorites; i++) {
//            meteorites[i] = new Meteorites();
//        }
//
//
//        spaceShip = new Space_ship(windowGame);
//        spaceShip.spaceShipControl(windowGame.getGamePanel());
//
//
//        JPanel gamePanel = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//
//                if (gameStarted) {
////                    drawScrollingBackground(g);
//                    spaceShip.dessiner(g);
//                    for (Meteorites m : meteorites) {
//                        m.draw(g);
//                    }
//                }
//            }
//        };
//        gamePanel.setBounds(0, 0, 800, 600);
//        gamePanel.setOpaque(false); // On laisse WindowGame dessiner le fond
//        windowGame.add(gamePanel);
//
//        // Démarrer le défilement de l'arrière-plan
////        startBackgroundScroll(gamePanel);
//    }
//
////        private void startBackgroundScroll(JPanel panel) {
////            new Thread(() -> {
////                while (true) {
////                    try {
////                        Thread.sleep(16);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////
////                    if (gameStarted) {
////                        backgroundY += scrollSpeed;
////                        if (backgroundY >= backgroundImage.getHeight(null)) {
////                            backgroundY = 0;
////                        }
////                    }
////
////                    panel.repaint();
////                }
////            }).start();
////        }
////
////        private void drawScrollingBackground(Graphics g) {
////            int height = backgroundImage.getHeight(null);
////            g.drawImage(backgroundImage, 0, backgroundY, null);
////            g.drawImage(backgroundImage, 0, backgroundY - height, null);
////        }
//
//    // Cette méthode est appelée lorsque le jeu commence (pendant le décompte)
//    public void startGame() {
//        gameStarted = true;
//
//        // Logique du jeu (mise à jour des météorites, etc.)
//        new Thread(() -> {
//            while (gameStarted) {
//                // Mettre à jour les météorites
//                for (Meteorites m : meteorites) {
//                    m.update();
//                }
//                windowGame.repaint();
//                try {
//                    Thread.sleep(16);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
//
//    public void stopGame() {
//        gameStarted = false;
//    }
//}
