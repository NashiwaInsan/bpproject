import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GameBotMain extends GameBase {
    private final boolean isBotFirst;
    private boolean isFirstBotMove = true;
    private Timer turnTimer;
    private final int TIME_LIMIT = 10; // detik
    public int timeLeft = TIME_LIMIT;

    public GameBotMain(String playerXName, boolean isBotFirst) {
        super(playerXName, "Bot");
        this.isBotFirst = isBotFirst;
        initGame();
        setupUI();
        newGame();

        if (isBotFirst) {
            currentPlayer = Seed.NOUGHT;
            makeBotMove();
        } else {
            currentPlayer = Seed.CROSS;
        }

        updateScoreLabel();
        updateStatusBar();
    }

    @Override
    public void handleClick(int x, int y) {
        int row = y / Cell.SIZE;
        int col = x / Cell.SIZE;

        if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS)
            return;

        if (currentState != State.PLAYING) {
            resetGame();
            return;
        }

        if (board.cells[row][col].content == Seed.NO_SEED && currentPlayer == Seed.CROSS) {
            SoundEffect.MOUSE_CLICK.play(5); // klik sound
            board.stepGame(Seed.CROSS, row, col);
            currentState = board.checkGameState(Seed.CROSS, row, col);

            if (currentState == State.CROSS_WON) playerXScore++;

            boardPanel.repaint();
            stopTurnTimer();
            updateScoreLabel();

            if (currentState == State.PLAYING) {
                currentPlayer = Seed.NOUGHT;
                updateStatusBar();
                botDelay(); // tunggu 1 detik, lalu bot jalan
            } else {
                updateStatusBar();
            }
        }
    }

    private void startNewGame() {
        newGame(); // reset papan dan status
        currentPlayer = isBotFirst ? Seed.NOUGHT : Seed.CROSS;
        isFirstBotMove = isBotFirst;

        updateScoreLabel();
        updateStatusBar();
        boardPanel.repaint();

        if (isBotFirst && currentPlayer == Seed.NOUGHT && currentState == State.PLAYING) {
            makeBotMove();
        } else if (currentPlayer == Seed.CROSS && currentState == State.PLAYING) {
            startTurnTimer();
        }
    }

    private void resetGame() {
        newGame(); // reset isi papan & status
        currentPlayer = isBotFirst ? Seed.NOUGHT : Seed.CROSS;
        isFirstBotMove = isBotFirst;
        updateScoreLabel();
        updateStatusBar();
        boardPanel.repaint();

        if (currentState == State.PLAYING) {
            if (isBotFirst && currentPlayer == Seed.NOUGHT) {
                botDelay(); // jalankan bot jika bot jalan duluan
            } else if (currentPlayer == Seed.CROSS) {
                startTurnTimer(); // mulai timer untuk pemain
            }
        }
    }

    private boolean outOfBounds(int row, int col) {
        return row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS;
    }

    private void makeBotMove() {
        int[] move = null;

        // 🔀 Langkah pertama: random
        if (isFirstBotMove) {
            isFirstBotMove = false; // pastikan hanya sekali
            move = getRandomEmptyCell();
        } else {
            // 1. Menang jika bisa
            move = findWinningMove(currentPlayer);

            // 2. Blokir lawan
            if (move == null) {
                Seed opponent = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                move = findWinningMove(opponent);
            }

            // 3. Tengah
            if (move == null && board.cells[1][1].content == Seed.NO_SEED) {
                move = new int[]{1, 1};
            }

            // 4. Corner
            if (move == null) {
                move = findEmptyCorner();
            }

            // 5. Edge
            if (move == null) {
                move = findEmptyEdge();
            }
        }

        // Jalankan langkah jika ditemukan
        if (move != null) {
            int row = move[0];
            int col = move[1];

            currentState = board.stepGame(currentPlayer, row, col);
            if (currentState == State.NOUGHT_WON) playerOScore++;
            currentPlayer = Seed.CROSS;

            updateScoreLabel();
            updateStatusBar();
            boardPanel.repaint();
        }
        if (currentPlayer == Seed.CROSS && currentState == State.PLAYING) {
            startTurnTimer();
        }
    }

    // Mengecek apakah ada langkah menang untuk pemain tertentu
    private int[] findWinningMove(Seed seed) {
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    board.cells[row][col].content = seed;
                    State state = board.checkGameState(seed, row, col);
                    board.cells[row][col].content = Seed.NO_SEED;

                    if (state == State.CROSS_WON && seed == Seed.CROSS ||
                            state == State.NOUGHT_WON && seed == Seed.NOUGHT) {
                        return new int[]{row, col};
                    }
                }
            }
        }
        return null;
    }

    // Ambil corner kosong
    private int[] findEmptyCorner() {
        int[][] corners = {{0,0}, {0,2}, {2,0}, {2,2}};
        for (int[] corner : corners) {
            if (board.cells[corner[0]][corner[1]].content == Seed.NO_SEED) {
                return corner;
            }
        }
        return null;
    }

    // Ambil sisi kosong
    private int[] findEmptyEdge() {
        int[][] edges = {{0,1}, {1,0}, {1,2}, {2,1}};
        for (int[] edge : edges) {
            if (board.cells[edge[0]][edge[1]].content == Seed.NO_SEED) {
                return edge;
            }
        }
        return null;
    }

    private int[] getRandomEmptyCell() {
        java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            int randomIndex = (int)(Math.random() * emptyCells.size());
            return emptyCells.get(randomIndex);
        }
        return null;
    }

    private void startTurnTimer() {
        if (turnTimer != null && turnTimer.isRunning()) {
            turnTimer.stop();
        }

        timeLeft = TIME_LIMIT;

        turnTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                updateStatusBar();

                if (timeLeft <= 0) {
                    turnTimer.stop();
                    currentState = State.NOUGHT_WON;
                    updateStatusBar();
                    JOptionPane.showMessageDialog(boardPanel, "Waktu habis! Kamu kalah!");
                    startNewGame();
                }
            }
        });


        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    private void botDelay() {
        Timer delay = new Timer(1000, e -> {
            ((Timer) e.getSource()).stop(); // hentikan delay timer
            makeBotMove(); // jalankan langkah bot
        });
        delay.setRepeats(false);
        delay.start();
    }
}