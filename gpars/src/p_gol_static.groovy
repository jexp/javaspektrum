import groovy.transform.CompileStatic
import groovyx.gpars.ParallelEnhancer

ParallelEnhancer.enhanceClass(HashSet)

/**
* @author Michael Hunger
* @since 24.03.2010
*/
class GameOfLife {
 static int X=0,Y=1;
 static Set<List<Integer>> env = [[-1,-1],[0,-1],[1,-1],[-1,0],[1,0],[-1,1],[0,1],[1,1]]
 Set<List<Integer>> alive = []

 @CompileStatic
 Set<List<Integer>> neighbours(List cell) {
   this.@env.collect {List value -> [((Integer)cell[X]).intValue() +
((Integer)value[X]),((Integer)cell[Y]).intValue() + ((Integer)value[Y])]
}
 }

 @CompileStatic
 Set<List<Integer>> aliveNeighbours(List<Integer> cell) {
   neighbours(cell).findAll {alive.contains(it) }
 }

 @CompileStatic
 Set<List<Integer>> deadNeighbours(List<Integer> cell) {
   neighbours(cell).findAll { !alive.contains(it) }
 }

 @CompileStatic
 Set<List<Integer>> haveNeighbourCount(Set<List<Integer>> coll,
List<Integer> counts) {
   coll.findAll {List<Integer> value ->
counts.contains(aliveNeighbours(value).size())}
 }

 GameOfLife next() {
   def stayingAlive = haveNeighbourCount(this.@alive, [2,3]).toSet()
   def wakingFromDead = this.@alive.parallel.combineImpl({1}, {it},
{[]}, this.&cl).get(1)

   new GameOfLife(alive: (stayingAlive + wakingFromDead).makeConcurrent())
 }

@CompileStatic
private def cl(Collection<List<Integer>> res, List<Integer> cell) {
   res + haveNeighbourCount(deadNeighbours(cell),[3])
}


 String toString() {
    (alive.min{it[Y]}[Y]..alive.max{it[Y]}[Y])
       .collect { y ->
       (alive.min{it[X]}[X]..alive.max{it[X]}[X])
       .collect { x ->
           alive.contains([x,y]) ? "X" : "."}
           .join("")+"\n"
           }.join("")
 }
 static GameOfLife fromString(def str) {
    int x=0,y=0;
    def alive=[]
    str.each { if (it == 'X') alive+=[[x,y]];
       if (it=='\n') { x=0;y--;}
       else x++;
    }
    new GameOfLife(alive: alive.toSet().makeConcurrent())
 }
 static GameOfLife random(count, size) {
    def rnd = new Random(0L)
    def alive = ((0..count).collect {
[rnd.nextInt(size),rnd.nextInt(size)]}).toSet()
    new GameOfLife(alive: alive.toSet().makeConcurrent())
 }

}
def gol=GameOfLife.fromString(
"""
  X
   X
   X
 XXX

""")


def benchmark = { closure ->
 start = System.currentTimeMillis()
 closure.call()
 now = System.currentTimeMillis()
 now - start
}

println benchmark { 1000.times{ gol = gol.next() }}

gol = GameOfLife.random(10000,500)

println benchmark { 100.times{ gol = gol.next() }}
