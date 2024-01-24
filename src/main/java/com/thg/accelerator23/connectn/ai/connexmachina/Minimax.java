package com.thg.accelerator23.connectn.ai.connexmachina;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Position;
import java.util.Arrays;

public class Minimax {

    private final Counter counter;

    public Minimax(Counter counter) {
        this.counter = counter;
    }

    public static int[] legalMoves(Board board) {
        int[] columnsNotFull = new int[10];
        int index = 0;

        for (int column = 0; column <= 9; column++) {
            Position topOfColumn = new Position(column, 7);
            if (!board.hasCounterAtPosition(topOfColumn)) {
                columnsNotFull[index] = column;
                index++;
            }
        }
        return Arrays.copyOf(columnsNotFull, index);
    }

    public int minimax(Board board, int depth, boolean isMaximiser) throws InvalidMoveException {
        if (depth == 0) {
            return evaluate(board);
        }
        if (isMaximiser) {
            int maxEval = Integer.MIN_VALUE;
            for (int move : legalMoves(board)) {
                Board newBoard = new Board(board, move, counter);
                int eval = minimax(newBoard, depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int move : legalMoves(board)) {
                Board newBoard = new Board(board, move, counter);
                int eval = minimax(newBoard, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    public int evaluate(Board board) {
        return -1;
    }
}