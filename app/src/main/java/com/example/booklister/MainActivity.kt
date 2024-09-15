package com.example.booklister

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booklister.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private val todoBooks = mutableListOf<Book>()

    private val addBookLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val addTitle = data?.getStringExtra(EXTRA_BOOK_TITLE)
            val addAuthor = data?.getStringExtra(EXTRA_BOOK_AUTHOR)

            if (addTitle != null && addAuthor != null) {
                todoBooks.add(Book(addTitle, addAuthor, false))
                val position = todoBooks.size - 1
                adapter.notifyItemInserted(position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.fabAddBook.setOnClickListener {
            val intent = Intent(this, AddBooks::class.java)
            addBookLauncher.launch(intent)
        }

        this.adapter = BookAdapter(todoBooks)
        binding.rvTodo.adapter = adapter
        binding.rvTodo.layoutManager = LinearLayoutManager(this)
        binding.rvTodo.setHasFixedSize(true)
    }

    companion object {
        const val EXTRA_BOOK_TITLE = "com.example.booklister.BOOK_TITLE"
        const val EXTRA_BOOK_AUTHOR = "com.example.booklister.BOOK_AUTHOR"
    }
}