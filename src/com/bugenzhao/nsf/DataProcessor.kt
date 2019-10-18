package com.bugenzhao.nsf

import com.bugenzhao.nsf.nodes.Award
import com.bugenzhao.nsf.nodes.Institution
import com.bugenzhao.nsf.nodes.Instrument
import com.bugenzhao.nsf.utils.computeRunningTime

data class ProcessedData(val awards: List<Award>,
                         val institutions: List<Institution>,
                         val instruments: List<Instrument>,
                         val institutionRel: Map<Award, Institution>,
                         val instrumentRel: Map<Award, Instrument>) {}

fun dataProcess(startYear: Int, endYear: Int): ProcessedData {
    val awards = mutableListOf<Award>()
    val institutions = mutableListOf<Institution>()
    val instruments = mutableListOf<Instrument>()
    val institutionRel = mutableMapOf<Award, Institution>()
    val instrumentRel = mutableMapOf<Award, Instrument>()

    val rs = computeRunningTime {
        nsfConnect(startYear, endYear)
    }

    println("Start to process the data...")

    rs.run {
        while (next()) {
            val awardID = getInt("ID")
            val awardTitle = getString("Title")
            val effectiveDate = getString("EffectiveDate")
            val expirationDate = getString("ExpirationDate")
            val institutionID = getInt("InstitutionID") + 100000
            val institutionName = getString("Name")
            val instrumentID = getInt("AwardInstrumentID")
            val instrumentValue = getString("Value")

            val award = Award(awardID, awardTitle, effectiveDate, expirationDate)
            val institution = Institution(institutionID, institutionName)
            val instrument = Instrument(instrumentID, instrumentValue)

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