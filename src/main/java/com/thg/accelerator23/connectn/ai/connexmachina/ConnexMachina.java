package com.thg.accelerator23.connectn.ai.connexmachina;

import com.thehutgroup.accelerator.connectn.player.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class ConnexMachina extends Player {

  private final Counter counter;

  public ConnexMachina(Counter counter) {
    super(counter, ConnexMachina.class.getName());
    this.counter = counter;
  }

  @Override
  public int makeMove(Board board) {
    try {
      return findBestMove(board, 3);
    } catch (InvalidMoveException e) {
      throw new RuntimeException(e);
    }
    //TODO: some crazy analysis
    //TODO: make sure said analysis uses less than 2G of heap and returns within 10 seconds on whichever machine is running it
  }


  public int minimax(Board board, int depth, boolean maximizingPlayer) throws InvalidMoveException {
    // may need to ad 'OR if game is over' clause to the if statement
    if (depth == 0) {
      return evaluate(board);
    }

    if (maximizingPlayer) {
      int maxEval = Integer.MIN_VALUE;
      for (int move : legalMoves(board)) {
        Board newBoard =  new Board (board, move, counter);
        int eval = minimax(newBoard, depth - 1, false);
        maxEval = Math.max(maxEval, eval);
      }
      return maxEval;
    } else {
      int minEval = Integer.MAX_VALUE;
      for (int move : legalMoves(board)) {
        Board newBoard =  new Board (board, move, counter);
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
      Board newBoard =  new Board (board, move, counter);
      int eval = minimax(newBoard, depth - 1, false);
      if (eval > maxEval) {
        maxEval = eval;
        bestMove = move;
      }
    }
    return bestMove;
  }

  public int getYCoord(Board board, int x) {
    for(int y=0; y<8; y++) {
      if(!board.hasCounterAtPosition(new Position(x, y))){
        return y;
      }
    }
    return -1;
  }
  private int evaluate(Board board) {

    int boardValue=0;

    for (int column = 0; column <= 9; column++) {
      int maxY = getYCoord(board, column);
      int lookingDepth;
      if (maxY<3){
        lookingDepth = maxY+1;
      } else {
        lookingDepth = 4;
      }
      int[] lookingDown = new int[lookingDepth];

      for(int i = 0; i<lookingDepth; i++) {
        int height = maxY-i;
        Position position = new Position(column, height);
        lookingDown[i] = getBinaryCounterAtPosition(board, position);
      }
      boardValue += findVerticalValue(lookingDown, counterToBinary(counter));

      if(column < 7) {

        for(int row=maxY; row>=0; row--) {

          int[] lookingRight = new int[4];
          for (int i = 0 ; i<4; i++) {
            Position position = new Position(column+i, row);
            lookingRight[i] = getBinaryCounterAtPosition(board, position);
          }
          boardValue += findHorizontalValue(lookingRight, counterToBinary(counter));
        }

      }
    }

    return boardValue;
  }

  public int findVerticalValue(int[] inputArray, int opponentCounter){
    int count=0;
    while (inputArray[count] != opponentCounter) {
      count++;
    }
    return count;
  }

  public int findHorizontalValue(int[] inputArray, int currentPlayerCounter){
    int count=0;
    for (int i: inputArray){
      int value = inputArray[i];
      if (value == (currentPlayerCounter *-1)) {
        return 0;
      } else if (value == currentPlayerCounter) {
        count++;
      }
    }
    return count;
  }

  public int counterToBinary(Counter counter) {
    if(counter == null){
      return 0;
    }else if (counter.getStringRepresentation().equals("X") ){
      return 1;
    }
    else{
      return -1;
    }
  }

  public int getBinaryCounterAtPosition(Board board, Position position) {
    Counter counter = board.getCounterAtPosition(position);
    return counterToBinary(counter);
  }

  public static int[] legalMoves(Board board) {
    int[] columnsNotFull = new int[10];
    int index = 0;

    for (int column = 0; column <= 9; column++) {
      Position topOfColumn = new Position(column, 7);
      if (!board.hasCounterAtPosition(topOfColumn)) {
        columnsNotFull[index++] = column;
      }
    }

    // Trim the array to the actual size of elements
    return Arrays.copyOf(columnsNotFull, index);
  }
}
