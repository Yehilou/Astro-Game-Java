import javax.swing.*;
import java.awt.*;

public class LevelSelector extends JFrame {
    private MenuPrincipal menuprincipal;

    public LevelSelector(MenuPrincipal menuPrincipal) {
        this.menuprincipal = menuPrincipal;

        setTitle("Choose level");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel panel = new JPanel() {
            private ImageIcon background = new ImageIcon("src/resources/Spacebackground.gif");

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        panel.setOpaque(false);


        JLabel title = new JLabel("SELECT YOUR LEVEL");
        title.setFont(new Font("Consolas", Font.BOLD, 50));
        title.setForeground(new Color(0,0,130));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createRigidArea(new Dimension(0, 40)));


        JButton level1Button = createLevelButton("level 1");
        JButton level2Button = createLevelButton("level 2");
        JButton level3Button = createLevelButton("level 3");

        panel.add(level1Button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(level2Button);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(level3Button);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));


        JButton backButton = new JButton("Back to Menu");
        stylizeButton(backButton);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.addActionListener(e -> {
            dispose();
            menuprincipal.setVisible(true);
        });
        panel.add(backButton);

        add(panel);
        setVisible(true);
    }

    private JButton createLevelButton(String levelName) {
        JButton button = new JButton(levelName);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        stylizeButton(button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));

        button.addActionListener(e -> {
            dispose();
            menuprincipal.stopMusic();

            int speed = 1;
            int duration = 90;
            int lives = 4;

            if (levelName.equalsIgnoreCase("level 2")) {
                speed = 2;
                duration = 150;
                lives = 3;
            } else if (levelName.equalsIgnoreCase("level 3")) {
                speed = 3;
                duration = 240;
                lives = 3;
            }

            new WindowGame(speed, duration , lives);
        });

        return button;
    }

    private void stylizeButton(JButton button) {
        button.setBackground(new Color(0, 0, 130));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }
}
