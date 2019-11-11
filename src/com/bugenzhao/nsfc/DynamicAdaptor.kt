package com.bugenzhao.nsfc

import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Interval
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup


fun toDynamic(pc: ProjectController): ProjectController {
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
    val undirectedGraph = graphModel.undirectedGraph

    var errorCount = 0

    undirectedGraph.nodes.toArray().forEach {
        when (it.getAttribute("type") as String) {
            "Award" -> {
                val start = (it.getAttribute("effectiveDate") as String).toDouble()
                val end = (it.getAttribute("expirationDate") as String).toDouble()
                try {
                    require(start != 1900.0)
                    it.addInterval(Interval(start, end))
                } catch (e: IllegalArgumentException) {
                    ++errorCount
                    undirectedGraph.removeNode(it)
                }

            }
            else -> {
                it.addInterval(Interval.INFINITY_INTERVAL)
            }
        }
    }

//    println("Time interval applied, $errorCount error(s) occurred")
    println("Time interval applied, $errorCount node(s) have been removed")
    return pc
}