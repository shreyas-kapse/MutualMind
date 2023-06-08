package com.example.mutualmind

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mutualmind.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception

class Login : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        // loading xml data to variables
        val loginbtn = binding.logLogBtn
        val forgetpassbtn = binding.logForgetpassTxt
        val registerbtn = binding.logJoinnowTxt

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        loginbtn.setOnClickListener {
            coroutineScope.launch {
                loginUserWithFirebase()
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

    private suspend fun loginUserWithFirebase() {
        val emailedt = binding.logEmailEdt.text.toString()
        val passedt = binding.logPassEdt.text.toString()
        if (emailedt.isNotEmpty() && passedt.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailedt, passedt)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (firebaseAuth.currentUser?.isEmailVerified == true) {
                            val intent = Intent(this@Login, Home::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@Login,
                                "Verify your email first.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(
                                this@Login,
                                "Invalid credentials check email and password",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: FirebaseAuthInvalidUserException) {
                            Toast.makeText(
                                this@Login,
                                "No account found",
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(
                                this@Login,
                                "This email is already linked to another account",
                                Toast.LENGTH_SHORT
                            ).show()

                        } catch (e: FirebaseNetworkException) {
                            Toast.makeText(this@Login, "Network error", Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Login,
                                task.exception.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
        } else {
            Toast.makeText(this@Login, "Empty fields are not allowed", Toast.LENGTH_SHORT)
                .show()
        }
    }
}