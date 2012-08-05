import extra166y.ParallelArray;

import java.util.List;

import static extra166y.Ops.*;

public class PAGameOfLife {
  ParallelArray<Cell> alive;

  public ParallelArray<Cell> nextGeneration() {
    return alive.withMapping(cellsToCheck).withFilter(willLive).all().max();
  }

  int liveNeighbourCount(Cell c) {
     int count=0;
     for (int x=-1;x<=1;x++)
     	for (int y=-1;y<=1;y++) {
		    if (x==0 && y==0) continue;
			if (alive.indexOf(Cell.cell(x, y)) != -1) count++;
			if (count>3) return count;
        }
      return count;
  }
  // helpers:
  class WillLive implements Predicate<Cell> {
    public boolean op(Cell c) {
        final int lifeNeighbours = liveNeighbourCount(c);
        return alive.indexOf(c)!=-1 && lifeNeighbours ==2 || lifeNeighbours == 3;
    }
  }
  final WillLive willLive = new WillLive();
  final class Neighbours implements ObjectToObject<Cell,List<Cell>> {
    public List<Cell> op(Cell c) { 
		return c.neighbours(); 
	}
  }
  static final GpaField gpaField = new GpaField();

  static class Cell {
      private final int x,y;

      Cell(int x, int y) {
          this.x = x;
          this.y = y;
      }

      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          Cell cell = (Cell) o;
          return x == cell.x && y == cell.y;
      }

      @Override
      public int hashCode() {
          return 31 * x + y;
      }

      public static Cell cell(int x, int y) {
          return new Cell(x,y);
      }
  }
}
// for flatMap ? public <V> ParallelArray<T> addAll(ParallelArrayWithMapping<V,T> other)