package com.example.drivingtestapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivingtestapp.databinding.ActivityTestResultBinding

class TestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestResultBinding
    private lateinit var resultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val questions = intent.getParcelableArrayListExtra<Question>("QUESTIONS") ?: arrayListOf()
        val userAnswers = intent.getIntegerArrayListExtra("USER_ANSWERS") ?: arrayListOf()
        val score = intent.getIntExtra("SCORE", 0)

        if (questions.isEmpty() || userAnswers.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu kết quả.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Hiển thị điểm
        binding.tvScore.text = "Điểm số: $score/${questions.size}"

        // Setup RecyclerView
        resultAdapter = ResultAdapter(questions, userAnswers)
        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = resultAdapter

        // Xử lý nút Quay lại
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
