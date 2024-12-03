package com.example.booklister

data class Book(
    val id: Long,
    var title: String,
    var author: String,
    var isChecked: Boolean // = false
)
