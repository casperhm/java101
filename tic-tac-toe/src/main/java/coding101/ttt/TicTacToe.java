package coding101.ttt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Console based 2-player Tic Tac Toe game.
 */
public class TicTacToe {

    /** Our board size. */
    private final int size;

    /**
     * The current game board.
     *
     * The board is implemented as a 2D array.
     * Each array element represents a "row" in the game, and there are {@code size} rows.
     * Each row array element represents a specific "column" in the game, and there are {@code size} columns.
     *
     * Visualize the 2D array like this:
     *
     * ┌─────────────────┐
     * │    ┌───────────┐│
     * │ 0: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * ├─────────────────┤
     * │    ┌───────────┐│
     * │ 1: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * ├─────────────────┤
     * │    ┌───────────┐│
     * │ 2: │ 0 │ 1 │ 2 ││
     * │    └───────────┘│
     * └─────────────────┘
     *
     * A {@code null} row element value represents an unused square, that can be "occupied" by
     * a {@code Status} value (that is, X or O).
     */
    private final Status[][] board;

    /** The next player move. */
    private Status next;

    /**
     * Constructor.
     * @param size the board size; use 3 for a "classic" board
     */
    public TicTacToe(int size) {
        this.size = size;
        board = new Status[size][];
        for (int i = 0; i < size; i++) {
            board[i] = new Status[size];
        }
        next = Status.X;
    }

    /**
     * Start the game.
     * @param in the input source, for example {@code new BufferedReader(new InputStreamReader(System.in)}
     * @param out the output destination, for example {@code new BufferedWriter(new OutputStreamWriter(System.out))}
     * @throws IOException if any I/O error occurs
     */
    public void go(BufferedReader in, BufferedWriter out) throws IOException {
        out.write("\nEnter moves like A1 (top-left) or B2 (center).\n\n");
        Utils.printBoard(board, out);
        while (true) {
            // prompt for next move
            var coord = Utils.promptNextMove(next, in, out);

            if (isMoveValid(coord)) {
                // update board with entered move
                board[coord.y()][coord.x()] = next;

                // print board
                out.write('\n');
                Utils.printBoard(board, out);

                if (isWon(coord)) {
                    out.write('\n');
                    out.write("Player %s wins!\n".formatted(next));
                    out.flush();
                    return;
                } else if (isDraw()) {
                    out.write('\n');
                    out.write("It's a draw!\n");
                    out.flush();
                    return;
                } else {
                    // switch to next player
                    next = (next == Status.X ? Status.O : Status.X);
                }
            } else {
                out.write("Invalid move! Try again.");
            }
        }
    }

    /**
     * Verify a coordinate can be filled, by validating it is within the board bounds and the given coordinate is not already occupied.
     * @param coord the coordinate to check
     * @return true if the given coordinate can be moved on
     */
    private boolean isMoveValid(Coordinate coord) {
        if (coord.x() >= size
                || coord.y() >= size
                || board[coord.y()][coord.x()] == Status.X
                || board[coord.y()][coord.x()] == Status.O) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Test if the game has been won.
     * @param lastMove the last move made
     * @return true if the game has been won
     */
    private boolean isWon(Coordinate lastMove) {
        var player = board[lastMove.y()][lastMove.x()];
        var row = lastMove.y();
        var column = lastMove.x();

        // keeps track how many squaers have been checked if is equal to size someone won
        var counter = 0;

        // look for a row win
        for (int i = 0; i < size; i++) {
            if (board[row][i] == player) {
                counter++;
            } else {
                break;
            }
        }
        if (counter == size) {
            return true;
        }

        // reset the counter
        counter = 0;

        // the same as previous loop but for columns
        for (int i = 0; i < size; i++) {
            if (board[i][column] == player) {
                counter++;
            } else {
                break;
            }
        }
        if (counter == size) {
            return true;
        }

        // reset the counter
        counter = 0;

        // diagonal wining left top to rigth bottom
        for (int i = 0; i < size; i++) {
            if (board[i][i] == player) {
                counter++;
            } else {
                break;
            }
        }
        if (counter == size) {
            return true;
        }

        // reset the counter
        counter = 0;

        // diagonal wining right top to left bottom
        for (int i = 0, j = size - 1; i < size; i++, j--) {
            if (board[i][j] == player) {
                counter++;
            } else {
                break;
            }
        }
        if (counter == size) {
            return true;
        }
        return false;
    }

    /**
     * Test if the game is a draw (tie/stalemate).
     * @return true if the game is a draw
     */
    private boolean isDraw() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // accept an integer size for the game board, defaulting to 3 if none provided
        var size = (args.length > 0 ? Integer.parseInt(args[0]) : 3);
        if (size < 2 || size > 26) {
            System.err.println("Invalid board size, please enter a size between 2 and 26.");
            System.exit(1);
        }
        var game = new TicTacToe(size);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out))) {
            game.go(in, out);
        } catch (java.io.IOException e) {
            System.err.printf("I/O exception: %s", e.toString());
        }
    }
}
