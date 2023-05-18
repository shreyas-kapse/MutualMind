package com.example.mutualmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mutualmind.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var register_btn=binding.regiRegiBtn
        var login_now_btn=binding.regiLoginNowTxt

        register_btn.setOnClickListener {
//            firebase register code
        }
        login_now_btn.setOnClickListener {
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}