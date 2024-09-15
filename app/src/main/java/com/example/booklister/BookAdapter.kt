package com.example.booklister

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
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
            cbTodo.isChecked = todo.isChecked

            tvTitle.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, DescriptionBook::class.java).also {
                    it.putExtra("ED_TITLE", todo.title)
                    it.putExtra("ED_AUTHOR", todo.author)
                }
                context.startActivity(intent)
            }

            // Listener of checkbox
            cbTodo.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Remove item
                    todoBook.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, todoBook.size)
                } else {
                    todo.isChecked = isChecked
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return todoBook.size
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val cbTodo: CheckBox = view.findViewById(R.id.cbTodo)
    }
}