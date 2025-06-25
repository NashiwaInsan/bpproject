import java.util.*;

public class AIBot {
    private Seed mySeed;
    private Seed opponentSeed;

    public AIBot(Seed seed) {
        this.mySeed = seed;
        this.opponentSeed = (seed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    public int[] move(Board board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                if (board.cells[row][col].content == Seed.NO_SEED) {
                    board.cells[row][col].content = mySeed;
                    int score = minimax(board, 0, false);
                    board.cells[row][col].content = Seed.NO_SEED;
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{row, col};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing) {
        State result = board.checkWinner();
        if (result != State.PLAYING) {
            if (result == State.CROSS_WON) return (mySeed == Seed.CROSS) ? 10 - depth : depth - 10;
            if (result == State.NOUGHT_WON) return (mySeed == Seed.NOUGHT) ? 10 - depth : depth - 10;
            return 0; // draw
        }

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    if (board.cells[row][col].content == Seed.NO_SEED) {
                        board.cells[row][col].content = mySeed;
                        best = Math.max(best, minimax(board, depth + 1, false));
                        board.cells[row][col].content = Seed.NO_SEED;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    if (board.cells[row][col].content == Seed.NO_SEED) {
                        board.cells[row][col].content = opponentSeed;
                        best = Math.min(best, minimax(board, depth + 1, true));
                        board.cells[row][col].content = Seed.NO_SEED;
                    }
                }
            }
            return best;
        }
    }
}
