package com.example.booklister

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booklister.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private var originalList: List<Book> = listOf()
    private var bookList: MutableList<Book> = mutableListOf()

    /* To-do:
        Bottom Navigation (Add and Update Book)
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val searchView = binding.searchView
        val topAppBar = binding.topAppBar
        val rvTodo = binding.rvTodo
        val chipGroup = binding.chipGroup

        adapter = BookAdapter(this) { updatedBook ->
            val index = originalList.indexOfFirst { it.id == updatedBook.id }
            if (index != -1) {
                val updatedList = originalList.toMutableList()
                updatedList[index] = updatedBook
                originalList = updatedList
                val selectedStatus = getSelectedStatus()
                val query = binding.searchView.query.toString()
                filterBooks(query, selectedStatus)
            } else {
                Toast.makeText(this, "Book not found", Toast.LENGTH_LONG).show()
            }
        }.apply {
            rvTodo.adapter = this
            rvTodo.layoutManager = LinearLayoutManager(this@MainActivity)
            rvTodo.setHasFixedSize(true)
            submitList(originalList.toMutableList())
        }

        configureSwipe()

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_add -> {
                    val dialogView = layoutInflater.inflate(R.layout.custom_layout_add, null)
                    val dialog = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create()
                    dialog.show()

                    val btnAdd = dialogView.findViewById<MaterialButton>(R.id.btnAdd)
                    val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
                    val edtCustomTitle = dialogView.findViewById<EditText>(R.id.edtCustomTitle)
                    val edtCustomAuthor = dialogView.findViewById<EditText>(R.id.edtCustomAuthor)
                    val chipGroup = dialogView.findViewById<ChipGroup>(R.id.chip_group)

                    btnAdd.setOnClickListener {
                        val title = edtCustomTitle.text.toString().trim()
                        val author = edtCustomAuthor.text.toString().trim()
                        val selectedChipId = chipGroup.checkedChipId
                        val selectedChip = if (selectedChipId != View.NO_ID) {
                            dialogView.findViewById<Chip>(selectedChipId)?.text?.toString() ?: ""
                        } else {
                            ""
                        }

                        val selectedStatus = when (selectedChipId) {
                            R.id.add_chip_not_started -> BookStatus.NOT_STARTED
                            R.id.add_chip_in_progress -> BookStatus.IN_PROGRESS
                            R.id.add_chip_stopped -> BookStatus.STOPPED
                            else -> BookStatus.NOT_STARTED // Default
                        }

                        if (title.isNotEmpty() && selectedChip.isNotEmpty()) {
                            val newBook = Book(
                                id = System.currentTimeMillis(),
                                title = title,
                                author = author,
                                false,
                                status = selectedStatus
                            )
                            originalList = originalList + newBook
                            bookList = originalList.filter { !it.isChecked }.toMutableList()
                            adapter.submitList(bookList)
                            dialog.dismiss()
                            Toast.makeText(this, "Book Added", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(
                                this, "Title and Status are required",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    btnCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    true
                }

                else -> false
            }
        }

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val selectedStatus = getSelectedStatus()
                    filterBooks(query, selectedStatus)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val selectedStatus = getSelectedStatus()
                    if (newText.isNullOrEmpty()) {
                        filterBooks(null, selectedStatus)
                    } else {
                        filterBooks(newText, selectedStatus)
                    }
                    return true
                }
            }).also {
            searchView.setOnCloseListener {
                val selectedStatus = getSelectedStatus()
                filterBooks(null, selectedStatus)
                false
            }
        }

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedStatus = when {
                checkedIds.contains(R.id.status_chip_all) -> null // All Books
                checkedIds.contains(R.id.status_chip_not_started) -> BookStatus.NOT_STARTED
                checkedIds.contains(R.id.status_chip_in_progress) -> BookStatus.IN_PROGRESS
                checkedIds.contains(R.id.status_chip_stopped) -> BookStatus.STOPPED
                checkedIds.contains(R.id.status_chip_done) -> BookStatus.DONE
                else -> null
            }
            bookList = if (selectedStatus != null) {
                originalList.filter { it.status == selectedStatus }.toMutableList()
            } else {
                originalList.toMutableList()
            }
            adapter.submitList(bookList)
        }
    }

    private fun getSelectedStatus(): BookStatus? {
        return when (binding.chipGroup.checkedChipId) {
            R.id.status_chip_not_started -> BookStatus.NOT_STARTED
            R.id.status_chip_in_progress -> BookStatus.IN_PROGRESS
            R.id.status_chip_stopped -> BookStatus.STOPPED
            R.id.status_chip_done -> BookStatus.DONE
            else -> null // All Books
        }
    }

    private fun filterBooks(query: String?, status: BookStatus?) {
        val filteredList = originalList.filter { book ->
            val matchesQuery = query?.let {
                book.title.contains(it, ignoreCase = true) || book.author.contains(
                    it,
                    ignoreCase = true
                )
            } ?: true
            val matchesStatus = status?.let { book.status == it } ?: true
            matchesQuery && matchesStatus
        }
        bookList = filteredList.toMutableList()
        adapter.submitList(bookList)
    }

    private fun configureSwipe() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        removeItem(position)
                    }

                    ItemTouchHelper.RIGHT -> {
                        val book = adapter.currentList[position]
                        editItem(book, position)
                        adapter.notifyItemChanged(position)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX < 0) {
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
                                R.color.color_crimson_red
                            )
                        )
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate()
                } else if (dX > 0) {
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
                                R.color.color_golden_yellow
                            )
                        )
                        .addActionIcon(R.drawable.ic_edit)
                        .create()
                        .decorate()
                }

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
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTodo)
    }

    private fun removeItem(position: Int) {
        val dialogDelete = AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this book?")
            .setPositiveButton("Delete") { dialog, _ ->
                val bookToRemove = bookList[position]
                originalList = originalList.filter { it.id != bookToRemove.id }
                bookList = originalList.filter { !it.isChecked }.toMutableList()
                adapter.submitList(bookList)
                dialog.dismiss()
                Toast.makeText(this@MainActivity, "Book Deleted", Toast.LENGTH_LONG)
                    .show()
            }.setNegativeButton("Cancel") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .create()
        dialogDelete.show()
    }

    private fun editItem(book: Book, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_layout_update, null)
        val edtCustomUpdateTitle = dialogView.findViewById<EditText>(R.id.edtCustomUpdateTitle)
        val edtCustomUpdateAuthor = dialogView.findViewById<EditText>(R.id.edtCustomUpdateAuthor)
        val chipGroup = dialogView.findViewById<ChipGroup>(R.id.chip_group)
        val btnCancelUpdate = dialogView.findViewById<Button>(R.id.btnCancelUpdate)
        val btnUpdate = dialogView.findViewById<Button>(R.id.btnUpdate)

        edtCustomUpdateTitle.setText(book.title)
        edtCustomUpdateAuthor.setText(book.author)
        when (book.status) {
            BookStatus.NOT_STARTED -> chipGroup.check(R.id.update_chip_not_started)
            BookStatus.IN_PROGRESS -> chipGroup.check(R.id.update_chip_in_progress)
            BookStatus.STOPPED -> chipGroup.check(R.id.update_chip_stopped)
            BookStatus.ALL_BOOKS -> TODO()
            BookStatus.DONE -> TODO()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnUpdate.setOnClickListener {
            val newTitle = edtCustomUpdateTitle.text.toString().trim()
            val newAuthor = edtCustomUpdateAuthor.text.toString().trim()
            val selectedChipId = chipGroup.checkedChipId

            val newStatus = when (selectedChipId) {
                R.id.update_chip_not_started -> BookStatus.NOT_STARTED
                R.id.update_chip_in_progress -> BookStatus.IN_PROGRESS
                R.id.update_chip_stopped -> BookStatus.STOPPED
                else -> book.status // Mantém o status atual se nenhum for selecionado
            }

            if (newTitle.isNotEmpty()) {
                val updateBook = book.copy(
                    title = newTitle,
                    author = newAuthor,
                    status = newStatus
                )
                val index = originalList.indexOfFirst { it.id == book.id }
                if (index != -1) {
                    val updatedList = originalList.toMutableList()
                    updatedList[index] = updateBook
                    originalList = updatedList
                    val selectedStatus = getSelectedStatus()
                    val query = binding.searchView.query.toString()
                    filterBooks(query, selectedStatus)
                    dialog.dismiss()
                    Toast.makeText(this, "Book updated", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Book not found", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Title is required", Toast.LENGTH_LONG).show()
            }
        }
        btnCancelUpdate.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}