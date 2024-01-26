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
        GameState gameState = boardAnalyser.calculateGameState(board);
        int winner = getWinner(gameState);
        if (winner != 0) {
            return winner * 1000;
        } else if (gameState.getIsFull()) {
            return 0;
        } else if (depth == 0) {
            return evaluate(gameState);
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

    private int evaluate(GameState gameState) {
        return gameState.getMaxInARowByCounter().get(counter) - gameState.getMaxInARowByCounter().get(counter.getOther());
    }
}