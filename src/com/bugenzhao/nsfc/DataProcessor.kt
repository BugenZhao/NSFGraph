package com.bugenzhao.nsfc

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.Institution
import com.bugenzhao.nsf.nodes.Instrument
import com.bugenzhao.nsf.utils.computeRunningTime
import java.util.regex.Pattern

data class ProcessedData(val awards: List<Award>,
                         val institutions: List<Institution>,
                         val instruments: List<Instrument>,
                         val institutionRel: Map<Award, Institution>,
                         val instrumentRel: Map<Award, Instrument>)

fun String.keepDigital(): String {
    val newString = StringBuffer()
    val matcher = Pattern.compile("\\d").matcher(this)
    while (matcher.find()) {
        newString.append(matcher.group())
    }
    return newString.toString()
}


object DataProcessor {
    fun dataProcess(startYear: Int, endYear: Int): ProcessedData {
        val awards = mutableListOf<Award>()
        val institutionNames = mutableSetOf<String>()
        val instrumentNames = mutableSetOf<String>()
        val institutions = mutableListOf<Institution>()
        val instruments = mutableListOf<Instrument>()
        val institutionRel = mutableMapOf<Award, Institution>()
        val instrumentRel = mutableMapOf<Award, Instrument>()

        val rs = computeRunningTime {
            NsfcConnector.nsfConnect(startYear, endYear)
        }

        println("Start to process the data...")

        rs.run {
            while (next()) {
                val date = getString("exec_time").split("至")
                val effectiveDate = date[0].split("-")[0]
                val expirationDate = date[1].split("-")[0]


                val awardID = getString("code").hashCode()
                val awardPrincipal = getString("principal")

                val institutionName = getString("institution_CN") ?: ""
                val instrumentName = getString("project_type") ?: ""

                institutionNames.add(institutionName)
                val institutionID = institutionNames.indexOf(institutionName) + 1000
                instrumentNames.add(instrumentName)
                val instrumentID = instrumentNames.indexOf(instrumentName) + 10000

                val award = Award(awardID, awardPrincipal, effectiveDate, expirationDate)
                val institution = Institution(institutionID, institutionName)
                val instrument = Instrument(instrumentID, instrumentName)

                awards.add(award)
                institutions.add(institution)
                instruments.add(instrument)
                institutionRel[award] = institution
                instrumentRel[award] = instrument
            }
        }

        println("Data processed")
        return ProcessedData(awards, institutions, instruments, institutionRel, instrumentRel)
    }
}
