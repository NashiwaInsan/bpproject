import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginSystemGUI {
    private JFrame frame;
    private DatabaseHelper dbHelper;
    private Runnable onLoginSuccess;

    public LoginSystemGUI(DatabaseHelper dbHelper, Runnable onLoginSuccess) {
        this.dbHelper = dbHelper;
        this.onLoginSuccess = onLoginSuccess;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Login System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new CardLayout());

        frame.add(createMainPanel(), "Main");
        frame.add(createLoginPanel(), "Login");
        frame.add(createRegisterPanel(), "Register");

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Agar tombol melebar

        // Tombol Login
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(200, 40)); // Ukuran tetap
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(loginButton, gbc);

        // Tombol Register
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = 1;
        panel.add(registerButton, gbc);

        // Tombol Exit
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(200, 40));
        gbc.gridy = 2;
        panel.add(exitButton, gbc);

        // Action Listeners (sama seperti sebelumnya)
        loginButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Login");
        });

        registerButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Register");
        });

        exitButton.addActionListener(e -> {
            // Tampilkan dialog konfirmasi
            int confirm = JOptionPane.showConfirmDialog(
                    null, // Komponen parent (null untuk center di layar)
                    "you haven't even played it", // Pesan
                    "Exit Confirmation", // Judul dialog
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            // Jika user memilih YES
            if (confirm == JOptionPane.YES_OPTION) {
                int confirm1 = JOptionPane.showConfirmDialog(
                        null, // Komponen parent (null untuk center di layar)
                        "You can create an account if you don't have one yet", // Pesan
                        "Exit Confirmation", // Judul dialog
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm1== JOptionPane.YES_OPTION) {
                    System.exit(0); // Keluar dari aplikasi
                }
            }
            // Jika NO, tidak melakukan apa-apa (dialog akan tertutup)
        });

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan Field Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField userField = new JTextField(15); // Lebar kolom 15 karakter
        panel.add(userField, gbc);

        // Label dan Field Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passField = new JPasswordField(15); // Lebar kolom 15 karakter
        panel.add(passField, gbc);

        // Tombol Login dan Back
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Atur ukuran tombol agar konsisten
        Dimension buttonSize = new Dimension(100, 30);
        loginButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Rentang 2 kolom
        panel.add(buttonPanel, gbc);

        // Tambahkan action listener (sama seperti sebelumnya)
        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            try {
                if (dbHelper.authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    onLoginSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Main");
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label dan Field Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField userField = new JTextField(15);
        panel.add(userField, gbc);

        // Label dan Field Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passField = new JPasswordField(15);
        panel.add(passField, gbc);

        // Tombol Register dan Back
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        // Atur ukuran tombol sama seperti di login
        Dimension buttonSize = new Dimension(100, 30);
        registerButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Action listener untuk tombol register (sama seperti sebelumnya)
        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            try {
                if (dbHelper.registerUser(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Registration successful! Please login.");
                    CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
                    cl.show(frame.getContentPane(), "Login");
                } else {
                    JOptionPane.showMessageDialog(frame, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.show(frame.getContentPane(), "Main");
        });

        return panel;
    }
}