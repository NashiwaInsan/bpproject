import javax.swing.*;
import java.sql.SQLException;

public class GameManager {
    public static final String TITLE = "Tic Tac Toe";

    public static void main(String[] args) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper("jdbc:mysql://mysql-28d51a49-nashiwa001-5da0.k.aivencloud.com:21734/login_schema", "avnadmin", "AVNS_8ptoH1XuqBdzcuc6ubQ");

            SwingUtilities.invokeLater(() -> {
                new LoginSystemGUI(dbHelper, () -> {
                    SoundEffect.playBGMusic();
                    JFrame frame = new JFrame(TITLE);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setContentPane(new StartMenu(frame));
                    frame.pack();
                    frame.setSize(720, 900);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
            });
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
