package com.thg.accelerator23.connectn.ai.connexmachina;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Player;
import com.thehutgroup.accelerator.connectn.player.Position;
import java.util.Arrays;


public class ConnexMachina extends Player {

    private final Counter counter;

    public ConnexMachina(Counter counter) {
        super(counter, ConnexMachina.class.getName());
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

        // Trim the array to the actual size of elements
        return Arrays.copyOf(columnsNotFull, index);
    }

    @Override
    public int makeMove(Board board) {
        try {
            return findBestMove(board, 3);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public int minimax(Board board, int depth, boolean maximizingPlayer) throws InvalidMoveException {
        // may need to ad 'OR if game is over' clause to the if statement
        if (depth == 0) {
            return evaluate(board, maximizingPlayer);
        }

        if (maximizingPlayer) {
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

    public int findBestMove(Board board, int depth) throws InvalidMoveException {
        int bestMove = -1;
        int maxEval = Integer.MIN_VALUE;
//        boolean isMaximisingPlayer;
//        isMaximisingPlayer = true; // I CHANGED THIS BUT NOT SURE IF CORRECT
        for (int move : legalMoves(board)) {
            Board newBoard = new Board(board, move, counter);
            int eval = minimax(newBoard, depth - 1, false);
            if (eval > maxEval) {
                System.out.println("found a bestMove");
                maxEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }

    public int getYCoord(Board board, int x) {
        for (int y = 0; y < 8; y++) {
            if (!board.hasCounterAtPosition(new Position(x, y))) {
                return y;
            }
        }
        return -1;
    }

    private int evaluate(Board board) {
        int boardValue = 0;

        for (int column = 0; column <= 9; column++) {
            int maxY = getYCoord(board, column);
            int lookingDepth;
            if (maxY < 3) {
                lookingDepth = maxY + 1;
            } else {
                lookingDepth = 4;
            }
            int[] lookingDown = new int[lookingDepth];

            for (int i = 0; i < lookingDepth; i++) {

                int height = maxY - i;
                Position position = new Position(column, height);
                lookingDown[i] = getBinaryCounterAtPosition(board, position);
            }
            boardValue += findVerticalValue(lookingDown, counterToBinary(counter), true);
            boardValue += findVerticalValue(lookingDown, -counterToBinary(counter), false);

            if (column < 7) {

                for (int row = maxY; row >= 0; row--) {

                    int[] lookingRight = new int[4];
                    for (int i = 0; i < 4; i++) {
                        Position position = new Position(column + i, row);
                        lookingRight[i] = getBinaryCounterAtPosition(board, position);
                    }
                    boardValue += findHorizontalValue(lookingRight, counterToBinary(counter), true);
                    boardValue += findHorizontalValue(lookingRight, -counterToBinary(counter), false);
                }
            }
        }
        return boardValue;
    }

    public int findVerticalValue(int[] inputArray, int opponentCounter, boolean maximisingPlayer) {
        int count = 0;
        while (count < inputArray.length && inputArray[count] != opponentCounter) {
            count++;
        }
        if (maximisingPlayer) {
            return count;
        } else {
            return -count;
        }
    }

    public int findHorizontalValue(int[] inputArray, int currentPlayerCounter, boolean maximisingPlayer) {
        int count = 0;
        for (int i = 0; i < inputArray.length; i++) {
            int value = inputArray[i];
            if (value == (currentPlayerCounter * -1)) {
                return 0;
            } else if (value == currentPlayerCounter) {
                count++;
            }
        }
        if (maximisingPlayer) {
            return count;
        } else {
            return -count;
        }
    }

    public int counterToBinary(Counter counter) {
        if (counter == null) {
            return 0;
        } else if (counter.getStringRepresentation().equals("X")) {
            return 1;
        } else {
            return -1;
        }
    }

    public int getBinaryCounterAtPosition(Board board, Position position) {
        Counter counter = board.getCounterAtPosition(position);
        return counterToBinary(counter);
    }
}
