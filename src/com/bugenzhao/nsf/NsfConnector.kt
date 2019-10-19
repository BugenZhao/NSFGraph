package com.bugenzhao.nsf

import com.mysql.cj.jdbc.MysqlDataSource
import java.sql.ResultSet

object NsfConnector {
    fun nsfConnect(startYear: Int, endYear: Int): ResultSet {
        val nsfServer = SQLServer
        println("Connecting to ${nsfServer.serverName}...")

        val dataSource = MysqlDataSource().apply {
            serverName = nsfServer.serverName
            port = nsfServer.port
            user = nsfServer.user
            password = nsfServer.password
            characterEncoding = nsfServer.characterEncoding
        }
        val conn = dataSource.connection
        println("Connected")

        println("Fetching data... ($startYear..$endYear)")
        val stmt = conn.createStatement()
        return stmt.run {
            execute("use acmDB;")
            executeQuery("""
            SELECT DISTINCT
                a.ID,
                a.Title,
                a.EffectiveDate,
                a.ExpirationDate,
                b.InstitutionID,
                c.`Name`,
                d.AwardInstrumentID,
                e.`Value` 
            FROM
                Award a
                INNER JOIN Award_Institution b ON a.ID = b.AwardID
                INNER JOIN Institution c ON b.InstitutionID = c.ID
                INNER JOIN Award_AwardInstrument d ON a.ID = d.AwardID
                INNER JOIN AwardInstrument e ON d.AwardInstrumentID = e.ID 
            WHERE
                STR_TO_DATE( a.EffectiveDate, '%m/%d/%Y' ) <= STR_TO_DATE( '12/31/$endYear', '%m/%d/%Y' ) 
                AND STR_TO_DATE( a.ExpirationDate, '%m/%d/%Y' ) >= STR_TO_DATE( '01/01/$startYear', '%m/%d/%Y' );
        """.trimIndent())
        }
    }
}

