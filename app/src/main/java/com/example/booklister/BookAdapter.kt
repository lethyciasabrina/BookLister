package com.example.booklister

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val context: Context,
    private val onUpdate: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    private var originalList: List<Book> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_book, parent, false
        )
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val todo = getItem(position)
        val book = getItem(position)

        holder.apply {
            tvTitle.text = todo.title
            if (todo.author.isEmpty()) {
                tvAuthor.visibility = View.GONE
            } else {
                tvAuthor.text = todo.author
                tvAuthor.visibility = View.VISIBLE
            }
            cbTodo.isChecked = todo.isChecked

            pencilUpdate.setOnClickListener {
                val adapterPosition = bindingAdapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    showUpdateDialog(todo, adapterPosition)
                }
            }

            cbTodo.setOnCheckedChangeListener { _, isChecked ->
                handleCheckBoxChange(book.id, isChecked)
            }
        }
    }

    private fun showUpdateDialog(todo: Book, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_layout_update, null)
        val edtCustomUpdateTitle = dialogView.findViewById<EditText>(R.id.edtCustomUpdateTitle)
        val edtCustomUpdateAuthor = dialogView.findViewById<EditText>(R.id.edtCustomUpdateAuthor)
        val btnCancelUpdate = dialogView.findViewById<Button>(R.id.btnCancelUpdate)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        edtCustomUpdateTitle.setText(todo.title)
        edtCustomUpdateAuthor.setText(todo.author)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnUpdate.setOnClickListener {
            val newTitle = edtCustomUpdateTitle.text.toString().trim()
            val newAuthor = edtCustomUpdateAuthor.text.toString().trim()

            if (newTitle.isNotEmpty()) {
                val updatedBook = todo.copy(title = newTitle, author = newAuthor)
                onUpdate(updatedBook)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Title is required", Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()

        btnCancelUpdate.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun submitList(list: List<Book>?) {
        super.submitList(list)
        if (list != null) {
//          Update the original list
            originalList = list.toMutableList()
        }
    }

    private fun handleCheckBoxChange(itemId: Long, isChecked: Boolean) {
        val updatedList = currentList.toMutableList()
//      Find index based on ID
        val itemIndex = updatedList.indexOfFirst { it.id == itemId }
        if (itemIndex != -1) {
            if (isChecked) {
                updatedList.removeAt(itemIndex)
            } else {
                updatedList[itemIndex] = updatedList[itemIndex].copy(isChecked = isChecked)
            }
            submitList(updatedList)
        }
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val cbTodo: CheckBox = view.findViewById(R.id.cbTodo)
        val pencilUpdate: ImageView = view.findViewById(R.id.pencilUpdate)
    }

    fun deleteItem(adapterPosition: Int) {
        if (adapterPosition != RecyclerView.NO_POSITION) {
            val updatedList = currentList.toMutableList()
            updatedList.removeAt(adapterPosition)
            submitList(updatedList)
        }
    }

    //  Implementing DiffUtil to compare items
    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.title == newItem.title && oldItem.author == newItem.author
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

}