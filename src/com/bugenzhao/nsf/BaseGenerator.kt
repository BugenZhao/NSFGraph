package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.Institution
import com.bugenzhao.nsf.nodes.Instrument
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup


fun graphGenerate(startYear: Int, endYear: Int): ProjectController {
    val (awards, institutions, instruments, institutionRel, instrumentRel) = dataProcess(startYear,endYear);
    println("Start to generate the graph...")

    val pc = Lookup.getDefault().lookup(ProjectController::class.java).apply { newProject() }
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
    val undirectedGraph = graphModel.undirectedGraph

    val awardNodes = mutableMapOf<Award, Node>()
    val institutionNodes = mutableMapOf<Institution, Node>()
    val instrumentNodes = mutableMapOf<Instrument, Node>()


    graphModel.nodeTable.run {
        addColumn("type", String::class.java)
        addColumn("effectiveDate", String::class.java)
        addColumn("expirationDate", String::class.java)
    }

    awards.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.title
            setAttribute("type", "Award")
            setAttribute("effectiveDate", it.effectiveDate)
            setAttribute("expirationDate", it.expirationDate)
//            val start = it.effectiveDate.split('/')[2].toDouble()
//            val end = it.expirationDate.split('/')[2].toDouble()
//            addInterval(Interval(start, end))
        }
        awardNodes[it] = node
        undirectedGraph.addNode(node)
    }
    institutions.forEach {
        if (institutionNodes[it] == null) {
            val node = graphModel.factory().newNode(it.id.toString()).apply {
                label = it.name
                setAttribute("type", "Institution")
//                addInterval(Interval.INFINITY_INTERVAL)
            }
            institutionNodes[it] = node
            undirectedGraph.addNode(node)
        }
    }
    instruments.forEach {
        if (instrumentNodes[it] == null) {
            val node = graphModel.factory().newNode(it.id.toString()).apply {
                label = it.value
                setAttribute("type", "Instrument")
//                addInterval(Interval.INFINITY_INTERVAL)
            }
            instrumentNodes[it] = node
            undirectedGraph.addNode(node)
        }
    }

    institutionRel.forEach { (a: Award, i: Institution) ->
        val edge = graphModel.factory().newEdge(
                undirectedGraph.getNode(a.id.toString()),
                undirectedGraph.getNode(i.id.toString()),
                false
        )
        undirectedGraph.addEdge(edge)
    }
    instrumentRel.forEach { (a: Award, i: Instrument) ->
        val edge = graphModel.factory().newEdge(
                undirectedGraph.getNode(a.id.toString()),
                undirectedGraph.getNode(i.id.toString()),
                false
        )
        undirectedGraph.addEdge(edge)
    }

    println("Nodes: " + undirectedGraph.nodeCount + " Edges: " + undirectedGraph.edgeCount)
    println("Graph generated")


    return pc
}