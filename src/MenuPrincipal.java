import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class MenuPrincipal extends JFrame {

    private Clip clip;

    public MenuPrincipal() {
        setTitle("space Game - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        try {
            File audioFile = new File("src/resources/sounds/lobyMusic.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }


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
        rulesButton.setFont(new Font("Arial", Font.BOLD, 20));
        rulesButton.setBackground(new Color(0, 0, 130));
        rulesButton.setForeground(Color.WHITE);
        rulesButton.setFocusPainted(false);
        rulesButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        rulesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rulesButton.setOpaque(true);
        rulesButton.addActionListener(e -> new RulesWindow());
        backroundPanel.add(rulesButton);
        setContentPane(backroundPanel);




        JButton playButton = new JButton("Play");
        playButton.setBounds(281, 210, 200, 60);

        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setBackground(new Color(0, 0, 130));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setOpaque(true);

        playButton.addActionListener(e -> {
            this.setVisible(false);
            new LevelSelector(this);
        });
        backroundPanel.add(playButton);



        JButton exitButton = new JButton("Log Out");
        exitButton.setBounds(281,350,200,60);
        exitButton.setFont(new Font("Arial" , Font.BOLD , 20));
        exitButton.setBackground(new Color(0,0,130));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rulesButton.setOpaque(true);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        backroundPanel.add(exitButton);

    }
    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}


