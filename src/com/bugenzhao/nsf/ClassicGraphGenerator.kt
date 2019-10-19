package com.bugenzhao.nsf


import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val year = scanner.nextInt()
    DynamicPrettyGraphGenerator.prettyGraphGenerate(year, year)
}