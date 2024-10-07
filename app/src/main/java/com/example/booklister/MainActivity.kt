package com.example.booklister

import android.content.DialogInterface
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booklister.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private val todoBooks = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.fabAddBook.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.custom_layout_add, null)
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()
            dialog.show()

            val btnAdd = dialogView.findViewById<MaterialButton>(R.id.btnAdd)
            val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
            val edtCustomTitle = dialogView.findViewById<EditText>(R.id.edtCustomTitle)
            val edtCustomAuthor = dialogView.findViewById<EditText>(R.id.edtCustomAuthor)

            btnAdd.setOnClickListener {
                val title = edtCustomTitle.text.toString().trim()
                val author = edtCustomAuthor.text.toString().trim()

                if (title.isNotEmpty()) {
                    val newBook = Book(title, author, false)
                    todoBooks.add(newBook)
                    adapter.notifyItemInserted(todoBooks.size - 1)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this, "Title is required",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        this.adapter = BookAdapter(this, todoBooks)
        binding.rvTodo.adapter = adapter
        binding.rvTodo.layoutManager = LinearLayoutManager(this)
        binding.rvTodo.setHasFixedSize(true)

//      Swipe RecyclerView
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback
            (0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//              Show delete confirmation dialog
                val dialogDelete = AlertDialog.Builder(viewHolder.itemView.context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this book?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        val position = viewHolder.bindingAdapterPosition
//                      Use Handler to safely delete item while RecyclerView is not computing layout
                        Handler(Looper.getMainLooper()).post {
                            adapter.deleteItem(position)
                        }
                        dialog.dismiss()
                        Toast.makeText(
                            this@MainActivity, "Book Deleted", Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        adapter.notifyItemChanged(viewHolder.bindingAdapterPosition)
                        dialog.dismiss()
                    }
                    .create()
                dialogDelete.show()
            }

            //          Customize the appearance of the swipe using RecyclerViewSwipeDecorator
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.red
                        )
                    )
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })
//      Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvTodo)
    }
}