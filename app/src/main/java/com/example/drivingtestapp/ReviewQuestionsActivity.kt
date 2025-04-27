package com.example.drivingtestapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.drivingtestapp.databinding.ActivityReviewQuestionsBinding
import kotlinx.coroutines.launch
import retrofit2.Response

class ReviewQuestionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewQuestionsBinding
    private val sets = mutableListOf<String>()
    private val TAG = "ReviewQuestionsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSets()
    }

    private fun loadSets() {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Fetching all questions to get sets")
                val response: Response<List<Question>> = RetrofitClient.apiService.getAllQuestions()

                if (response.isSuccessful) {
                    val allQuestions = response.body() ?: emptyList()
                    Log.d(TAG, "Total questions fetched: ${allQuestions.size}")

                    // Lấy danh sách set_name duy nhất
                    sets.addAll(allQuestions.mapNotNull { it.setName }.distinct().sorted())
                    Log.d(TAG, "Found ${sets.size} sets: $sets")

                    if (sets.isNotEmpty()) {
                        // Thêm các nút vào GridLayout
                        sets.forEachIndexed { index, setName ->
                            val button = Button(this@ReviewQuestionsActivity).apply {
                                // Đổi tên hiển thị: set1 -> Đề Số 1
                                val displayName = when (setName) {
                                    "set1" -> "Đề Số 1"
                                    "set2" -> "Đề Số 2"
                                    "set3" -> "Đề Số 3"
                                    "set4" -> "Đề Số 4"
                                    "set5" -> "Đề Số 5"
                                    "set6" -> "Đề Số 6"
                                    "set7" -> "Đề Số 7"
                                    "set8" -> "Đề Số 8"
                                    else -> "Đề Số ${setName.removePrefix("set")}"
                                }
                                text = displayName
                                textSize = 16f
                                setTextColor(resources.getColor(android.R.color.white))
                                setBackgroundResource(R.drawable.rounded_button)
                                setPadding(16)
                                elevation = 4f // Thêm bóng nhẹ cho nút
                                layoutParams = GridLayout.LayoutParams().apply {
                                    width = 0
                                    height = GridLayout.LayoutParams.WRAP_CONTENT
                                    columnSpec = GridLayout.spec(index % 4, 1f)
                                    rowSpec = GridLayout.spec(index / 4)
                                    setMargins(12) // Tăng khoảng cách giữa các nút
                                }
                                setOnClickListener {
                                    Log.d(TAG, "Selected set: $setName")
                                    val intent = Intent(this@ReviewQuestionsActivity, TestBySetActivity::class.java)
                                    intent.putExtra("SET_NAME", setName)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            binding.gridSets.addView(button)
                        }
                    } else {
                        Toast.makeText(this@ReviewQuestionsActivity, "Không tìm thấy bộ đề nào", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Log.e(TAG, "API Error: ${response.code()}")
                    Toast.makeText(this@ReviewQuestionsActivity, "Lỗi tải bộ đề: ${response.code()}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}", e)
                Toast.makeText(this@ReviewQuestionsActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}