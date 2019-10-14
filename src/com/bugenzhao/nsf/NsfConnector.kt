package com.bugenzhao.nsf

import com.mysql.cj.jdbc.MysqlDataSource
import java.sql.ResultSet

fun nsfConnect(year: Int = 70): ResultSet {
    val nsfServer = SQLServer()
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

    println("Fetching data...")
    val stmt = conn.createStatement()
    return stmt.run {
        execute("use acmDB;")
        executeQuery("""
            SELECT
	            DISTINCT a.ID, a.Title, b.InstitutionID, c.`Name`
            FROM Award a
            INNER JOIN Award_Institution b ON a.ID = b.AwardID 
            INNER JOIN Institution c ON b.InstitutionID = c.ID 
            WHERE
	            a.ID LIKE "$year%";
        """.trimIndent())
    }
}