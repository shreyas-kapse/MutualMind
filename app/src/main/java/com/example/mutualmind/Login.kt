package com.example.mutualmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mutualmind.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var login_btn=binding.logLogBtn
        var forget_pass_btn=binding.logForgetpassTxt
        var register_btn=binding.logJoinnowTxt

        login_btn.setOnClickListener {
//            firebase code
        }
        forget_pass_btn.setOnClickListener {
//          firebase code
        }
        register_btn.setOnClickListener {
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}