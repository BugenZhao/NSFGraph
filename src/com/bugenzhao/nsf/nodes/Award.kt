package com.bugenzhao.nsf.nodes

data class Award(val id: Int,
                 val title: String,
                 val effectiveDate: String,
                 val expirationDate: String) : DataNode()