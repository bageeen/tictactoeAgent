package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static model.Player.O;
import static model.Player.X;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Board}.
 *
 * <p>A game is set up by passing its moves as (row, column) pairs to {@code play}, and the
 * resulting grid is checked against a {@code "row0|row1|row2"} rendering where a dot is an
 * empty cell, e.g. {@code "XXX|OO.|..."}.
 */
class BoardTest {

    private static final String EMPTY_GRID = "...|...|...";

    private Board board;

    @BeforeEach
    void startNewGame() {
        board = new Board();
    }

    // ----- a new game ---------------------------------------------------------------------

    @Test
    public void aNewGameHasAnEmptyGrid() {
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void aNewGameStartsWithPlayerX() {
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void aNewGameHasNoWinner() {
        assertNull(board.getWinner());
    }

    @Test
    public void aNewGameIsInProgress() {
        assertTrue(board.isInProgressMode());
    }

    // ----- marking a cell -----------------------------------------------------------------

    @Test
    public void markPutsTheCurrentPlayerInTheChosenCell() {
        play(1, 1);
        assertEquals("...|.X.|...", grid());
    }

    @Test
    public void playersAlternateAfterEachMark() {
        play(0, 0,  1, 1);
        assertEquals("X..|.O.|...", grid());
    }

    @Test
    public void markGivesTheTurnToO() {
        play(0, 0);
        assertSame(O, board.getCurrentTurn());
    }

    @Test
    public void markGivesTheTurnBackToX() {
        play(0, 0,  1, 1);
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void everyCornerIsPlayable() {
        play(0, 0,  0, 2,  2, 0,  2, 2);
        assertEquals("X.O|...|X.O", grid());
    }

    // ----- moves that must be ignored -----------------------------------------------------

    @Test
    public void markIsIgnoredAboveTheTopRow() {
        play(-1, 0);
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void markIsIgnoredBelowTheBottomRow() {
        play(3, 0);
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void markIsIgnoredLeftOfTheFirstColumn() {
        play(0, -1);
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void markIsIgnoredRightOfTheLastColumn() {
        play(0, 3);
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void anOutOfBoundsMarkKeepsTheTurn() {
        play(3, 3);
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void markIsIgnoredOnAnAlreadyPlayedCell() {
        play(1, 1,  1, 1);
        assertEquals("...|.X.|...", grid());
    }

    @Test
    public void markOnAnAlreadyPlayedCellKeepsTheTurn() {
        play(1, 1,  1, 1);
        assertSame(O, board.getCurrentTurn());
    }

    // ----- the eight winning lines --------------------------------------------------------

    @Test
    public void xWinsOnTheTopRow() {
        playTopRowWinByX();
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheMiddleRow() {
        play(1, 0,  0, 0,  1, 1,  0, 1,  1, 2);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheBottomRow() {
        play(2, 0,  0, 0,  2, 1,  0, 1,  2, 2);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheLeftColumn() {
        play(0, 0,  0, 1,  1, 0,  1, 1,  2, 0);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheMiddleColumn() {
        play(0, 1,  0, 0,  1, 1,  1, 0,  2, 1);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheRightColumn() {
        play(0, 2,  0, 0,  1, 2,  1, 0,  2, 2);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheMainDiagonal() {
        play(0, 0,  0, 1,  1, 1,  0, 2,  2, 2);
        assertSame(X, board.getWinner());
    }

    // The board only looks at a diagonal when the cell just played sits on it, so the
    // anti-diagonal is checked from both of its ends.

    @Test
    public void xWinsOnTheAntiDiagonalEndingBottomLeft() {
        play(0, 2,  0, 0,  1, 1,  0, 1,  2, 0);
        assertSame(X, board.getWinner());
    }

    @Test
    public void xWinsOnTheAntiDiagonalEndingTopRight() {
        play(2, 0,  0, 0,  1, 1,  0, 1,  0, 2);
        assertSame(X, board.getWinner());
    }

    @Test
    public void oCanWinToo() {
        play(0, 0,  1, 0,  0, 1,  1, 1,  2, 2,  1, 2);
        assertSame(O, board.getWinner());
    }

    // ----- end of a won game --------------------------------------------------------------

    @Test
    public void winningEndsTheGame() {
        playTopRowWinByX();
        assertTrue(board.isInFinishedMode(), "a won game should be finished");
        assertFalse(board.isInProgressMode(), "a won game should no longer be in progress");
        assertFalse(board.isInDrawMode(), "a won game is not a draw");
    }

    @Test
    public void theWinnerKeepsTheTurn() {
        playTopRowWinByX();
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void marksAreIgnoredOnceTheGameIsWon() {
        playTopRowWinByX();
        play(2, 2);
        assertEquals("XXX|OO.|...", grid());
    }

    // ----- a game still running -----------------------------------------------------------

    @Test
    public void anUnfinishedGameHasNoWinner() {
        play(0, 0,  1, 1);
        assertNull(board.getWinner());
    }

    @Test
    public void anUnfinishedGameStaysInProgress() {
        play(0, 0,  1, 1);
        assertTrue(board.isInProgressMode());
    }

    // ----- draw ---------------------------------------------------------------------------

    @Test
    public void aFullGridWithoutAlignmentIsADraw() {
        playDrawnGame();
        assertTrue(board.isInDrawMode(), "a full grid without alignment should be a draw");
        assertFalse(board.isInProgressMode(), "a drawn game should no longer be in progress");
        assertFalse(board.isInFinishedMode(), "a draw is not a win");
    }

    @Test
    public void aDrawnGameFillsTheWholeGrid() {
        playDrawnGame();
        assertEquals("XOX|XOO|OXX", grid());
    }

    @Test
    public void aDrawLeavesNoWinner() {
        playDrawnGame();
        assertNull(board.getWinner());
    }

    @Test
    public void aDrawKeepsTheTurnOnTheLastPlayer() {
        playDrawnGame();
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void aWinOnTheLastFreeCellIsAWinNotADraw() {
        play(0, 0,  0, 2,  1, 0,  1, 1,  0, 1,  2, 1,  1, 2,  2, 2,  2, 0);
        assertTrue(board.isInFinishedMode(), "the last free cell completed the left column");
        assertSame(X, board.getWinner(), "X played the winning last cell");
    }

    // ----- restart ------------------------------------------------------------------------

    @Test
    public void restartClearsTheGrid() {
        playTopRowWinByX();
        board.restart();
        assertEquals(EMPTY_GRID, grid());
    }

    @Test
    public void restartClearsTheWinner() {
        playTopRowWinByX();
        board.restart();
        assertNull(board.getWinner());
    }

    @Test
    public void restartGivesTheTurnBackToX() {
        play(0, 0);
        board.restart();
        assertSame(X, board.getCurrentTurn());
    }

    @Test
    public void restartMakesTheGamePlayableAgain() {
        playTopRowWinByX();
        board.restart();
        play(2, 2);
        assertEquals("...|...|..X", grid());
    }

    // ----- choosing who plays next --------------------------------------------------------

    @Test
    public void setCurrentTurnDecidesWhoMarksNext() {
        board.setCurrentTurn(O);
        play(0, 0);
        assertEquals("O..|...|...", grid());
    }

    // ----- helpers ------------------------------------------------------------------------

    /** X takes the top row while O answers on the middle row: X wins on the fifth move. */
    private void playTopRowWinByX() {
        play(0, 0,  1, 0,  0, 1,  1, 1,  0, 2);
    }

    /** Fills the grid without ever aligning three marks: XOX / XOO / OXX. */
    private void playDrawnGame() {
        play(0, 0,  0, 1,  0, 2,  1, 1,  1, 0,  1, 2,  2, 1,  2, 0,  2, 2);
    }

    /** Marks the given cells in order, each move being a (row, column) pair. */
    private void play(int... rowColumnPairs) {
        for (int move = 0; move < rowColumnPairs.length; move += 2) {
            board.mark(rowColumnPairs[move], rowColumnPairs[move + 1]);
        }
    }

    /** Renders the grid as {@code "row0|row1|row2"}, a dot standing for an empty cell. */
    private String grid() {
        return row(0) + "|" + row(1) + "|" + row(2);
    }

    private String row(int row) {
        return cell(row, 0) + cell(row, 1) + cell(row, 2);
    }

    private String cell(int row, int column) {
        Player value = board.getCellValue(row, column);
        return value == null ? "." : value.name();
    }
}
