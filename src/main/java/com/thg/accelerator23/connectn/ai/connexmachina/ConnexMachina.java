package com.thg.accelerator23.connectn.ai.connexmachina;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.GameConfig;
import com.thehutgroup.accelerator.connectn.player.InvalidMoveException;
import com.thehutgroup.accelerator.connectn.player.Player;
import com.thehutgroup.accelerator.connectn.player.Position;
import java.util.Arrays;


public class ConnexMachina extends Player {

    private final Counter counter;
    private final BoardAnalyser boardAnalyser = new BoardAnalyser(new GameConfig(10, 8, 4));

    public ConnexMachina(Counter counter) {
        super(counter, ConnexMachina.class.getName());
        this.counter = counter;
        System.out.println("VERSION 5.2");
    }

    public static int[] legalMoves(Board board) {
        int[] columnsNotFull = new int[10];
        int count = 0;
        for (int i = 0; i < 5; i++) {
            Position topOfColumnLeft = new Position(4 - i, 7);
            if (!board.hasCounterAtPosition(topOfColumnLeft)) {
                columnsNotFull[count] = 4 - i;
                count++;
            }
            Position topOfColumnRight = new Position(5 + i, 7);
            if (!board.hasCounterAtPosition(topOfColumnRight)) {
                columnsNotFull[count] = 5 + i;
                count++;
            }
        }
        return Arrays.copyOf(columnsNotFull, count);
    }

    @Override
    public int makeMove(Board board) {
        try {
            return findBestMove(board, 7);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public int minimax(Board board, int depth, boolean maximizingPlayer, int alpha, int beta) throws InvalidMoveException {
        // may need to ad 'OR if game is over' clause to the if statement
        boolean isTerminalNode = isTerminalNode(board);
        if (depth == 0 || isTerminalNode) {
            return evaluate(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int move : legalMoves(board)) {
                Board newBoard = new Board(board, move, counter);
                int eval = minimax(newBoard, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int move : legalMoves(board)) {
                Board newBoard = new Board(board, move, counter.getOther());
                int eval = minimax(newBoard, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    public int findBestMove(Board board, int depth) throws InvalidMoveException {
        int bestMove = -1;
        int maxEval = Integer.MIN_VALUE;
        for (int move : legalMoves(board)) {
            Board newBoard = new Board(board, move, counter);
            int eval = minimax(newBoard, depth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }


    boolean isTerminalNode(Board board) {
        GameState gameState = boardAnalyser.calculateGameState(board);
        if (getWinner(gameState) != 0 || gameState.getIsFull()) {
            return true;
        } else {
            return false;
        }
    }

    int getWinner(GameState gameState) {
        Counter winner = gameState.getWinner();
        if (winner == null) {
            return 0;
        } else if (winner.equals(counter)) {
            return 1;
        } else {
            return -1;
        }
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
            int maxValDown = findVerticalValue(lookingDown, counterToBinary(counter));
            if (maxValDown == 4) {
                boardValue += 1000;
            } else {
                boardValue += maxValDown;
            }
            int minValDown = findVerticalValue(lookingDown, -counterToBinary(counter));
            if (minValDown == 4) {
                boardValue -= 1000;
            } else {
                boardValue -= minValDown;
            }
            if (column < 7) {

                for (int row = maxY; row >= 0; row--) {

                    int[] lookingRight = new int[4];
                    for (int i = 0; i < 4; i++) {
                        Position position = new Position(column + i, row);
                        lookingRight[i] = getBinaryCounterAtPosition(board, position);
                    }
                    int maxValRight = findHorizontalValue(lookingRight, counterToBinary(counter));
                    if (maxValRight == 4) {
                        boardValue += 1000;
                    } else {
                        boardValue += maxValRight;
                    }
                    int minValRight = findHorizontalValue(lookingRight, -counterToBinary(counter));
                    if (minValRight == 4) {
                        boardValue -= 1000;
                    } else {
                        boardValue -= minValRight;
                    }
                }
            }
        }
        return boardValue;
    }

    public int findVerticalValue(int[] inputArray, int opponentCounter) {
        int count = 0;
        while (count < inputArray.length && inputArray[count] != opponentCounter) {
            count++;
        }
        return count;
    }

    public int findHorizontalValue(int[] inputArray, int currentPlayerCounter) {
        int count = 0;
        for (int i = 0; i < inputArray.length; i++) {
            int value = inputArray[i];
            if (value == (currentPlayerCounter * -1)) {
                return 0;
            } else if (value == currentPlayerCounter) {
                count++;
            }
        }
        return count;
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
