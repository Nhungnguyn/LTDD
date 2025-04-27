package com.example.drivingtestapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivingtestapp.databinding.ActivityTrafficSignsBinding
import kotlinx.coroutines.launch
import retrofit2.Response

class TrafficSignsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTrafficSignsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrafficSignsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTrafficSigns.layoutManager = LinearLayoutManager(this)
        loadTrafficSigns()
    }

    private fun loadTrafficSigns() {
        // Launching a coroutine to call the suspend function
        lifecycleScope.launch {
            try {
                // Call the suspend function to get traffic signs
                val response: Response<List<TrafficSign>> = RetrofitClient.apiService.getTrafficSigns()

                if (response.isSuccessful) {
                    val signs = response.body() ?: emptyList()
                    if (signs.isEmpty()) {
                        Toast.makeText(this@TrafficSignsActivity, "Không có biển báo giao thông", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.rvTrafficSigns.adapter = TrafficSignAdapter(signs)
                    }
                } else {
                    Toast.makeText(this@TrafficSignsActivity, "Lỗi tải biển báo: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TrafficSignsActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
