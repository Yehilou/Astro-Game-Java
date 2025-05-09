import javax.swing.*;
import java.awt.*;

public class WindowGame extends JFrame {
    private int speed;
    private int timerDuration;
    private int timeRemaining;
    private JLabel timerLabel;
    private Image backgroundImage;
    private int backgroundY = 0;
    private JLabel countdownLabel;
    private boolean gameStarted = false;
    private boolean inCountdown = true;  // Flag pour savoir si on est en mode décompte ou jeu

    private Music music;

    private JPanel gamePanel; // Déclare un attribut pour gamePanel

    public WindowGame(int speed, int timerDuration) {
        this.speed = speed;
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;
        this.music = new Music(); // MODIFICATION

        setTitle("Space Game");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Charger l'image de fond
        backgroundImage = new ImageIcon("src/resources/fondespace.jpg").getImage();

        // Créer l'étiquette du timer
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(10, 10, 200, 30);
        add(timerLabel);

        // Créer l'étiquette du décompte
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Consolas", Font.BOLD, 100));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setBounds(0, 200, 800, 100);
        add(countdownLabel);

        // Créer le bouton "Retour au menu"
        JButton backButton = new JButton("<< Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBounds(680, 10, 100, 30);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            music.stop(); // MODIFICATION : on coupe la musique en revenant au menu
            dispose();
            new MenuPrincipal().setVisible(true);
        });
        add(backButton);

        // Créer le JPanel pour le jeu
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (inCountdown) {
                    drawStaticBackground(g);  // Afficher fond statique pendant le décompte
                } else if (gameStarted) {
                    drawScrollingBackground(g);  // Afficher fond défilant pendant le jeu
                }
            }
        };
        gamePanel.setBounds(0, 0, 800, 600);
        gamePanel.setOpaque(false);
        add(gamePanel);

        setVisible(true);

        // Démarrer le décompte et la musique du décompte
        startIntroCountdown();
    }

    // Méthode pour dessiner le fond statique pendant le décompte
    private void drawStaticBackground(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, null);  // Fond statique pendant le décompte
    }

    // Méthode pour dessiner le fond défilant pendant le jeu
    private void drawScrollingBackground(Graphics g) {
        int height = backgroundImage.getHeight(null);
        g.drawImage(backgroundImage, 0, backgroundY, null);
        g.drawImage(backgroundImage, 0, backgroundY - height, null);
    }

    // Méthode pour démarrer le défilement du fond après le décompte
    private void startBackgroundScroll() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(16);  // Approx. 60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (gameStarted) {
                    backgroundY += speed;  // Augmenter la position du fond
                    if (backgroundY >= backgroundImage.getHeight(null)) {
                        backgroundY = 0;  // Revenir au début de l'image lorsque le fond est complet
                    }
                }

                gamePanel.repaint();  // Redessiner le fond
            }
        }).start();
    }

    // Méthode pour démarrer le décompte et afficher "321 GO!"
    private void startIntroCountdown() {
        new Thread(() -> {
            try {
                music.playOnce("src/resources/sounds/321Go.wav");  // Jouer la musique du décompte

                // Afficher "3", "2", "1"
                for (int i = 3; i > 0; i--) {
                    String number = String.valueOf(i);
                    SwingUtilities.invokeLater(() -> countdownLabel.setText(number));
                    Thread.sleep(1000);  // Attendre 1 seconde avant de passer au suivant
                }

                // Afficher "GO!"
                SwingUtilities.invokeLater(() -> countdownLabel.setText("GO!"));
                Thread.sleep(1000);  // Attendre encore 1 seconde

                // Cacher le décompte et démarrer le jeu
                SwingUtilities.invokeLater(() -> {
                    countdownLabel.setVisible(false);  // Cacher l'étiquette du décompte
                    inCountdown = false;  // Passer en mode jeu
                    gameStarted = true;  // Le jeu commence ici
                    startBackgroundScroll();  // Démarrer le défilement du fond
                    music.stop();  // Stopper la musique de décompte
                    music.playLoop("src/resources/sounds/battleMusic.wav");  // Démarrer la musique de jeu
                    new RunGame(this).startGame();  // Lancer la logique du jeu
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Méthode pour démarrer le timer du jeu (compte à rebours)
    private void startGameTimer() {
        music.playLoop("src/resources/sounds/battleMusic.wav");  // Jouer la musique de fond du jeu
        new Thread(() -> {
            while (timeRemaining > 0) {
                try {
                    Thread.sleep(1000);  // Attendre 1 seconde
                    timeRemaining--;  // Diminuer le temps restant

                    // Mettre à jour le label du timer
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText("Time: " + (timeRemaining / 60) + ":" + String.format("%02d", timeRemaining % 60));
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            music.stop();  // Arrêter la musique lorsque le temps est écoulé

            // Afficher un message de fin de jeu
            SwingUtilities.invokeLater(() -> {
                music.stop();
                JOptionPane.showMessageDialog(this, "Time's up!");
            });
        }).start();
    }

    // Getter pour gamePanel
    public JPanel getGamePanel() {
        return gamePanel;
    }
}
