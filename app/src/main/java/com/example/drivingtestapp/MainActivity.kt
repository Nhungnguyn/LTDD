package com.example.drivingtestapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drivingtestapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kiểm tra trạng thái đăng nhập
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d(TAG, "User not logged in, redirecting to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Log.d(TAG, "User logged in: ${user.uid}")
            // Đã đăng nhập, thiết lập các sự kiện cho giao diện
            setupUI()
        }
    }

    private fun setupUI() {
        // Đề ngẫu nhiên
        binding.btnRandomTest.setOnClickListener {
            Log.d(TAG, "Navigating to RandomTestActivity")
            startActivity(Intent(this, RandomTestActivity::class.java))
        }

        // Thi theo bộ đề
        binding.btnTestBySet.setOnClickListener {
            Log.d(TAG, "Navigating to ReviewQuestionsActivity to select set")
            startActivity(Intent(this, ReviewQuestionsActivity::class.java))
        }

        // Xem câu bị sai
        binding.btnViewIncorrect.setOnClickListener {
            Log.d(TAG, "Navigating to ViewIncorrectActivity")
            startActivity(Intent(this, ViewIncorrectActivity::class.java))
        }

        // Ôn tập câu hỏi
        binding.btnReviewQuestions.setOnClickListener {
            Log.d(TAG, "Navigating to ReviewQuestionsActivity")
            startActivity(Intent(this, ReviewQuestionsActivity::class.java))
        }

        // Các biển báo
        binding.btnTrafficSigns.setOnClickListener {
            Log.d(TAG, "Navigating to TrafficSignsActivity")
            startActivity(Intent(this, TrafficSignsActivity::class.java))
        }

        // Mẹo ghi nhớ
        binding.btnMemoryAid.setOnClickListener {
            Log.d(TAG, "Navigating to MemoryAidActivity")
            startActivity(Intent(this, MemoryAidActivity::class.java))
        }

        // 60 câu điểm liệt
        binding.btnCriticalQuestions.setOnClickListener {
            Log.d(TAG, "Navigating to CriticalQuestionsActivity")
            startActivity(Intent(this, CriticalQuestionsActivity::class.java))
        }

        // Top 50 câu sai
        binding.btnTopWrong.setOnClickListener {
            Log.d(TAG, "Navigating to TopWrongActivity")
            startActivity(Intent(this, TopWrongActivity::class.java))
        }

        // Đăng xuất
        binding.btnLogout.setOnClickListener {
            Log.d(TAG, "Logging out user")
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}