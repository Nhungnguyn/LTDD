package com.example.drivingtestapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivingtestapp.databinding.ActivityTopWrongBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TopWrongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopWrongBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: WrongQuestionAdapter
    private val wrongQuestions = mutableListOf<WrongQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopWrongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupListeners()
        loadTopWrongQuestions()
    }

    private fun setupRecyclerView() {
        adapter = WrongQuestionAdapter(wrongQuestions) { question ->
            val intent = Intent(this, WrongQuestionDetailActivity::class.java)
            intent.putExtra("WRONG_QUESTION", question)
            startActivity(intent)
        }
        binding.rvWrongQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvWrongQuestions.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTopWrongQuestions() {
        db.collection("wrong_questions_stats")
            .orderBy("wrongCount", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                wrongQuestions.clear()
                for (doc in result.documents) {
                    val question = WrongQuestion(
                        questionId = (doc.getLong("questionId") ?: -1L).toInt(),
                        question = doc.getString("question") ?: "",
                        optionA = doc.getString("option_a") ?: "",
                        optionB = doc.getString("option_b") ?: "",
                        optionC = doc.getString("option_c") ?: "",
                        optionD = doc.getString("option_d") ?: "",
                        correctAnswer = (doc.getLong("correct_answer") ?: -1L).toInt(),
                        userSelected = -1,
                        timestamp = doc.getLong("lastUpdated") ?: 0L,
                        wrongCount = (doc.getLong("wrongCount") ?: 0L).toInt()
                    )
                    wrongQuestions.add(question)
                }

                if (wrongQuestions.isEmpty()) {
                    Toast.makeText(this, "Chưa có câu hỏi sai nào!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
