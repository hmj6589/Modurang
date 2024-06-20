package com.example.projectapplication

data class ItemData (
    // document 아이디
    var docId: String? = null,

    // document 내용
    var email: String? = null,
    var stars: Float = 0.0f,
    var comments: String? = null,
    var date_time: String? = null
)