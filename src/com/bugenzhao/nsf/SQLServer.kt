package com.bugenzhao.nsf

import java.nio.file.Files
import java.nio.file.Paths

class SQLServer {
    val serverName = "server.acemap.cn"
    val port = 13306
    val user = "remote"
    val password = Files.newInputStream(Paths.get("ps.txt")).bufferedReader().readLine()!!
    val characterEncoding = "UTF-8"
}