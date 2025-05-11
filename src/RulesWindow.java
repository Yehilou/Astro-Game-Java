import javax.swing.*;
import java.awt.*;

public class RulesWindow extends JFrame {

    public RulesWindow() {
        setTitle("Game Rules");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        JPanel backgroundPanel = new JPanel() {
            private final ImageIcon backGround = new ImageIcon("src/resources/Spacebackground.gif");
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backGround.getImage(), 0, 0, getWidth(), getHeight(), this);
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
                """
                                      Welcome to Space Game!
                        
                           Objective:
                           - Avoid asteroids and survive until timer is             gone.
                        
                           - Collect power-ups (items) to gain advantages.
                        
                           Controls:
                           - Arrow keys to move
                           - Spacebar to use items
                        
                           Good luck, pilot!\
                        """
        );
        rulesText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        backgroundPanel.add(rulesText, BorderLayout.CENTER);

        setContentPane(backgroundPanel);
        setVisible(true);
    }
}
