package com.example.booklister

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val context: Context,
    private val onBookChecked: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_book, parent, false
        )
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val book = getItem(position)
        holder.apply {
            tvTitle.text = book.title
            if (book.author.isEmpty()) {
                tvAuthor.visibility = View.GONE
            } else {
                tvAuthor.text = book.author
                tvAuthor.visibility = View.VISIBLE
            }
            cbRead.setOnCheckedChangeListener(null)
            cbRead.isChecked = book.isChecked
            cbRead.setOnCheckedChangeListener { _, isChecked ->
                val updatedBook = book.copy(
                    isChecked = isChecked,
                    status = if (isChecked) BookStatus.DONE else BookStatus.NOT_STARTED
                )
                onBookChecked(updatedBook)
            }
        }
    }

    override fun submitList(list: List<Book>?) {
        super.submitList(list)
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val cbRead: CheckBox = view.findViewById(R.id.isChecked)
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}