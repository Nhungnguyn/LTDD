package com.example.drivingtestapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.drivingtestapp.databinding.ActivityTestBySetBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log // Thêm import này

class TestBySetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBySetBinding
    private lateinit var timer: CountDownTimer
    private lateinit var indicatorAdapter: QuestionIndicatorAdapter
    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var score = 0
    private val answeredQuestions = mutableListOf<Int>()
    private val userAnswers = mutableListOf<Int>()
    private val wrongQuestions = mutableListOf<Question>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators()

        val setName = intent.getStringExtra("SET_NAME")
        if (setName != null) {
            loadQuestionsBySet(setName)
        } else {
            showToast("Không tìm thấy bộ đề!")
            finish()
        }

        binding.btnSubmit.setOnClickListener { handleSubmit() }
    }

    private fun setupIndicators() {
        binding.rvQuestionIndicators.layoutManager = GridLayoutManager(this, 10)
    }

    private fun startTimer() {
        timer = object : CountDownTimer(19 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = "Thời gian: ${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
            }

            override fun onFinish() {
                confirmSubmit()
            }
        }.start()
    }

    private fun loadQuestionsBySet(setName: String) {
        lifecycleScope.launch {
            try {
                val response: Response<List<Question>> = RetrofitClient.apiService.getQuestionsBySet(setName)
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    if (questions.isNotEmpty()) {
                        repeat(questions.size) { userAnswers.add(-1) }
                        startTimer()

                        indicatorAdapter = QuestionIndicatorAdapter(
                            questions.size,
                            currentQuestionIndex,
                            answeredQuestions
                        ) { position ->
                            currentQuestionIndex = position
                            displayQuestion()
                        }
                        binding.rvQuestionIndicators.adapter = indicatorAdapter
                        displayQuestion()
                    } else {
                        showToast("Bộ đề không có câu hỏi!")
                        finish()
                    }
                } else {
                    showToast("Lỗi server: ${response.code()}")
                    finish()
                }
            } catch (e: Exception) {
                showToast("Lỗi kết nối: ${e.message}")
                finish()
            }
        }
    }

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]

        binding.tvQuestionNumber.text = "Câu ${currentQuestionIndex + 1}"
        binding.tvQuestion.text = question.question

        if (!question.imageUrl.isNullOrEmpty()) {
            binding.imgQuestion.visibility = View.VISIBLE
            Picasso.get()
                .load(question.imageUrl)
                .into(binding.imgQuestion)
        } else {
            binding.imgQuestion.visibility = View.GONE
        }

        setupOption(binding.rbOptionA, question.option_a, "A")
        setupOption(binding.rbOptionB, question.option_b, "B")
        setupOption(binding.rbOptionC, question.option_c, "C")
        setupOption(binding.rbOptionD, question.option_d, "D")

        binding.rgOptions.setOnCheckedChangeListener(null)

        when (userAnswers[currentQuestionIndex]) {
            0 -> binding.rbOptionA.isChecked = true
            1 -> binding.rbOptionB.isChecked = true
            2 -> binding.rbOptionC.isChecked = true
            3 -> binding.rbOptionD.isChecked = true
            else -> binding.rgOptions.clearCheck()
        }

        binding.rgOptions.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.rbOptionA -> 0
                R.id.rbOptionB -> 1
                R.id.rbOptionC -> 2
                R.id.rbOptionD -> 3
                else -> -1
            }
            if (selectedOption != -1) {
                userAnswers[currentQuestionIndex] = selectedOption
                if (!answeredQuestions.contains(currentQuestionIndex)) {
                    answeredQuestions.add(currentQuestionIndex)
                }
                indicatorAdapter.addAnsweredQuestion(currentQuestionIndex)
            }
        }

        indicatorAdapter.updateCurrentQuestion(currentQuestionIndex)
    }

    private fun setupOption(radioButton: RadioButton, text: String?, prefix: String) {
        if (!text.isNullOrEmpty()) {
            radioButton.visibility = View.VISIBLE
            radioButton.text = "$prefix. $text"
        } else {
            radioButton.visibility = View.GONE
        }
    }

    private fun handleSubmit() {
        val unanswered = userAnswers.count { it == -1 }
        if (unanswered > 0) {
            AlertDialog.Builder(this)
                .setTitle("Bạn còn $unanswered câu chưa làm")
                .setMessage("Bạn muốn tiếp tục làm bài hay nộp luôn?")
                .setPositiveButton("Tiếp tục làm") { dialog, _ -> dialog.dismiss() }
                .setNegativeButton("Nộp bài") { _, _ -> confirmSubmit() }
                .show()
        } else {
            confirmSubmit()
        }
    }

    private fun confirmSubmit() {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận nộp bài")
            .setMessage("Bạn có chắc chắn muốn nộp bài không?")
            .setPositiveButton("Nộp bài") { _, _ -> finishTest() }
            .setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun finishTest() {
        if (::timer.isInitialized) timer.cancel()

        score = 0
        wrongQuestions.clear()

        questions.forEachIndexed { index, question ->
            val userAnswer = userAnswers.getOrNull(index) ?: -1
            if (userAnswer == question.answer) {
                score++
            } else {
                wrongQuestions.add(question)
                saveWrongQuestionToFirestore(question, userAnswer)
            }
        }

        val intent = Intent(this, TestResultActivity::class.java)
        intent.putParcelableArrayListExtra("QUESTIONS", ArrayList(questions))
        intent.putIntegerArrayListExtra("USER_ANSWERS", ArrayList(userAnswers))
        intent.putParcelableArrayListExtra("WRONG_QUESTIONS", ArrayList(wrongQuestions))
        intent.putExtra("SCORE", score)
        startActivity(intent)
        finish()
    }

    private fun saveWrongQuestionToFirestore(question: Question, userSelected: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.d("RandomTestActivity", "Saving wrong question for user: $userId, questionId: ${question.id}")
            // Lưu vào wrong_questions của người dùng
            val userData = hashMapOf(
                "questionId" to question.id,
                "question" to question.question,
                "option_a" to question.option_a,
                "option_b" to question.option_b,
                "option_c" to question.option_c,
                "option_d" to question.option_d,
                "correct_answer" to question.answer,
                "user_selected" to userSelected,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("users")
                .document(userId)
                .collection("wrong_questions")
                .add(userData)
                .addOnSuccessListener {
                    Log.d("RandomTestActivity", "Successfully saved user wrong question: ${question.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("RandomTestActivity", "Error saving user wrong question: ${e.message}")
                }

            // Cập nhật thống kê chung trong wrong_questions_stats
            val statsRef = db.collection("wrong_questions_stats").document(question.id.toString())
            db.runTransaction { transaction ->
                val snapshot = transaction.get(statsRef)
                val newCount = if (snapshot.exists()) {
                    (snapshot.getLong("wrongCount") ?: 0) + 1
                } else {
                    1L
                }

                val statsData = hashMapOf(
                    "questionId" to question.id,
                    "question" to question.question,
                    "option_a" to question.option_a,
                    "option_b" to question.option_b,
                    "option_c" to question.option_c,
                    "option_d" to question.option_d,
                    "correct_answer" to question.answer,
                    "wrongCount" to newCount,
                    "lastUpdated" to System.currentTimeMillis()
                )
                transaction.set(statsRef, statsData)
                newCount
            }.addOnSuccessListener { newCount ->
                Log.d("RandomTestActivity", "Successfully updated wrong_questions_stats for questionId: ${question.id}, new wrongCount: $newCount")
            }.addOnFailureListener { e ->
                Log.e("RandomTestActivity", "Error updating wrong questions stats: ${e.message}")
            }
        } else {
            Log.w("RandomTestActivity", "User not logged in, skipping save to Firestore")
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
    }
}
