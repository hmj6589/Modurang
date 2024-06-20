package com.example.projectapplication

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml


@Xml(name = "response")
data class ApiResponse(
    @Element
    var header: ResponseHeader? = null,

    @Element
    var body: ResponseBody? = null
)

@Xml(name = "header")
data class ResponseHeader(
    @PropertyElement
    var resultCode: String? = null,

    @PropertyElement
    var resultMsg: String? = null
)

@Xml(name = "body")
data class ResponseBody(
    @Element
    var items: Items? = null,

    @PropertyElement
    var numOfRows: Int? = null,

    @PropertyElement
    var pageNo: Int? = null,

    @PropertyElement
    var totalCount: Int? = null
)

@Xml(name = "items")
data class Items(
    @Element
    var item: MutableList<XmlItem>? = null
)

@Xml(name = "item")
data class XmlItem(
    @PropertyElement
    var title: String? = null,

    @PropertyElement
    var addr1: String? = null,

    @PropertyElement
    var firstimage: String? = null,

    @PropertyElement
    var tel: String? = null
)