package com.bugenzhao.nsf.nodes

data class Institution(override val id: Int, val name: String) : DataNode {
    override val type: String
        get() = "Institution"
}