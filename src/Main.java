import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal M = new MenuPrincipal();
            M.setVisible(true);
        });
    }
}