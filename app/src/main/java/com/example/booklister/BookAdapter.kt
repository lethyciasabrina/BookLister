package com.example.booklister

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val context: Context,
    private var todoBook: MutableList<Book>
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_book, parent, false
        )
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookAdapter.BookViewHolder, position: Int) {
        val todo = todoBook[position]
        holder.apply {
            tvTitle.text = todo.title
            if (todo.author.isNullOrEmpty()) {
                tvAuthor.visibility = View.GONE
            } else {
                tvAuthor.text = todo.author
                tvAuthor.visibility = View.VISIBLE
            }
            cbTodo.isChecked = todo.isChecked

//          Update item by clicking on the pencil icon
            pencilUpdate.setOnClickListener {

                val dialogView = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.custom_layout_update, null)

                val edtCustomUpdateTitle =
                    dialogView.findViewById<EditText>(R.id.edtCustomUpdateTitle)
                val edtCustomUpdateAuthor =
                    dialogView.findViewById<EditText>(R.id.edtCustomUpdateAuthor)
                val btnCancelUpdate = dialogView.findViewById<Button>(R.id.btnCancelUpdate)
                val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

                edtCustomUpdateTitle.setText(todo.title)
                edtCustomUpdateAuthor.setText(todo.author)

                val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                    .setView(dialogView)
                val dialog = dialogBuilder.create()

                btnUpdate.setOnClickListener {
                    val newTitle = edtCustomUpdateTitle.text.toString().trim()
                    val newAuthor = edtCustomUpdateAuthor.text.toString().trim()

                    if (newTitle.isNotEmpty()) {
                        todo.title = newTitle
                        todo.author = newAuthor
                        notifyItemChanged(position)
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

//          Listener of checkbox
            cbTodo.setOnCheckedChangeListener { _, isChecked ->
                Handler(Looper.getMainLooper()).post {
                    if (isChecked && position < todoBook.size) {
                        todoBook.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, todoBook.size)
                    } else {
                        todo.isChecked = isChecked
                        if (position < todoBook.size) {
                            notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return todoBook.size
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val cbTodo: CheckBox = view.findViewById(R.id.cbTodo)
        val pencilUpdate: ImageView = view.findViewById(R.id.pencilUpdate)
    }

    fun deleteItem(adapterPosition: Int) {
        todoBook.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }
}