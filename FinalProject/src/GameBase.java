import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameBase extends JPanel {
    public Board board;
    public State currentState;
    public Seed currentPlayer;

    protected JLabel statusLabel;
    protected JProgressBar turnProgressBar;
    protected JLabel scoreLabel;

    public String playerXName;
    public String playerOName;
    public int playerXScore = 0;
    public int playerOScore = 0;

    public BoardPanel boardPanel;
    private JPanel pauseOverlay;

    public GameBase(String playerXName, String playerOName) {
        this.playerXName = playerXName;
        this.playerOName = playerOName;
    }

    public void initGame() {
        this.board = new Board();
    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row)
            for (int col = 0; col < Board.COLS; ++col)
                board.cells[row][col].content = Seed.NO_SEED;

        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    public void setupUI() {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 70));
        setBackground(GameConstants.COLOR_BG);
        setBorder(BorderFactory.createLineBorder(new Color(100, 100, 130), 3));

        scoreLabel = new JLabel();
        scoreLabel.setFont(GameConstants.FONT_SCORE);
        scoreLabel.setForeground(new Color(240, 240, 255));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(40, 40, 70), getWidth(), getHeight(), new Color(30, 30, 50)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 45));
        topPanel.add(scoreLabel, BorderLayout.CENTER);

        // Status bar & timer
        statusLabel = new JLabel("Status...");
        statusLabel.setPreferredSize(new Dimension(147, 20));
        statusLabel.setMinimumSize(new Dimension(147, 20));
        statusLabel.setMaximumSize(new Dimension(147, 20));
        statusLabel.setFont(GameConstants.FONT_STATUS);
        statusLabel.setForeground(Color.DARK_GRAY);

        turnProgressBar = new JProgressBar(0, 10);
        turnProgressBar.setVisible(true);
        turnProgressBar.setValue(10);
        turnProgressBar.setString("10 detik");
        turnProgressBar.setFont(GameConstants.FONT_STATUS);
        turnProgressBar.setStringPainted(true);
        turnProgressBar.setForeground(new Color(0, 153, 0));
        turnProgressBar.setBorder(BorderFactory.createEmptyBorder());
        turnProgressBar.setPreferredSize(new Dimension(120, 18));

        JButton pauseButton = new JButton("Pause");
        pauseButton.setFont(GameConstants.FONT_STATUS);
        pauseButton.setBackground(GameConstants.COLOR_BG);
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pauseButton.addActionListener(e -> showPauseMenu());

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setBackground(GameConstants.COLOR_BG_STATUS);
        statusPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 36));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JPanel centerStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerStatus.setOpaque(false);
        centerStatus.add(turnProgressBar);

        statusPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());  // fleksibel tengah
        statusPanel.add(turnProgressBar);
        statusPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        statusPanel.add(pauseButton);
        statusPanel.add(Box.createRigidArea(new Dimension(10, 0)));

        boardPanel = new BoardPanel(board);
        setBackground(new Color(226, 234, 244));
        boardPanel.setBackground(new Color(226, 234, 244));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void updateScoreLabel() {
        scoreLabel.setText(playerXName + ": " + playerXScore + "   |   " + playerOName + ": " + playerOScore);
    }

    public void updateStatusBar() {
        if (statusLabel == null || turnProgressBar == null) return;

        if (this instanceof GameBotMain botGame) {
            turnProgressBar.setMaximum(10);
            turnProgressBar.setValue(botGame.timeLeft);
            turnProgressBar.setString(botGame.timeLeft + " detik");
            turnProgressBar.setForeground(botGame.timeLeft <= 3 ? Color.RED :
                    botGame.timeLeft <= 6 ? new Color(255, 140, 0) : new Color(0, 153, 0));
        }

        switch (currentState) {
            case PLAYING -> {
                if (currentPlayer == Seed.CROSS) {
                    statusLabel.setText("Giliran kamu (" + playerXName + ")");
                } else {
                    statusLabel.setText("Giliran Bot (" + playerOName + ")");
                }
            }
            case DRAW -> statusLabel.setText("Seri! Mulai lagi?");
            case CROSS_WON -> statusLabel.setText(playerXName + " menang!");
            case NOUGHT_WON -> statusLabel.setText(playerOName + " menang!");
        }
    }

    public void handleClick(int x, int y) { }

    public void showPauseMenu() {
        pauseOverlay = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        pauseOverlay.setLayout(new BoxLayout(pauseOverlay, BoxLayout.Y_AXIS));
        pauseOverlay.setOpaque(false);
        pauseOverlay.setFocusable(true);
        pauseOverlay.requestFocusInWindow();

        pauseOverlay.addMouseListener(new MouseAdapter() {});
        pauseOverlay.addMouseMotionListener(new MouseMotionAdapter() {});
        pauseOverlay.addMouseWheelListener(e -> {});

        JPanel buttonBox = new JPanel();
        buttonBox.setOpaque(false);
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));

        JButton continueButton = new JButton("Continue");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setFont(GameConstants.FONT_STATUS);
        continueButton.setMaximumSize(new Dimension(240, 50));
        continueButton.setBackground(GameConstants.COLOR_BG);
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.addActionListener(e -> hidePauseMenu());

        JButton exitButton = new JButton("Exit to Menu");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setFont(GameConstants.FONT_STATUS);
        exitButton.setMaximumSize(new Dimension(240, 50));
        exitButton.setBackground(GameConstants.COLOR_BG);
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        exitButton.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.getContentPane().removeAll();
            topFrame.setContentPane(new StartMenu(topFrame));
            topFrame.setSize(720, 800);
            topFrame.revalidate();
            topFrame.repaint();
        });

        buttonBox.add(Box.createVerticalGlue());
        buttonBox.add(continueButton);
        buttonBox.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonBox.add(exitButton);
        buttonBox.add(Box.createVerticalGlue());

        pauseOverlay.add(Box.createVerticalGlue());
        pauseOverlay.add(buttonBox);
        pauseOverlay.add(Box.createVerticalGlue());

        setLayout(null);
        add(pauseOverlay);
        SwingUtilities.invokeLater(() -> {
            pauseOverlay.setBounds(0, 0, getWidth(), getHeight());
            setComponentZOrder(pauseOverlay, 0);
            revalidate();
            repaint();
        });
    }

    public void hidePauseMenu() {
        if (pauseOverlay != null) {
            remove(pauseOverlay);
            pauseOverlay = null;
            setLayout(new BorderLayout());
            revalidate();
            repaint();
        }
    }
}

