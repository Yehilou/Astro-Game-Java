import javax.swing.*;
import java.awt.*;

public class RulesWindow extends JFrame {

    public RulesWindow() {
        setTitle("Game Rules");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        JPanel backgroundPanel = new JPanel() {
            private ImageIcon background = new ImageIcon("src/resources/Spacebackground.gif");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());


        JTextArea rulesText = new JTextArea();
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        rulesText.setOpaque(false);
        rulesText.setForeground(Color.WHITE);
        rulesText.setFont(new Font("Consolas", Font.BOLD, 18));
        rulesText.setText(
                "              Welcome to Space Game!\n\n" +
                        "   Objective:\n" +
                        "   - Avoid asteroids and survive until timer is           gone.\n\n" +
                        "   - Collect power-ups (items) to gain advantages.\n\n" +
                        "   Controls:\n" +
                        "   - Arrow keys to move\n" +
                        "   - Spacebar to use items\n\n" +
                        "   Good luck, pilot!"
        );
        rulesText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        backgroundPanel.add(rulesText, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        setVisible(true);
    }
}
