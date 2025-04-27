package com.example.drivingtestapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WrongQuestionAdapter(private val questions: List<WrongQuestion>) :
    RecyclerView.Adapter<WrongQuestionAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestionItem)
        val rgOptions: RadioGroup = itemView.findViewById(R.id.rgOptionsItem)
        val rbA: RadioButton = itemView.findViewById(R.id.rbOptionAItem)
        val rbB: RadioButton = itemView.findViewById(R.id.rbOptionBItem)
        val rbC: RadioButton = itemView.findViewById(R.id.rbOptionCItem)
        val rbD: RadioButton = itemView.findViewById(R.id.rbOptionDItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wrong_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.tvQuestion.text = question.question

        // Reset màu về mặc định trước
        resetOptions(holder)

        // Setup đáp án
        setupOption(holder.rbA, question.optionA, "A")
        setupOption(holder.rbB, question.optionB, "B")
        setupOption(holder.rbC, question.optionC, "C")
        setupOption(holder.rbD, question.optionD, "D")

        holder.rgOptions.clearCheck()

        // Check đáp án người dùng đã chọn
        when (question.userSelected) {
            0 -> holder.rbA.isChecked = true
            1 -> holder.rbB.isChecked = true
            2 -> holder.rbC.isChecked = true
            3 -> holder.rbD.isChecked = true
        }

        // Highlight đáp án
        when (question.correctAnswer) {
            0 -> holder.rbA.setTextColor(Color.parseColor("#388E3C")) // Xanh
            1 -> holder.rbB.setTextColor(Color.parseColor("#388E3C"))
            2 -> holder.rbC.setTextColor(Color.parseColor("#388E3C"))
            3 -> holder.rbD.setTextColor(Color.parseColor("#388E3C"))
        }

        if (question.userSelected != -1 && question.userSelected != question.correctAnswer) {
            when (question.userSelected) {
                0 -> holder.rbA.setTextColor(Color.parseColor("#D32F2F")) // Đỏ
                1 -> holder.rbB.setTextColor(Color.parseColor("#D32F2F"))
                2 -> holder.rbC.setTextColor(Color.parseColor("#D32F2F"))
                3 -> holder.rbD.setTextColor(Color.parseColor("#D32F2F"))
            }
        }
    }

    override fun getItemCount(): Int = questions.size

    private fun setupOption(radioButton: RadioButton, text: String?, prefix: String) {
        if (!text.isNullOrEmpty()) {
            radioButton.visibility = View.VISIBLE
            radioButton.text = "$prefix. $text"
        } else {
            radioButton.visibility = View.GONE
        }
    }

    private fun resetOptions(holder: QuestionViewHolder) {
        val defaultColor = Color.BLACK
        holder.rbA.visibility = View.VISIBLE
        holder.rbB.visibility = View.VISIBLE
        holder.rbC.visibility = View.VISIBLE
        holder.rbD.visibility = View.VISIBLE

        holder.rbA.setTextColor(defaultColor)
        holder.rbB.setTextColor(defaultColor)
        holder.rbC.setTextColor(defaultColor)
        holder.rbD.setTextColor(defaultColor)
    }
}
