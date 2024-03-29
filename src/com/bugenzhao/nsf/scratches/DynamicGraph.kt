package com.bugenzhao.nsf.scratches

import org.gephi.io.exporter.api.ExportController
import org.gephi.io.importer.api.Container
import org.gephi.io.importer.api.ImportController
import org.gephi.io.processor.plugin.MergeProcessor
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup
import java.io.File
import java.io.IOException


fun main() {
    //Init a project - and therefore a workspace
    val pc = Lookup.getDefault().lookup(ProjectController::class.java)
    pc.newProject()
    val workspace = pc.currentWorkspace

    //Import first file
    val importController = Lookup.getDefault().lookup(ImportController::class.java)
    val size = 3
    val containers = arrayOfNulls<Container>(size)
    try {
        for (i in 0 until size) {
            val year = 1975 + i
            println("Generating $year...")
//            prettyGraphGenerate(year)
            val file = File("$year.gexf")
            containers[i] = importController.importFile(file)
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        return
    }

//    val dynamicProcessor = MultiProcessor()
//    dynamicProcessor.setDateMode(false)    //Set 'true' if you set real dates (ex: yyyy-mm-dd), it's double otherwise
//    dynamicProcessor.setLabelMatching(true)   //Set 'true' if node matching is done on labels instead of ids

    //Process the container using the MergeProcessor
    importController.process(containers, MergeProcessor(), workspace)

    val ec = Lookup.getDefault().lookup(ExportController::class.java).apply {
        try {
            exportFile(File("output.gexf"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
    }

    println("Exported")
    println("\nALL DONE")
}