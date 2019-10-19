package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.DataNode
import com.bugenzhao.nsf.nodes.Institution
import com.bugenzhao.nsf.nodes.Instrument
import org.gephi.graph.api.GraphController
import org.gephi.graph.api.Node
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup

object BaseGenerator {
    fun graphGenerate(startYear: Int, endYear: Int): ProjectController {
        val (awards, institutions, instruments, institutionRel, instrumentRel) = DataProcessor.dataProcess(startYear, endYear)
        println("Start to generate the graph...")

        val pc = Lookup.getDefault().lookup(ProjectController::class.java).apply { newProject() }
        val workspace = pc.currentWorkspace
        val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
        val undirectedGraph = graphModel.undirectedGraph

        val awardNodes = mutableMapOf<Award, Node>()
        val otherNodes = mutableMapOf<DataNode, Node>()


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
            }
            awardNodes[it] = node
            undirectedGraph.addNode(node)
        }

        listOf(institutions, instruments).forEach { list ->
            list.forEach {
                if (otherNodes[it] == null) {
                    val node = graphModel.factory().newNode(it.id.toString()).apply {
                        label = when (it) {
                            is Institution -> it.name
                            is Instrument -> it.value
                            else -> throw RuntimeException()
                        }
                        setAttribute("type", it.type)
                    }
                    otherNodes[it] = node
                    undirectedGraph.addNode(node)
                }
            }
        }

        listOf(institutionRel, instrumentRel).forEach {
            it.forEach { (a: Award, i: DataNode) ->
                val edge = graphModel.factory().newEdge(
                        undirectedGraph.getNode(a.id.toString()),
                        undirectedGraph.getNode(i.id.toString()),
                        false
                )
                undirectedGraph.addEdge(edge)
            }
        }

        println("Nodes: " + undirectedGraph.nodeCount + " Edges: " + undirectedGraph.edgeCount)
        println("Graph generated")

        return pc
    }
}