package com.example.booklister

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val isChecked: Boolean // = false?
)
