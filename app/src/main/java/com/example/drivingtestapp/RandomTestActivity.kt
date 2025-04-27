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
import com.example.drivingtestapp.databinding.ActivityRandomTestBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Response

class RandomTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRandomTestBinding
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
        binding = ActivityRandomTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIndicators()
        loadQuestions()

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

    private fun loadQuestions() {
        lifecycleScope.launch {
            try {
                val response: Response<List<Question>> = RetrofitClient.apiService.getAllQuestions()
                if (response.isSuccessful) {
                    questions = response.body()?.shuffled()?.take(25) ?: emptyList()
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
                        showToast("Không có câu hỏi!")
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

        // Load hình ảnh nếu có
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
        val data = hashMapOf(
            "question" to question.question,
            "option_a" to question.option_a,
            "option_b" to question.option_b,
            "option_c" to question.option_c,
            "option_d" to question.option_d,
            "correct_answer" to question.answer,
            "user_selected" to userSelected,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("wrong_questions").add(data)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
    }
}
