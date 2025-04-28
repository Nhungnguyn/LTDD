package com.example.drivingtestapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivingtestapp.databinding.ActivityViewIncorrectBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ViewIncorrectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewIncorrectBinding
    private lateinit var adapter: WrongQuestionAdapter
    private val wrongQuestions = mutableListOf<WrongQuestion>()
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewIncorrectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        listenToWrongQuestions()
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

    private fun listenToWrongQuestions() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        listenerRegistration = db.collection("users")
            .document(userId)
            .collection("wrong_questions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("ViewIncorrectActivity", "Listen failed.", error)
                    Toast.makeText(this, "Lỗi kết nối Firestore", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    wrongQuestions.clear()
                    for (doc in snapshots) {
                        val question = WrongQuestion(
                            questionId = (doc.getLong("questionId") ?: -1L).toInt(),
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

                    if (wrongQuestions.isEmpty()) {
                        Toast.makeText(this, "Bạn chưa có câu sai nào!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hủy listener khi activity bị hủy để tránh rò rỉ bộ nhớ
        listenerRegistration?.remove()
    }
}
