package com.bugenzhao.nsfc

import com.bugenzhao.nsfc.BaseGenerator
import org.gephi.graph.api.GraphController
import org.gephi.io.exporter.api.ExportController
import org.openide.util.Lookup
import java.io.File
import java.io.IOException


fun main() {
    val year = readLine()?.toInt()!!

    val pc = BaseGenerator.graphGenerate(year, year)
    val workspace = pc.currentWorkspace
    val graphModel = Lookup.getDefault().lookup(GraphController::class.java).getGraphModel(workspace)

    val ec = Lookup.getDefault().lookup(ExportController::class.java).apply {
        try {
            exportFile(File("$year.gexf"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }

    println("GEXF exported")
    println("\nALL DONE")
}
