package com.example.drivingtestapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.drivingtestapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.progressBar.visibility = View.GONE

        binding.btnSendResetEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Định dạng Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendPasswordResetEmail(email)
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSendResetEmail.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                binding.btnSendResetEmail.isEnabled = true

                if (task.isSuccessful) {
                    Toast.makeText(this, "Đã gửi Email khôi phục mật khẩu! Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Đã xảy ra lỗi không xác định"
                    Toast.makeText(this, "Lỗi: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
