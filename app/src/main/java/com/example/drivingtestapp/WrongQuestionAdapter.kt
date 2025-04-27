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
        holder.rbA.text = "A. ${question.optionA}"
        holder.rbB.text = "B. ${question.optionB}"
        holder.rbC.text = "C. ${question.optionC}"
        holder.rbD.text = "D. ${question.optionD}"

        // Clear old selection
        holder.rgOptions.clearCheck()

        // Select the answer user selected
        when (question.userSelected) {
            0 -> holder.rbA.isChecked = true
            1 -> holder.rbB.isChecked = true
            2 -> holder.rbC.isChecked = true
            3 -> holder.rbD.isChecked = true
        }

        // Highlight correct answer
        when (question.correctAnswer) {
            0 -> holder.rbA.setTextColor(Color.GREEN)
            1 -> holder.rbB.setTextColor(Color.GREEN)
            2 -> holder.rbC.setTextColor(Color.GREEN)
            3 -> holder.rbD.setTextColor(Color.GREEN)
        }
    }

    override fun getItemCount(): Int = questions.size
}
