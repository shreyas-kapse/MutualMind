package com.example.mutualmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import com.example.mutualmind.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        var register_btn=binding.registerBtn
        var login_btn=binding.loginBtn

        register_btn.setOnClickListener {
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        login_btn.setOnClickListener {
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
        val user=firebaseAuth.currentUser
        if(user!=null){
            val intent=Intent(this,Home::class.java)
            startActivity(intent)
        }
    }
}