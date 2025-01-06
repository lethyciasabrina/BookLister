package com.example.booklister

import android.graphics.Canvas
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private var originalList: List<Book> = listOf()
    private var bookList: MutableList<Book> = mutableListOf()

    /* To-do:
        Readme: How It Works
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
                    showBottomSheetAddDialog()
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

    private fun showBottomSheetAddDialog() {
        val bottomSheetView = layoutInflater.inflate(R.layout.custom_bottom_sheet_add, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val edtTitle = bottomSheetView.findViewById<EditText>(R.id.edtCustomTitle)
        val edtAuthor = bottomSheetView.findViewById<EditText>(R.id.edtCustomAuthor)
        val chipGroup = bottomSheetView.findViewById<ChipGroup>(R.id.chip_group)
        val btnAdd = bottomSheetView.findViewById<MaterialButton>(R.id.btnAdd)
        val btnCancel = bottomSheetView.findViewById<MaterialButton>(R.id.btnCancel)

        btnAdd.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val author = edtAuthor.text.toString().trim()
            val selectedChipId = chipGroup.checkedChipId

            val selectedStatus = when (selectedChipId) {
                R.id.add_chip_not_started -> BookStatus.NOT_STARTED
                R.id.add_chip_in_progress -> BookStatus.IN_PROGRESS
                R.id.add_chip_stopped -> BookStatus.STOPPED
                else -> BookStatus.NOT_STARTED // Default
            }

            if (title.isNotEmpty()) {
                val newBook = Book(
                    id = System.currentTimeMillis(),
                    title = title,
                    author = author,
                    isChecked = false,
                    status = selectedStatus
                )
                originalList = originalList + newBook
                bookList = originalList.toMutableList()
                adapter.submitList(bookList)
                dialog.dismiss()
                Toast.makeText(this, "Book added", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Title is required", Toast.LENGTH_LONG).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
        adapter.notifyItemChanged(position)
        dialogDelete.show()
    }

    private fun editItem(book: Book, position: Int) {
        val bottomSheetView = layoutInflater.inflate(R.layout.custom_bottom_sheet_update, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val edtCustomUpdateTitle = bottomSheetView.findViewById<EditText>(R.id.edtCustomUpdateTitle)
        val edtCustomUpdateAuthor =
            bottomSheetView.findViewById<EditText>(R.id.edtCustomUpdateAuthor)
        val chipGroup = bottomSheetView.findViewById<ChipGroup>(R.id.chip_group)
        val btnUpdate = bottomSheetView.findViewById<MaterialButton>(R.id.btnUpdate)
        val btnCancel = bottomSheetView.findViewById<MaterialButton>(R.id.btnCancel)

        edtCustomUpdateTitle.setText(book.title)
        edtCustomUpdateAuthor.setText(book.author)

        when (book.status) {
            BookStatus.NOT_STARTED -> chipGroup.check(R.id.update_chip_not_started)
            BookStatus.IN_PROGRESS -> chipGroup.check(R.id.update_chip_in_progress)
            BookStatus.STOPPED -> chipGroup.check(R.id.update_chip_stopped)
            else -> chipGroup.clearCheck()
        }

        btnUpdate.setOnClickListener {
            val newTitle = edtCustomUpdateTitle.text.toString().trim()
            val newAuthor = edtCustomUpdateAuthor.text.toString().trim()
            val selectedChipId = chipGroup.checkedChipId

            val newStatus = when (selectedChipId) {
                R.id.update_chip_not_started -> BookStatus.NOT_STARTED
                R.id.update_chip_in_progress -> BookStatus.IN_PROGRESS
                R.id.update_chip_stopped -> BookStatus.STOPPED
                else -> book.status
            }

            if (newTitle.isNotEmpty()) {
                val updatedBook = book.copy(
                    title = newTitle,
                    author = newAuthor,
                    status = newStatus
                )
                val index = originalList.indexOfFirst { it.id == book.id }
                if (index != -1) {
                    val updatedList = originalList.toMutableList()
                    updatedList[index] = updatedBook
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

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}