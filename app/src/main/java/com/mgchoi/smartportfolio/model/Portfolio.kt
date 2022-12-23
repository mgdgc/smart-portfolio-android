package com.mgchoi.smartportfolio.model

class Portfolio(
    var id: Int,
    var memberId: Int,
    var title: String,
    var content: String,
    var url: String,
    var image: String?
)