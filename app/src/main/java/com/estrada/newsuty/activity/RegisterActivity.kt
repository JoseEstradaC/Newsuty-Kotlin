package com.estrada.newsuty.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.estrada.newsuty.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth


        setContentView(binding.root)

        binding.RegisterBtnRegister.setOnClickListener {
            val email = binding.RegisterEmail.text.toString().trim()
            val password = binding.RegisterPassword.text.toString().trim()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                val intencion = Intent()
                if (it.isSuccessful) {
                    setResult(RESULT_OK, intencion)
                } else {
                    setResult(RESULT_CANCELED, intencion)
                }
                finish()
            }
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }
}