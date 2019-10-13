package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.Institution

data class ProcessedData(val awards: List<Award>,
                         val insts: List<Institution>,
                         val relationships: Map<Award, Institution>) {}

fun dataProcess(year: Int): ProcessedData {
    val awards = mutableListOf<Award>()
    val insts = mutableListOf<Institution>()
    val relationships = mutableMapOf<Award, Institution>()

    val rs = nsfConnect(year);
    println("Start to process the data...")

    rs.run {
        while (next()) {
            val awardID = getInt("ID")
            val awardTitle = getString("Title")
            val instID = getInt("InstitutionID")
            val instName = getString("Name")

            val award = Award(awardID, awardTitle)
            val inst = Institution(instID, instName)

            awards.add(award)
            insts.add(inst)
            relationships[award] = inst
        }
    }

    println("Data processed")
    return ProcessedData(awards, insts, relationships);
}