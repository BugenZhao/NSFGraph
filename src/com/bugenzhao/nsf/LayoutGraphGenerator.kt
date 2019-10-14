package com.bugenzhao.nsf

import org.gephi.appearance.api.*
import org.gephi.graph.api.GraphController
import org.gephi.io.exporter.api.ExportController
import org.openide.util.Lookup
import java.io.*
import org.gephi.statistics.plugin.GraphDistance
import org.gephi.appearance.plugin.PartitionElementColorTransformer
import org.gephi.appearance.plugin.RankingElementColorTransformer
import org.gephi.appearance.plugin.RankingNodeSizeTransformer
import java.awt.Color
import org.gephi.appearance.plugin.palette.PaletteManager
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder
import java.lang.System.getProperties
import org.gephi.preview.types.DependantOriginalColor
import java.awt.event.ComponentEvent
import java.awt.event.ComponentAdapter
import com.sun.java.accessibility.util.AWTEventMonitor.addComponentListener
import org.apache.batik.ext.awt.image.rendered.TileCache.setSize
import org.gephi.io.exporter.preview.PNGExporter
import org.gephi.preview.api.*
import java.awt.BorderLayout
import javax.swing.JFrame
import org.gephi.preview.api.RenderTarget.G2D_TARGET
import org.gephi.preview.presets.BlackBackground
import org.gephi.preview.presets.DefaultCurved
import org.gephi.preview.spi.PreviewUI
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import javax.swing.Spring.height


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
