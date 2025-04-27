package com.example.drivingtestapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.drivingtestapp.databinding.ActivityQuestionBinding
import kotlinx.coroutines.launch
import retrofit2.Response

class TopWrongActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuestionBinding
    private val db = FirebaseFirestore.getInstance()
    private var questions = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTopWrongQuestions()
    }

    private fun loadTopWrongQuestions() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem câu hỏi trả lời sai", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users").document(user.uid)
            .collection("attempts")
            .get()
            .addOnSuccessListener { attempts ->
                val wrongQuestionIds = mutableListOf<String>()
                for (attempt in attempts) {
                    val wrongQuestions = attempt.get("wrongQuestions") as? List<String> ?: emptyList()
                    wrongQuestionIds.addAll(wrongQuestions)
                }

                val wrongCountMap = wrongQuestionIds.groupingBy { it }.eachCount()
                val topWrongIds = wrongCountMap.entries
                    .sortedByDescending { it.value }
                    .take(10)
                    .map { it.key }

                if (topWrongIds.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa trả lời sai câu hỏi nào", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }

                fetchQuestions(topWrongIds)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun fetchQuestions(topWrongIds: List<String>) {
        lifecycleScope.launch {
            try {
                val response: Response<List<Question>> = RetrofitClient.apiService.getAllQuestions()

                if (response.isSuccessful) {
                    val allQuestions = response.body() ?: emptyList()
                    questions = allQuestions.filter { it.id.toString() in topWrongIds }.toMutableList()
                    if (questions.isEmpty()) {
                        Toast.makeText(this@TopWrongActivity, "Không tìm thấy câu hỏi", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        displayQuestion()
                    }
                } else {
                    Toast.makeText(this@TopWrongActivity, "Lỗi tải câu hỏi", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TopWrongActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
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
                val selectedOption = when (binding.radioGroupOptions.checkedRadioButtonId) {
                    binding.radioButtonA.id -> 0
                    binding.radioButtonB.id -> 1
                    binding.radioButtonC.id -> 2
                    binding.radioButtonD.id -> 3
                    else -> -1
                }

                if (selectedOption == -1) {
                    Toast.makeText(this, "Vui lòng chọn đáp án!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedOption == question.answer) {
                    Toast.makeText(this, "Đúng!", Toast.LENGTH_SHORT).show()
                } else {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        db.collection("users").document(user.uid)
                            .collection("attempts").add(
                                mapOf(
                                    "wrongQuestions" to listOf(question.id.toString()),
                                    "timestamp" to com.google.firebase.Timestamp.now()
                                )
                            )
                    }
                    Toast.makeText(this, "Sai! Đáp án đúng: ${question.getOptions()[question.answer]}", Toast.LENGTH_LONG).show()
                }

                currentQuestionIndex++
                if (currentQuestionIndex < questions.size) {
                    displayQuestion()
                } else {
                    Toast.makeText(this, "Hoàn thành xem xét câu hỏi trả lời sai nhiều nhất!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}