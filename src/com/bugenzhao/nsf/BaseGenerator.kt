package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.DataNode
import com.bugenzhao.nsf.nodes.Institution
import org.gephi.graph.api.Edge
import org.gephi.graph.api.Graph
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import org.gephi.project.api.Workspace;
import org.gephi.io.importer.api.Container;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.io.processor.plugin.AppendProcessor;
import org.gephi.io.generator.plugin.DynamicGraph;
import kotlin.contracts.contract
import java.io.IOException
import org.gephi.io.exporter.api.ExportController
import java.io.File


fun graphGenerate(year: Int): ProjectController {
    val (awards, insts, relationships) = dataProcess(year);
    println("Start to generate the graph... (19$year)")

    val pc = Lookup.getDefault().lookup(ProjectController::class.java).apply { newProject() }
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
    val undirectedGraph = graphModel.undirectedGraph

    val awardNodes = mutableMapOf<Award, Node>()
    val instNodes = mutableMapOf<Institution, Node>()
    val rsEdges = mutableSetOf<Edge>()


    graphModel.nodeTable.addColumn("type", String::class.java)

    awards.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.title
            setAttribute("type", "Award");
        }
        awardNodes[it] = node
        undirectedGraph.addNode(node)
    }
    insts.forEach {
        val node = graphModel.factory().newNode(it.id.toString()).apply {
            label = it.name
            setAttribute("type", "Institution");
        }
        if (instNodes[it] == null) {
            instNodes[it] = node
            undirectedGraph.addNode(node)
        }
    }
    relationships.forEach { (a: Award, i: Institution) ->
        val edge = graphModel.factory().newEdge(awardNodes[a], instNodes[i], false)
        undirectedGraph.addEdge(edge)
    }

    println("Nodes: " + undirectedGraph.nodeCount + " Edges: " + undirectedGraph.edgeCount)
    println("Graph generated")


    return pc
}