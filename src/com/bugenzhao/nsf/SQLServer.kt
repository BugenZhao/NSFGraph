package com.bugenzhao.nsf

import java.nio.file.Files
import java.nio.file.Paths

object SQLServer {
    const val serverName = "server.acemap.cn"
    const val port = 13306
    const val user = "remote"
    val password = Files.newInputStream(Paths.get("ps.txt")).bufferedReader().readLine()!!
    const val characterEncoding = "UTF-8"
}