package com.bugenzhao.nsfc

import java.nio.file.Files
import java.nio.file.Paths

object SQLServer {
    const val serverName = "10.10.11.2"
    const val port = 3306
    const val user = "teammate"
    val password = Files.newInputStream(Paths.get("ps.txt")).bufferedReader().readLines()[1]
    const val characterEncoding = "UTF-8"
}