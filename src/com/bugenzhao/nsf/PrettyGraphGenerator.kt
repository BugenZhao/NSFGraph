package com.bugenzhao.nsf

import org.gephi.appearance.api.AppearanceController
import org.gephi.appearance.api.AppearanceModel
import org.gephi.appearance.api.PartitionFunction
import org.gephi.appearance.api.RankingFunction
import org.gephi.appearance.plugin.PartitionElementColorTransformer
import org.gephi.appearance.plugin.RankingNodeSizeTransformer
import org.gephi.appearance.plugin.palette.PaletteManager
import org.gephi.graph.api.GraphController
import org.gephi.io.exporter.api.ExportController
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2
import org.openide.util.Lookup
import java.io.File
import java.io.IOException


fun main() {
    val year = readLine()?.toInt()!! % 100

    val pc = graphGenerate(year);
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)
    val undirectedGraph = graphModel.undirectedGraph


    println("Applying appearance...")
    val appearanceController = Lookup.getDefault().lookup(AppearanceController::class.java)
    val appearanceModel = appearanceController.model

    val degreeRanking = appearanceModel.getNodeFunction(undirectedGraph, AppearanceModel.GraphFunction.NODE_DEGREE, RankingNodeSizeTransformer::class.java) as RankingFunction
    degreeRanking.getTransformer<RankingNodeSizeTransformer>().apply {
        minSize = 20F
        maxSize = 100F
    }
    val typePartition = appearanceModel.getNodeFunction(undirectedGraph, graphModel.nodeTable.getColumn("type"), PartitionElementColorTransformer::class.java) as PartitionFunction
    typePartition.partition.apply {
        val palette = PaletteManager.getInstance().generatePalette(size())
        setColors(palette.colors)
    }

    appearanceController.apply {
        transform(degreeRanking)
        transform(typePartition)
    }


    println("Applying layout...")
    val layout = ForceAtlas2(null).apply {
        setGraphModel(graphModel)
        resetPropertiesValues()
        scalingRatio = 80.0
        isStrongGravityMode = true
        gravity = 1.0
        isLinLogMode = false
        jitterTolerance = 20.0
        isAdjustSizes = false
        threadsCount = 10
        initAlgo()
    }
    repeat(1000) {
        layout.goAlgo()
    }.run { layout.endAlgo() }


//    val previewController = Lookup.getDefault().lookup(PreviewController::class.java)
//    val previewModel = previewController.model
//    previewModel.properties.applyPreset(DefaultCurved())
//    previewModel.properties.putValue(PreviewProperty.SHOW_NODE_LABELS, false)

    val ec = Lookup.getDefault().lookup(ExportController::class.java).apply {
        try {
            exportFile(File("19$year.gexf"))
//            exportFile(File("19$year.svg"))
//            exportFile(File("19$year.png"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }

    println("Exported")
    println("\nALL DONE")
}
