package gol;

import extra166y.ParallelArray;
import jsr166y.ForkJoinPool;

import java.util.Random;

import static extra166y.Ops.*;
import static gol.PAGameOfLife.Cell.cell;

public class PAGameOfLife {
    static Random RANDOM = new Random(0L);
    ParallelArray<Cell> alive;

    public PAGameOfLife(Cell... cells) {
        this(ParallelArray.createFromCopy(cells.length, cells, new ForkJoinPool(8)));
    }

    public PAGameOfLife(int count, final int width) {
        this(ParallelArray.<Cell>create(count, Cell.class, new ForkJoinPool(8)).replaceWithGeneratedValue(new Generator<Cell>() {
            public Cell op() {
                return cell(RANDOM.nextInt(width), RANDOM.nextInt(width));
            }
        }));
    }

    public PAGameOfLife(ParallelArray<Cell> alive) {
        this.alive = alive;
    }

    public PAGameOfLife nextGeneration() {
        final ParallelArray<Cell> nextGen = ParallelArray.<Cell>create(alive.size() * 9, Cell.class, alive.getExecutor())
                .replaceWithMappedIndex(new IntToObject<Cell>() {
                    public Cell op(int idx) {
                        Cell liveCell = alive.get(idx / 9);
                        return liveCell.offset(idx % 9);
                    }
                });
        return new PAGameOfLife(nextGen.allUniqueElements().withFilter(new WillLive()).all());
    }

    /*
            nextGen.addAll(alive.withMapping())
        alive.apply(new Procedure<Cell>() {
          public void op(Cell cell) {
              // parallel arrays are not thread safe to write to :( they have shared mutable state
              nextGen.addAll(cell.cellAndNeighbours());
          }
      });
     */
    int liveNeighbourCount(final Cell c) {
        return alive.withFilter(new Predicate<Cell>() {
            public boolean op(Cell cell) {
                return c.isNeighbour(cell);
            }

        }).size();
    }

    // helpers:
    class WillLive implements Predicate<Cell> {
        public boolean op(Cell c) {
            final int lifeNeighbours = liveNeighbourCount(c);
            return lifeNeighbours == 3 || alive.indexOf(c) != -1 && lifeNeighbours == 2;
        }
    }

    public static class Cell {
        private final int x, y;

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
            return new Cell(x, y);
        }

        @Override
        public String toString() {
            return "(" + x + ":" + y + ")";
        }

        /**
         * @param i 0..8 offset in the cellAndNeighbours() array
         * @return
         */
        public Cell offset(int i) {
            return cell(x + (i % 3) - 1, y + (i / 3) - 1);
        }

        private boolean isNeighbour(Cell cell) {
            int dx = x - cell.x;
            int dy = y - cell.y;
            if (dx==0 && dy == 0) return false;
            return dx > -2 && dx < 2 && dy > -2 && dy < 2;
        }
    }

    @Override
    public String toString() {
        return alive.asList().toString();
    }

    public static void main(String[] args) {
        PAGameOfLife game = new PAGameOfLife(10000, 500);
        //PAGameOfLife game = new PAGameOfLife(cell(0, -1), cell(0, 0), cell(0, 1));
        System.out.println(game.alive.size());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            game = game.nextGeneration();
        }
        System.out.println((System.currentTimeMillis() - start) + " ms.");
        //PAGameOfLife game = new PAGameOfLife(cell(0, -1), cell(0, 0), cell(0, 1));
        //System.out.println(game.nextGeneration());
    }
}
// for flatMap ? public <V> ParallelArray<T> addAll(ParallelArrayWithMapping<V,T> other)