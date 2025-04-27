package com.example.drivingtestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class SetInfo(val setName: String, val questionCount: Int)

class SetAdapter(
    private val sets: List<SetInfo>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SetAdapter.SetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
        return SetViewHolder(view)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        val setInfo = sets[position]
        holder.bind(setInfo)
    }

    override fun getItemCount(): Int = sets.size

    inner class SetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSetName: TextView = itemView.findViewById(R.id.tvSetName)
        private val tvQuestionCount: TextView = itemView.findViewById(R.id.tvQuestionCount)

        fun bind(setInfo: SetInfo) {
            // Ánh xạ setName thành tên hiển thị thân thiện (set1 -> Bộ đề 1)
            val displayName = when (setInfo.setName) {
                "set1" -> "Bộ đề 1"
                "set2" -> "Bộ đề 2"
                "set3" -> "Bộ đề 3"
                else -> "Bộ đề ${setInfo.setName.removePrefix("set")}"
            }
            tvSetName.text = displayName
            tvQuestionCount.text = "Số câu hỏi: ${setInfo.questionCount}"

            itemView.setOnClickListener {
                onItemClick(setInfo.setName) // Truyền setName gốc (set1, set2,...)
            }
        }
    }
}