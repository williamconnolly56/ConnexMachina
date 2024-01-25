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

    @Override
    public int makeMove(Board board) {
        try {
            return findBestMove(board, 5);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public int minimax(Board board, int depth, boolean maximizingPlayer) throws InvalidMoveException {
        // may need to ad 'OR if game is over' clause to the if statement
        boolean isTerminalNode = isTerminalNode(board);
        if(depth == 0 || isTerminalNode) {
            return evaluate(board);
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
                Board newBoard = new Board(board, move, counter.getOther());
                int eval = minimax(newBoard, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    public int findBestMove(Board board, int depth) throws InvalidMoveException {
        int bestMove = -1;
        int maxEval = Integer.MIN_VALUE;
        for (int move : legalMoves(board)) {
            Board newBoard = new Board(board, move, counter);
            int eval = minimax(newBoard, depth - 1, false);
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }
        return bestMove;
    }


    boolean isTerminalNode(Board board) {
        GameState gameState = boardAnalyser.calculateGameState(board);
        if(getWinner(gameState) != 0 || gameState.getIsFull()) {
            return true;
        } else {
            return false;
        }
    }

    int getWinner(GameState gameState) {
        Counter winner = gameState.getWinner();
        if(winner == null){
            return 0;
        } else if (winner.equals(counter)) {
            return 1;
        } else {
            return -1;
        }
    }

    int evaluate(Board board) {
        return getWinner(boardAnalyser.calculateGameState(board));
    }
}
