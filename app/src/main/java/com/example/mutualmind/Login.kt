package com.example.mutualmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mutualmind.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth=FirebaseAuth.getInstance()
        // loading xml data to variables
        val loginbtn=binding.logLogBtn
        val forgetpassbtn=binding.logForgetpassTxt
        val registerbtn=binding.logJoinnowTxt


        loginbtn.setOnClickListener {
            val emailedt=binding.logEmailEdt.text.toString()
            val passedt=binding.logPassEdt.text.toString()
            if(emailedt.isNotEmpty()&&passedt.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(emailedt,passedt).addOnCompleteListener {
                    task->
                    if(task.isSuccessful){
                        if(firebaseAuth.currentUser?.isEmailVerified==true){
//                            val intent=Intent(this,)
                        }
                    }
                }
            }
        }
        forgetpassbtn.setOnClickListener {
//          firebase code
        }
        registerbtn.setOnClickListener {
            var intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}