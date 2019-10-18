package com.bugenzhao.nsf.utils

fun <T> computeRunningTime(action: (() -> T)): T {
    val startTime = System.currentTimeMillis()
    val ret = action.invoke()
    println("Completed in ${System.currentTimeMillis() - startTime} ms")
    return ret
}
