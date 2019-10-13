package com.bugenzhao.nsf

fun main() {
    val (awards, insts, relationships) = dataProcess(70);
    awards.forEach(::println)
}