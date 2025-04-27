package com.example.drivingtestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drivingtestapp.databinding.ActivityMemoryAidBinding

class MemoryAidActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoryAidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemoryAidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvMemoryAid.text = "Danh sách mẹo ghi nhớ sẽ hiển thị ở đây (tùy chỉnh thêm)."
    }
}