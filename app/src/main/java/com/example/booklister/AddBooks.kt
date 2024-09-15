package com.example.booklister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booklister.databinding.ActivityAddBooksBinding

class AddBooks : AppCompatActivity() {
    private lateinit var binding: ActivityAddBooksBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.toolbarAddBook.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.fabAdd.setOnClickListener {
            val title = binding.edTitle.text.toString()
            val author = binding.edAuthor.text.toString()
            if (title.isNotBlank() && author.isNotBlank()) {
                val resultIntent = Intent()
                resultIntent.putExtra(MainActivity.EXTRA_BOOK_TITLE, title)
                resultIntent.putExtra(MainActivity.EXTRA_BOOK_AUTHOR, author)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(
                    this, "Sorry, cannot be empty", Toast.LENGTH_LONG
                ).show()
            }
        }

    }
}