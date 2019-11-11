package com.bugenzhao.nsfc

import com.mysql.cj.jdbc.MysqlDataSource
import java.sql.ResultSet

object NsfcConnector {
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
            execute("use NSF_CN;")
            executeQuery("""
            SELECT DISTINCT
                a.code,
                a.principal,
                a.exec_time,
                a.institution_CN,
                a.project_type
            FROM
                nsfc_new a
        """.trimIndent())
        }
    }
}

