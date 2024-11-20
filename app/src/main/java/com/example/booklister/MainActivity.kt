package com.example.booklister

import android.graphics.Canvas
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booklister.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter

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
                    val newBook = Book(id = System.currentTimeMillis(), title, author, false)
                    val updatedList = adapter.currentList.toMutableList()
                    updatedList.add(newBook)
                    adapter.submitList(updatedList)
                    dialog.dismiss()
                    Toast.makeText(this, "Book Added", Toast.LENGTH_LONG).show()
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

        this.adapter = BookAdapter(this)
        binding.rvTodo.adapter = adapter
        binding.rvTodo.layoutManager = LinearLayoutManager(this)
        binding.rvTodo.setHasFixedSize(true)

//      Swipe RecyclerView
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback
            (
//          Drag and drop support
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//          Swipe support
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
//              Update list when moving items
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition
                val updatedList = adapter.currentList.toMutableList()
//              Update the list
                Collections.swap(updatedList, fromPosition, toPosition)
//              Update the adapter
                adapter.submitList(updatedList)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//              Show delete confirmation dialog
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val dialogDelete = AlertDialog.Builder(this@MainActivity)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this book?")
                        .setPositiveButton("Delete") { dialog, _ ->
                            adapter.deleteItem(position)
                            dialog.dismiss()
                            Toast.makeText(this@MainActivity, "Book Deleted", Toast.LENGTH_LONG)
                                .show()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            adapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        .create()
                    dialogDelete.show()
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                  Reduce the opacity of the dragged item to indicate that it is moving
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
//              Restore item opacity when movement ends
                viewHolder.itemView.alpha = 1.0f
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