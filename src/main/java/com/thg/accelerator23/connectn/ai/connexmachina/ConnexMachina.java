package com.thg.accelerator23.connectn.ai.connexmachina;

import com.thehutgroup.accelerator.connectn.player.Board;
import com.thehutgroup.accelerator.connectn.player.Counter;
import com.thehutgroup.accelerator.connectn.player.Player;
import com.thehutgroup.accelerator.connectn.player.Position;

import java.util.Arrays;
import java.util.Random;


public class ConnexMachina extends Player {
  public ConnexMachina(Counter counter) {
    //TODO: fill in your name here
    super(counter, ConnexMachina.class.getName());
  }

  @Override
  public int makeMove(Board board) {
    int[] columnsNotFull = checkColumnsNotFull(board);
    //TODO: some crazy analysis
    //TODO: make sure said analysis uses less than 2G of heap and returns within 10 seconds on whichever machine is running it
    return randomlySelectColumn(columnsNotFull);
  }

  public int[] checkColumnsNotFull(Board board) {
    int[] columnsNotFull = new int[10];
    int index = 0;

    for (int column = 1; column <= 10; column++) {
      Position topOfColumn = new Position(column, 8);
      if (!board.hasCounterAtPosition(topOfColumn)) {
        columnsNotFull[index++] = column;
      }
    }

    // Trim the array to the actual size of elements
    return Arrays.copyOf(columnsNotFull, index);
  }

  public int randomlySelectColumn(int[] columns) {
    if (columns.length == 0) {
      throw new IllegalArgumentException("Array is empty");
    }

    Random random = new Random();
    int randomIndex = random.nextInt(columns.length);

    return columns[randomIndex];
  }
}
