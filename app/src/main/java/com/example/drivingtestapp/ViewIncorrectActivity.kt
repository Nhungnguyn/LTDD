package com.example.drivingtestapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivingtestapp.databinding.ActivityViewIncorrectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewIncorrectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewIncorrectBinding
    private lateinit var adapter: WrongQuestionAdapter
    private val wrongQuestions = mutableListOf<WrongQuestion>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewIncorrectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadWrongQuestionsFromFirestore()
    }

    private fun setupRecyclerView() {
        adapter = WrongQuestionAdapter(wrongQuestions)
        binding.rvWrongQuestions.layoutManager = LinearLayoutManager(this)
        binding.rvWrongQuestions.adapter = adapter
    }

    private fun loadWrongQuestionsFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("users")
            .document(userId)
            .collection("wrong_questions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                wrongQuestions.clear()
                for (doc in result) {
                    val question = WrongQuestion(
                        question = doc.getString("question") ?: "",
                        optionA = doc.getString("option_a") ?: "",
                        optionB = doc.getString("option_b") ?: "",
                        optionC = doc.getString("option_c") ?: "",
                        optionD = doc.getString("option_d") ?: "",
                        correctAnswer = (doc.getLong("correct_answer") ?: -1L).toInt(),
                        userSelected = (doc.getLong("user_selected") ?: -1L).toInt(),
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                    wrongQuestions.add(question)
                }

                if (wrongQuestions.isNotEmpty()) {
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Bạn chưa có câu sai nào!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("ViewIncorrectActivity", "Error: ${e.message}")
                Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
