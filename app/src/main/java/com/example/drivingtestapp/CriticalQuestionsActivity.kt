package com.example.drivingtestapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivingtestapp.databinding.ActivityQuestionBinding
import kotlinx.coroutines.launch
import retrofit2.Response

class CriticalQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionBinding
    private var questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCriticalQuestions()
    }

    private fun loadCriticalQuestions() {
        lifecycleScope.launch {
            try {
                Log.d("CriticalQuestionsActivity", "Fetching critical questions")
                val response: Response<List<Question>> = RetrofitClient.apiService.getCriticalQuestions()

                if (response.isSuccessful) {
                    questions = response.body()?.toMutableList() ?: mutableListOf()
                    if (questions.isNotEmpty()) {
                        displayQuestion()
                    } else {
                        Toast.makeText(this@CriticalQuestionsActivity, "Không tìm thấy câu hỏi điểm liệt", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@CriticalQuestionsActivity, "Lỗi tải câu hỏi", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CriticalQuestionsActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.tvQuestion.text = question.question
            binding.radioGroupOptions.clearCheck()

            val options = question.getOptions()
            binding.radioButtonA.text = options[0]
            binding.radioButtonB.text = options[1]
            binding.radioButtonC.text = options[2]
            binding.radioButtonD.text = options[3]

            binding.btnSubmit.setOnClickListener {
                currentQuestionIndex++
                displayQuestion()
            }
        } else {
            Toast.makeText(this, "Hoàn thành xem xét câu hỏi điểm liệt!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}