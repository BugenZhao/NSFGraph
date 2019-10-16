package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.Institution
import com.bugenzhao.nsf.nodes.Instrument
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup


fun graphGenerate(year: Int): ProjectController {
    val (awards, institutions, instruments, institutionRel, instrumentRel) = dataProcess(year);
    println("Start to generate the graph... (19$year)")

    val pc = Lookup.getDefault().lookup(ProjectController::class.java).apply { newProject() }
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
    val undirectedGraph = graphModel.undirectedGraph

    val awardNodes = mutableMapOf<Award, Node>()
    val institutionNodes = mutableMapOf<Institution, Node>()
    val instrumentNodes = mutableMapOf<Instrument, Node>()


    graphModel.nodeTable.addColumn("type", String::class.java)

    awards.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.title
            setAttribute("type", "Award");
        }
        awardNodes[it] = node
        undirectedGraph.addNode(node)
    }
    institutions.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.name
            setAttribute("type", "Institution");
        }
        if (institutionNodes[it] == null) {
            institutionNodes[it] = node
            undirectedGraph.addNode(node)
        }
    }
    instruments.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.value
            setAttribute("type", "Instrument");
        }
        if (instrumentNodes[it] == null) {
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