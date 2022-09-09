package com.mgchoi.smartportfolio.model

import com.mgchoi.smartportfolio.ViewStyle

class Member(
    var id: Int,
    var name: String,
    var image: String?,
    var url: String,
    var viewStyle: ViewStyle,
    var destroyable: Boolean
)