package com.bugenzhao.nsf.nodes

data class Award(override val id: Int,
                 val title: String,
                 val effectiveDate: String,
                 val expirationDate: String) : DataNode {
    override val type: String
        get() = "Award"
}