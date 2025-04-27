package com.example.drivingtestapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionIndicatorAdapter(
    private val totalQuestions: Int,
    private var currentPosition: Int,
    private val answeredQuestions: MutableList<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<QuestionIndicatorAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvQuestionNumber)

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(position: Int) {
            textView.text = (position + 1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_indicator, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = totalQuestions

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
        val isCurrent = (position == currentPosition)
        val isAnswered = answeredQuestions.contains(position)

        when {
            isCurrent -> {
                holder.itemView.setBackgroundResource(R.drawable.bg_indicator_current)
                holder.textView.setTextColor(Color.WHITE)
            }
            isAnswered -> {
                holder.itemView.setBackgroundResource(R.drawable.bg_indicator_answered)
                holder.textView.setTextColor(Color.WHITE)
            }
            else -> {
                holder.itemView.setBackgroundResource(R.drawable.bg_indicator_normal)
                holder.textView.setTextColor(Color.BLACK)
            }
        }
    }

    fun updateCurrentQuestion(position: Int) {
        currentPosition = position
        notifyDataSetChanged()
    }

    fun addAnsweredQuestion(position: Int) {
        if (!answeredQuestions.contains(position)) {
            answeredQuestions.add(position)
            notifyItemChanged(position)
        }
    }
}
