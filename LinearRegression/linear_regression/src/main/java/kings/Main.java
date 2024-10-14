package kings;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InteractivePointPlotter app = new InteractivePointPlotter();
            app.setVisible(true);
        });
    }
}