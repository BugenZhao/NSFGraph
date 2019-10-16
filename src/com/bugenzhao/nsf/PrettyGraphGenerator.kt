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


fun prettyGraphGenerate(_year: Int) {
    val year = _year % 100
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
        maxSize = 80F
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

//    val filterController = Lookup.getDefault().lookup(FilterController::class.java)
//    val partitionFilter = NodePartitionFilter(graphModel.nodeTable.getColumn("type"), appearanceModel)
//
//    partitionFilter.unselectAll()
//    partitionFilter.addPart("Institution")
//    var query2 = filterController.createQuery(partitionFilter)
//    var view2 = filterController.filter(query2)
//    graphModel.visibleView = view2
//    degreeRanking.getTransformer<RankingNodeSizeTransformer>().apply {
//        minSize = 25F
//        maxSize = 60F
//    }
//    appearanceController.transform(degreeRanking)
//
//    partitionFilter.unselectAll()
//    partitionFilter.addPart("Instrument")
//    query2 = filterController.createQuery(partitionFilter)
//    view2 = filterController.filter(query2)
//    graphModel.visibleView = view2
//    graphModel.getGraph(view) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//    degreeRanking.getTransformer<RankingNodeSizeTransformer>().apply {
//        minSize = 80F
//        maxSize = 120F
//    }
//    appearanceController.transform(degreeRanking)
//
//    graphModel.visibleView = graphModel.createView()


    println("Applying layout...")
    val layout = ForceAtlas2(null).apply {
        setGraphModel(graphModel)
        resetPropertiesValues()
        scalingRatio = 200.0
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
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }

    println("Exported")
    println("\nALL DONE")
}


fun main() {
    val year = readLine()?.toInt()!! % 100
    prettyGraphGenerate(year)
}