package com.bugenzhao.nsf

import com.bugenzhao.nsf.utils.computeRunningTime
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
import java.util.*


fun prettyGraphGenerate(startYear: Int, endYear: Int = startYear) {
    require(endYear >= startYear)
    var pc = graphGenerate(startYear, endYear);
    if (endYear > startYear) {
        pc = toDynamic(pc)
    }
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

    val repeatTimes = 1000
    computeRunningTime {
        repeat(repeatTimes) {
            layout.goAlgo()
            if (it % 100 == 0) print("$it..")
        }.run { layout.endAlgo() }
    }.run { println("\nCompleted layout after $repeatTimes iterations") }


//    val previewController = Lookup.getDefault().lookup(PreviewController::class.java)
//    val previewModel = previewController.model
//    previewModel.properties.applyPreset(DefaultCurved())
//    previewModel.properties.putValue(PreviewProperty.SHOW_NODE_LABELS, false)

    val pathname = "${startYear}_${endYear}_${System.currentTimeMillis()}.gexf"
    val ec = Lookup.getDefault().lookup(ExportController::class.java).apply {
        try {
            exportFile(File(pathname))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }

    println("Exported as '$pathname'")
    println("\nALL DONE")
}


fun main() {
    val scanner = Scanner(System.`in`)
    val startYear = scanner.nextInt()
    val endYear = scanner.nextInt()
    prettyGraphGenerate(startYear, endYear)
}