package com.thg.accelerator23.connectn.ai.connexmachina;

import static com.thehutgroup.accelerator.connectn.player.Counter.O;
import static com.thehutgroup.accelerator.connectn.player.Counter.X;
import static com.thg.accelerator23.connectn.ai.connexmachina.ConnexMachina.legalMoves;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class ConnexMachinaTest {

    ConnexMachina connexMachina = new ConnexMachina(O);

    @Test
    public void non_winning_move_returns_null_as_winner() throws InvalidMoveException {
        int numInARow = 4;
        int width = 5;
        int height = 5;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        counters[4] = new Counter[]{null, null, null, null, null};
        counters[3] = new Counter[]{null, X, null, null, null};
        counters[2] = new Counter[]{null, O, null, null, null};
        counters[1] = new Counter[]{null, O, null, null, null};
        counters[0] = new Counter[]{null, X, null, X, null};
        counters = rotateBoard(counters);

        Board board = new Board(new Board(counters, new GameConfig(width, height, numInARow)), 1, O);
        GameState gameState = boardAnalyser.calculateGameState(board);

        assertEquals(0, connexMachina.getWinner(gameState));
        assertFalse(connexMachina.isTerminalNode(board));
    }

    @Test
    public void vertical_winning_move_returns_winner() throws InvalidMoveException {
        int numInARow = 4;
        int width = 5;
        int height = 5;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        counters[4] = new Counter[]{null, null, null, null, null};
        counters[3] = new Counter[]{null, O, null, null, null};
        counters[2] = new Counter[]{null, O, null, null, null};
        counters[1] = new Counter[]{null, O, null, null, null};
        counters[0] = new Counter[]{null, X, X, X, null};
        counters = rotateBoard(counters);

        Board board = new Board(new Board(counters, new GameConfig(width, height, numInARow)), 1, O);
        GameState gameState = boardAnalyser.calculateGameState(board);

        assertEquals(1, connexMachina.getWinner(gameState));
        assertTrue(connexMachina.isTerminalNode(board));
    }

    @Test
    public void diagonal_winning_move_returns_winner() throws InvalidMoveException {
        int numInARow = 4;
        int width = 5;
        int height = 5;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        counters[4] = new Counter[]{null, null, null, null, null};
        counters[3] = new Counter[]{null, X, null, null, null};
        counters[2] = new Counter[]{null, O, X, null, null};
        counters[1] = new Counter[]{null, O, O, X, null};
        counters[0] = new Counter[]{null, O, O, X, null};
        counters = rotateBoard(counters);

        Board board = new Board(new Board(counters, new GameConfig(width, height, numInARow)), 4, X);
        GameState gameState = boardAnalyser.calculateGameState(board);

        assertEquals(-1, connexMachina.getWinner(gameState));
        assertTrue(connexMachina.isTerminalNode(board));
    }

    @Test
    public void horizontal_winning_move_returns_winner() throws InvalidMoveException {
        int numInARow = 4;
        int width = 5;
        int height = 5;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        counters[4] = new Counter[]{null, null, null, null, null};
        counters[3] = new Counter[]{null, null, null, null, null};
        counters[2] = new Counter[]{null, null, null, null, null};
        counters[1] = new Counter[]{null, null, null, null, null};
        counters[0] = new Counter[]{null, O, O, O, null};
        counters = rotateBoard(counters);

        Board board = new Board(new Board(counters, new GameConfig(width, height, numInARow)), 4, O);
        GameState gameState = boardAnalyser.calculateGameState(board);

        assertEquals(1, connexMachina.getWinner(gameState));
        assertTrue(connexMachina.isTerminalNode(board));
    }

    @Test
    public void find_best_move_finds_own_winning_move() throws InvalidMoveException {
        int numInARow = 4;
        int width = 10;
        int height = 8;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        Counter[] emptyRow = new Counter[]{null, null, null, null, null, null, null, null, null, null};

        counters[7] = emptyRow;
        counters[6] = emptyRow;
        counters[5] = emptyRow;
        counters[4] = emptyRow;
        counters[3] = emptyRow;
        counters[2] = emptyRow;
        counters[1] = emptyRow;
        counters[0] = emptyRow;
        counters = rotateBoard(counters);

        Board board = new Board(counters, new GameConfig(width, height, numInARow));
       assertEquals(5, connexMachina.findBestMove(board, 5));
//        assertTrue(connexMachina.isTerminalNode(board));
    }

    @Test
    public void legal_moves_starts_from_middle() throws InvalidMoveException {
        int numInARow = 4;
        int width = 10;
        int height = 8;
        BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(width, height, numInARow));
        Counter[][] counters = new Counter[height][width];
        Counter[] emptyRow = new Counter[]{null, null, null, null, null, null, null, null, null, null};

        counters[7] = emptyRow;
        counters[6] = emptyRow;
        counters[5] = emptyRow;
        counters[4] = emptyRow;
        counters[3] = emptyRow;
        counters[2] = emptyRow;
        counters[1] = emptyRow;
        counters[0] = emptyRow;
        counters = rotateBoard(counters);

        Board board = new Board(new Board(counters, new GameConfig(width, height, numInARow)), 4, O);
        int[] expected = new int[]{4, 5, 3, 6, 2, 7, 1, 8, 0, 9};
        assertArrayEquals(expected, legalMoves(board));
    }

    private Map<Counter, Integer> getCounts(int x, int o) {
        HashMap<Counter, Integer> counts = new HashMap<>();
        counts.put(X, x);
        counts.put(O, o);
        return counts;
    }

    private Counter[][] rotateBoard(Counter[][] board) {
        Counter[][] newBoard = new Counter[board[0].length][board.length];
        for (int i = 0; i < board[0].length; i++) {
            for (int j = board.length - 1; j >= 0; j--) {
                newBoard[i][j] = board[j][i];
            }
        }
        return newBoard;
    }
}
