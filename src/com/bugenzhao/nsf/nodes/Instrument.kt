package com.bugenzhao.nsf.nodes

data class Instrument(override val id: Int, val value: String) : DataNode {
    override val type: String
        get() = "Instrument"
}