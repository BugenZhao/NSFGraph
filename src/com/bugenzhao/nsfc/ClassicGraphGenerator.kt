package com.bugenzhao.nsfc


import com.bugenzhao.nsf.DynamicPrettyGraphGenerator
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val year = scanner.nextInt()
    DynamicPrettyGraphGenerator.prettyGraphGenerate(year, year)
}