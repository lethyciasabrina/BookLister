package com.example.booklister

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booklister.databinding.ActivityDescriptionBookBinding

class DescriptionBook : AppCompatActivity() {
    private lateinit var binding: ActivityDescriptionBookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.toolbarDesc.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val title = intent.getStringExtra("ED_TITLE")
        val author = intent.getStringExtra("ED_AUTHOR")

        binding.tvBookTitle.text = title
        binding.tvBookAuthor.text = author

    }
}