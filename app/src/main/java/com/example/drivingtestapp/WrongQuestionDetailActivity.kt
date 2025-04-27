package com.example.drivingtestapp

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drivingtestapp.databinding.ActivityWrongQuestionDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class WrongQuestionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWrongQuestionDetailBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var currentQuestion: WrongQuestion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWrongQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        currentQuestion = intent.getParcelableExtra("WRONG_QUESTION") ?: return

        setupUI()
        setupDeleteButton()
    }

    private fun setupUI() {
        binding.tvQuestion.text = currentQuestion.question
        binding.tvOptionA.text = "A. ${currentQuestion.optionA}"
        binding.tvOptionB.text = "B. ${currentQuestion.optionB}"
        binding.tvOptionC.text = "C. ${currentQuestion.optionC}"
        binding.tvOptionD.text = "D. ${currentQuestion.optionD}"

        // Highlight đúng
        when (currentQuestion.correctAnswer) {
            0 -> binding.tvOptionA.setTextColor(Color.GREEN)
            1 -> binding.tvOptionB.setTextColor(Color.GREEN)
            2 -> binding.tvOptionC.setTextColor(Color.GREEN)
            3 -> binding.tvOptionD.setTextColor(Color.GREEN)
        }

        // Highlight đáp án người chọn (nếu sai)
        if (currentQuestion.userSelected != currentQuestion.correctAnswer) {
            when (currentQuestion.userSelected) {
                0 -> binding.tvOptionA.setTextColor(Color.RED)
                1 -> binding.tvOptionB.setTextColor(Color.RED)
                2 -> binding.tvOptionC.setTextColor(Color.RED)
                3 -> binding.tvOptionD.setTextColor(Color.RED)
            }
        }
    }

    private fun setupDeleteButton() {
        binding.btnDeleteWrong.setOnClickListener {
            db.collection("wrong_questions")
                .whereEqualTo("question", currentQuestion.question)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        for (doc in result.documents) {
                            db.collection("wrong_questions").document(doc.id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Đã xóa câu sai thành công!", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Xóa thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Không tìm thấy câu sai để xóa!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
