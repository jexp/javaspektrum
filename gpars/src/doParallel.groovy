import groovyx.gpars.GParsPool

def list = (1..10).toList()
GParsPool.withPool {

  def res = list.findAllParallel { it % 2 == 0}
       .collectParallel { it * it }
       .sortParallel { a,b -> a <=> b}
       .groupByParallel { it % 2 }
  println res
}
