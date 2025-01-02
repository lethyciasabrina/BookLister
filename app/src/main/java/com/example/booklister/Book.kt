package com.example.booklister

data class Book(
    val id: Long,
    val title: String,
    val author: String,
    val isChecked: Boolean,
    val status: BookStatus = BookStatus.ALL_BOOKS
)

enum class BookStatus(val displayName: String) {
    ALL_BOOKS("All Books"),
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    STOPPED("Stopped"),
    DONE("Done")
}
