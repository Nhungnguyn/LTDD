package com.example.drivingtestapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drivingtestapp.databinding.ItemResultBinding

class ResultAdapter(
    private val questions: List<Question>,
    private val userAnswers: List<Int>
) : RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {

    inner class ResultViewHolder(val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val question = questions[position]
        val userAnswer = userAnswers.getOrNull(position) ?: -1

        holder.binding.tvQuestionNumber.text = "Câu ${position + 1}"
        holder.binding.tvQuestion.text = question.question

        // Gán text cho đáp án
        setupOption(holder.binding.rbOptionA, "A", question.option_a)
        setupOption(holder.binding.rbOptionB, "B", question.option_b)
        setupOption(holder.binding.rbOptionC, "C", question.option_c)
        setupOption(holder.binding.rbOptionD, "D", question.option_d)

        holder.binding.rbOptionA.isChecked = (userAnswer == 0)
        holder.binding.rbOptionB.isChecked = (userAnswer == 1)
        holder.binding.rbOptionC.isChecked = (userAnswer == 2)
        holder.binding.rbOptionD.isChecked = (userAnswer == 3)

        val correctAnswer = question.answer

        // Tô màu đáp án
        colorAnswer(holder.binding.rbOptionA, 0, correctAnswer, userAnswer)
        colorAnswer(holder.binding.rbOptionB, 1, correctAnswer, userAnswer)
        colorAnswer(holder.binding.rbOptionC, 2, correctAnswer, userAnswer)
        colorAnswer(holder.binding.rbOptionD, 3, correctAnswer, userAnswer)

        // Hiển thị giải thích nếu có
        if (!question.explanation.isNullOrEmpty()) {
            holder.binding.tvExplanation.text = "Giải thích: ${question.explanation}"
            holder.binding.tvExplanation.visibility = View.VISIBLE
        } else {
            holder.binding.tvExplanation.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = questions.size

    private fun setupOption(radioButton: RadioButton, label: String, option: String?) {
        if (option.isNullOrEmpty()) {
            radioButton.visibility = View.GONE
        } else {
            radioButton.text = "$label. $option"
            radioButton.visibility = View.VISIBLE
        }
    }

    private fun colorAnswer(radioButton: RadioButton, optionIndex: Int, correctAnswer: Int, userAnswer: Int) {
        when {
            optionIndex == correctAnswer -> {
                radioButton.setTextColor(Color.parseColor("#388E3C")) // màu xanh đúng
            }
            optionIndex == userAnswer -> {
                radioButton.setTextColor(Color.parseColor("#D32F2F")) // màu đỏ sai
            }
            else -> {
                radioButton.setTextColor(Color.BLACK) // bình thường
            }
        }
    }
}
