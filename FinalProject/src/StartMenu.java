import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class StartMenu extends JPanel {
    public StartMenu(JFrame frame) {

        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BG); // Warna biru toska terang

        // Panel tengah untuk tombol dan logo
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Tambahkan logo Tic Tac Toe

        URL logoURL = getClass().getClassLoader().getResource("images/tictactoe_logo.png");
        if (logoURL != null) {
            ImageIcon originalLogo = new ImageIcon(logoURL);
            Image scaledImage = originalLogo.getImage().getScaledInstance(512, 512, Image.SCALE_SMOOTH);
            ImageIcon scaledLogoIcon = new ImageIcon(scaledImage);
            JLabel logoLabel = new JLabel(scaledLogoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(Box.createVerticalStrut(30));
            centerPanel.add(logoLabel);

        } else {
            System.err.println("Logo image not found!");
            JLabel fallback = new JLabel("Tic Tac Toe");
            fallback.setFont(new Font("SegoeUI", Font.BOLD, 36));
            fallback.setForeground(Color.WHITE);
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(Box.createVerticalStrut(30));
            centerPanel.add(fallback);
        }

        centerPanel.add(Box.createVerticalStrut(40));
        centerPanel.add(createButton("Play Now!", new Color(2,21,38), () -> startDuoGame(frame)));
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(createButton("Play with Bot", new Color(2,21,38), () -> startBotGame(frame)));

        add(centerPanel, BorderLayout.CENTER);

        // Panel bawah terpisah kiri dan kanan
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        // Panel kiri untuk tombol options
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        leftPanel.setOpaque(false);

        // Tombol options
        URL gearURL = getClass().getClassLoader().getResource("images/gear_icon.png");
        JButton btnOptions;
        if (gearURL != null) {
            ImageIcon originalIcon = new ImageIcon(gearURL);
            Image scaledImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            btnOptions = new JButton(scaledIcon);
        } else {
            System.err.println("Icon not found!!");
            btnOptions = new JButton("Options");
        }
        btnOptions.setPreferredSize(new Dimension(60, 40));
        btnOptions.setContentAreaFilled(false);
        btnOptions.setBorderPainted(false);
        btnOptions.setFocusPainted(false);
        btnOptions.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnOptions.addActionListener(e -> startSettingsMenu(frame));
        leftPanel.add(btnOptions);

        // Panel kanan untuk tombol exit
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rightPanel.setOpaque(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("SegoeUI", Font.BOLD, 16));
        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setPreferredSize(new Dimension(100, 40));
        exitButton.addActionListener(e -> System.exit(0));
        rightPanel.add(exitButton);

// Tambahkan ke bottomPanel
        bottomPanel.add(leftPanel, BorderLayout.WEST);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

// Tambahkan bottomPanel ke layout utama
        add(bottomPanel, BorderLayout.SOUTH);

    }

    private JButton createButton(String text, Color color, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setAlignmentY(10);
        button.setFont(new Font("SegoeUI", Font.BOLD, 20));
        button.setMaximumSize(new Dimension(240, 50));
        button.setBackground(GameConstants.COLOR_BG);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);

        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private void startDuoGame(JFrame frame) {
        JTextField playerXField = new JTextField("Player X", 15);
        JTextField playerOField = new JTextField("Player O", 15);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Enter name for Player X:"));
        panel.add(playerXField);
        panel.add(new JLabel("Enter name for Player O:"));
        panel.add(playerOField);

        int result = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Enter Player Names",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        String playerX = "Player X";
        String playerO = "Player O";

        if (result == JOptionPane.OK_OPTION) {
            String inputX = playerXField.getText().trim();
            String inputO = playerOField.getText().trim();

            if (!inputX.isEmpty()) playerX = inputX;
            if (!inputO.isEmpty()) playerO = inputO;

            frame.setContentPane(new GameMain(playerX, playerO));
            frame.setSize(400,400);

            frame.pack();
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        }else{
            frame.setContentPane(new StartMenu(frame));
            frame.revalidate();
            frame.repaint();
        }


    }

    private void startBotGame(JFrame frame) {
        String[] options = {"Player", "Bot"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Siapa yang jalan duluan?",
                "Pilih Giliran",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == JOptionPane.CLOSED_OPTION) {
            return; // cancel tidak lanjut
        }

        boolean isBotFirst = (choice == 1); // "Bot" dipilih

        JTextField nameField = new JTextField("Player X", 15);
        int result = JOptionPane.showConfirmDialog(
                frame,
                nameField,
                "Masukkan nama kamu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        String playerName = "Player X";
        if (result == JOptionPane.OK_OPTION) {
            String input = nameField.getText().trim();
            if (!input.isEmpty()) {
                playerName = input;
            }
        }

        frame.setContentPane(new GameBotMain(playerName, isBotFirst));
        frame.setSize(400, 400);
        frame.pack();
        frame.setResizable(false);
        frame.revalidate();
        frame.repaint();
    }

    private void startSettingsMenu(JFrame frame) {
        frame.setContentPane(new SettingsMenu(frame));
        frame.revalidate(); // refresh UI
        frame.repaint();

    }
}