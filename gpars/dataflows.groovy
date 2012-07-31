import groovyx.gpars.dataflow.*
import static groovyx.gpars.dataflow.Dataflow.task

final def flows = new Dataflows()

task {
    flows.z = flows.x + flows.y
}

task {
    flows.x = 10
}

task {
    flows.y = 5
}

println "Result: ${flows.z}"
