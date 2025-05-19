import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private Music music = new Music();

    public MenuPrincipal() {
        setTitle("space Game - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Définir un volume doux pour la musique du menu
        music.setLoopVolume(-15.0f);  // Plus doux
        music.playLoop("src/resources/sounds/lobyMusic.wav");

        JPanel backroundPanel = new JPanel() {
            private ImageIcon background = new ImageIcon("src/resources/images/Space/SpaceBackground.gif");

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background.getImage(), 0, 0, this);
            }
        };
        backroundPanel.setLayout(null);
        setContentPane(backroundPanel);

        JLabel titleLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String text = "SPACE GAME";
                Font font = new Font("Consolas", Font.BOLD, 80);
                g2d.setFont(font);

                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                g2d.setColor(Color.WHITE);
                for (int i = 0; i < 9; i++) {
                    int dx = (i % 3) - 1;
                    int dy = (i / 3) - 1;
                    if (dx != 0 || dy != 0) {
                        g2d.drawString(text, x + dx, y + dy);
                    }
                }

                g2d.setColor(new Color(0, 0, 130));
                g2d.drawString(text, x, y);
            }
        };
        titleLabel.setBounds(-20, 25, 800, 150);
        backroundPanel.add(titleLabel);

        JButton rulesButton = new JButton("Rules");
        rulesButton.setBounds(281, 280, 200, 60);
        styleButton(rulesButton);
        rulesButton.addActionListener(e -> new RulesWindow());
        backroundPanel.add(rulesButton);

        JButton playButton = new JButton("Play");
        playButton.setBounds(281, 210, 200, 60);
        styleButton(playButton);
        playButton.addActionListener(e -> {
            this.setVisible(false);
            music.stop(); // On arrête la musique du menu
            new LevelSelector(this);
        });
        backroundPanel.add(playButton);

        JButton exitButton = new JButton("Log Out");
        exitButton.setBounds(281, 350, 200, 60);
        styleButton(exitButton);
        exitButton.addActionListener(e -> System.exit(0));
        backroundPanel.add(exitButton);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(0, 0, 130));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }

    public void stopMusic() {
        music.stop();
    }
}
