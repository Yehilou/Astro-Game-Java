import javax.swing.*;
import java.awt.*;

public class WindowGame extends JFrame {
    private int speed;
    private int timerDuration;
    private int timeRemaining;
    private JLabel timerLabel;
    private JLabel countdownLabel;
    private boolean gameStarted = false;
    private boolean inCountdown = true;
    private boolean menuAlreadyOpened = false; // ✅ Nouveau flag
    private Music music =  new Music();
    private JPanel gamePanel;
    private int lives;
    private JPanel livesPanel;
    private ImageIcon heartIcon;
    private boolean isWin; // pour le son de la fin ( si il est false bas sa lancera game over )

    public WindowGame(int speed, int timerDuration, int lives) {
        this.speed = speed;
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;
        this.music = new Music();
        this.lives = lives;

        setTitle("Space Game");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // Timer label
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(10, 10, 200, 30);
        add(timerLabel);

        // Countdown label
        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Consolas", Font.BOLD, 100));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setBounds(100, 200, 600, 100);
        add(countdownLabel);

        // Back to menu button
        JButton backButton = new JButton("<< Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBounds(680, 10, 100, 30);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            music.stop();
            ((GamePanel) gamePanel).stopGameThread(); // stoppe proprement le thread
            dispose();
            new MenuPrincipal().setVisible(true);
        });
        add(backButton);

        // Lives panel (hearts)
        heartIcon = new ImageIcon("src/resources/images/Heart/livesHeart.png");
        Image scaledImage = heartIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        heartIcon = new ImageIcon(scaledImage);
        livesPanel = new JPanel();
        livesPanel.setBounds(10, 50, 200, 40);
        livesPanel.setOpaque(false);
        livesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(livesPanel);
        updateLivesDisplay();

        // Game panel
        gamePanel = new GamePanel(this, this);
        gamePanel.setBounds(0, 0, 800, 600);
        gamePanel.setOpaque(false);
        add(gamePanel);

        setVisible(true);

        // Start countdown
        startIntroCountdown();
    }

    private void startIntroCountdown() {
        new Thread(() -> {
            try {
                music.playOnce("src/resources/sounds/321Go.wav");

                for (int i = 3; i > 0; i--) {
                    String number = String.valueOf(i);
                    SwingUtilities.invokeLater(() -> countdownLabel.setText(number));
                    Thread.sleep(1000);
                }

                SwingUtilities.invokeLater(() -> countdownLabel.setText("GO!"));
                Thread.sleep(1000);
                GamePanel.gameStarted = true;

                SwingUtilities.invokeLater(() -> {
                    countdownLabel.setVisible(false);
                    inCountdown = false;
                    gameStarted = true;
                    music.stop();
                    music.setLoopVolume(-15.0f);
                    music.playLoop("src/resources/sounds/battleMusic.wav");
                    startGameTimer();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startGameTimer() {
        new Thread(() -> {
            while (gameStarted && timeRemaining > 0) {
                try {
                    Thread.sleep(1000);
                    timeRemaining--;
                    updateTimerLabel();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (timeRemaining <= 0) {
                SwingUtilities.invokeLater(() -> {
                    isWin = true;
                    gameStarted = false;
                    GamePanel.gameStarted = false;
                    music.stop();
                    showEndScreen("src/resources/images/Game_Ends/game_win.png" , isWin);

                });
            }
        }).start();
    }

    private void updateTimerLabel() {
        SwingUtilities.invokeLater(() -> {
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });
    }

    public void addlife(){ // pour rajoute une vie
        lives++;
        updateLivesDisplay();
    }

    public void loseLife() {
        music.playOnce("src/resources/sounds/lostlife.wav");
        lives--;
        updateLivesDisplay();
        if (lives <= 0) {
            isWin = false;
            gameStarted = false;
            GamePanel.gameStarted = false;
            music.stop();
            showEndScreen("src/resources/images/Game_Ends/game_lose.png" , isWin);
        }
    }

    private void updateLivesDisplay() {
        livesPanel.removeAll();
        for (int i = 0; i < lives; i++) {
            JLabel heartLabel = new JLabel(heartIcon);
            livesPanel.add(heartLabel);
        }
        livesPanel.revalidate();
        livesPanel.repaint();
    }

    private void showEndScreen(String imagePath , boolean isWin) {
        if (menuAlreadyOpened) return;
        menuAlreadyOpened = true;

        if (isWin){
            music.playOnce("src/resources/sounds/you_win.wav");
        } else {
            music.playOnce("src/resources/sounds/game_over.wav");
        }

        // Panneau avec fond spatial (on suppose que le fond était déjà dans gamePanel)
        JPanel endPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                gamePanel.paint(g); // Réutilise le fond spatial actuel
            }
        };

        endPanel.setBounds(0, 0, getWidth(), getHeight());

        // Image clignotante (game over ou win)
        JLabel flashingLabel = new JLabel(new ImageIcon(imagePath));
        flashingLabel.setBounds(150, 50, 500, 500); // ajuste les positions selon le design
        endPanel.add(flashingLabel);

        setContentPane(endPanel);
        revalidate();
        repaint();

        // Thread pour faire clignoter l'image
        new Thread(() -> {
            int duration = 4000; // 4 secondes
            int interval = 300;  // toutes les 300 ms
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < duration) {
                SwingUtilities.invokeLater(() -> flashingLabel.setVisible(!flashingLabel.isVisible()));
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(() -> {
                ((GamePanel) gamePanel).stopGameThread(); // stoppe proprement le thread
                dispose();
                new MenuPrincipal().setVisible(true);
            });
        }).start();
    }

}
