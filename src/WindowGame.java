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
    private Music music;
    private JPanel gamePanel;
    private int lives;
    private JPanel livesPanel;
    private ImageIcon heartIcon;

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
            dispose();
            new MenuPrincipal().setVisible(true);
        });
        add(backButton);

        // Lives panel (hearts)
        heartIcon = new ImageIcon("src/resources/images/livesHeart.png");
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
                    gameStarted = false;
                    GamePanel.gameStarted = false;
                    music.stop();
                    showEndScreen("src/resources/images/you_winn.png");

                    if (lives > 0) {
                        JOptionPane.showMessageDialog(this, "You Win!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Game Over!");
                    }
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

    public JPanel getGamePanel() {
        return gamePanel;
    }

    public void loseLife() {
        lives--;
        updateLivesDisplay();
        if (lives <= 0) {
            gameStarted = false;
            GamePanel.gameStarted = false;
            music.stop();
            showEndScreen("src/resources/images/game_over.png");
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

    private void showEndScreen(String imagePath) {
        if (menuAlreadyOpened) return;
        menuAlreadyOpened = true;

        JPanel endPanel = new JPanel() {
            private Image endImage = new ImageIcon(imagePath).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(endImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        endPanel.setBounds(0, 0, getWidth(), getHeight());
        endPanel.setLayout(null);
        setContentPane(endPanel);
        revalidate();
        repaint();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                dispose();
                new MenuPrincipal().setVisible(true);
            });
        }).start();
    }
}
