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
        // Déplacer le décompte à gauche, en changeant les coordonnées de setBounds
        countdownLabel.setBounds(100, 200, 600, 100); // Déplacement de l'étiquette vers la gauche
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
        gamePanel = new GamePanel(this); // Utilisation de GamePanel pour gérer le jeu
        gamePanel.setBounds(0, 0, 800, 600);
        gamePanel.setOpaque(false);
        add(gamePanel);

        setVisible(true);

        // Démarrer le décompte et la musique du décompte
        startIntroCountdown();
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
                GamePanel.gameStarted = true;

                // Cacher le décompte et démarrer le jeu
                SwingUtilities.invokeLater(() -> {
                    countdownLabel.setVisible(false);  // Cacher l'étiquette du décompte
                    inCountdown = false;  // Passer en mode jeu
                    gameStarted = true;  // Le jeu commence ici
                    music.stop();  // Stopper la musique de décompte
                    music.playLoop("src/resources/sounds/battleMusic.wav");  // Démarrer la musique de jeu

                    // Démarrer le timer pour le jeu
                    startGameTimer();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Méthode pour démarrer le timer du jeu
    private void startGameTimer() {
        new Thread(() -> {
            while (gameStarted && timeRemaining > 0) {
                try {
                    Thread.sleep(1000);  // Attendre 1 seconde
                    timeRemaining--;
                    updateTimerLabel();  // Mettre à jour l'étiquette du timer
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Si le temps est écoulé, afficher la fin du jeu
            if (timeRemaining <= 0) {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Time's up!");
                    // Ajouter ici les actions à effectuer quand le temps est écoulé (ex: fin du jeu)
                });
            }
        }).start();
    }

    // Méthode pour mettre à jour l'étiquette du timer
    private void updateTimerLabel() {
        SwingUtilities.invokeLater(() -> {
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });
    }

    // Getter pour gamePanel
    public JPanel getGamePanel() {
        return gamePanel;
    }
}
