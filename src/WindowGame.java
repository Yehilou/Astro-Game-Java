import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WindowGame extends JFrame {
    private int speed;
    private int timerDuration;
    private int timeRemaining;
    private JLabel timerLabel;
    private Image backgroundImage;
    private int backgroundY = 0;
    private JLabel countdownLabel;
    private boolean gameStarted = false;

    public WindowGame(int speed, int timerDuration) {
        this.speed = speed;
        this.timerDuration = timerDuration;
        this.timeRemaining = timerDuration;

        setTitle("Space Game");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        backgroundImage = new ImageIcon("src/resources/fondespace.jpg").getImage();

        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(10, 10, 200, 30);
        add(timerLabel);

        countdownLabel = new JLabel("", SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Consolas", Font.BOLD, 100));
        countdownLabel.setForeground(Color.WHITE);
        countdownLabel.setBounds(0, 200, 800, 100);
        add(countdownLabel);


        JButton backButton = new JButton("<< Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBounds(680, 10, 100, 30);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            dispose();
            new MenuPrincipal().setVisible(true);
        });
        add(backButton);

        JPanel gamePanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawScrollingBackground(g);
            }
        };
        gamePanel.setBounds(0, 0, 800, 600);
        gamePanel.setOpaque(false);
        add(gamePanel);

        setVisible(true);

        startBackgroundScroll(gamePanel);
        startIntroCountdown();  // nouvelle méthode
    }

    private void drawScrollingBackground(Graphics g) {
        int height = backgroundImage.getHeight(null);
        g.drawImage(backgroundImage, 0, backgroundY, null);
        g.drawImage(backgroundImage, 0, backgroundY - height, null);
    }

    private void startBackgroundScroll(JPanel panel) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (gameStarted) {
                    backgroundY += speed;
                    if (backgroundY >= backgroundImage.getHeight(null)) {
                        backgroundY = 0;
                    }
                }

                panel.repaint();
            }
        }).start();
    }

    private void startIntroCountdown() {
        new Thread(() -> {
            try {
                playSound("321Go.wav");
                for (int i = 3; i > 0; i--) {
                    String number = String.valueOf(i);
                    SwingUtilities.invokeLater(() -> countdownLabel.setText(number));
                    Thread.sleep(1000);
                }

                SwingUtilities.invokeLater(() -> countdownLabel.setText("GO!"));
                Thread.sleep(1000);

                SwingUtilities.invokeLater(() -> {
                    countdownLabel.setVisible(false);
                    gameStarted = true;
                    startGameTimer();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startGameTimer() {
        new Thread(() -> {
            while (timeRemaining > 0) {
                try {
                    Thread.sleep(1000);
                    timeRemaining--;

                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText("Time: " + (timeRemaining / 60) + ":" + String.format("%02d", timeRemaining % 60));
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Time's up!");
            });
        }).start();
    }
    private void playSound(String fileName) {
        try {
            File soundFile = new File("src/resources/sounds/321Go.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
