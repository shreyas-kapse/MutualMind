package com.example.mutualmind

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mutualmind.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var alert: CustomAlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        alert = CustomAlertDialog()
        //loading xml values to variable
        val registerbtn = binding.regiRegiBtn
        val loginnowbtn = binding.regiLoginNowTxt

        val coroutineScope = CoroutineScope(Dispatchers.Main)
        var loadingDialog: Dialog? = null
        registerbtn.setOnClickListener {

                loadingDialog = alert.showLoadingDialog(this@Register)
                registerUserWithFirebase(loadingDialog)
        }
        //go to login page
        loginnowbtn.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private  fun registerUserWithFirebase(loadingDialog: Dialog?) {
        val email_edt = binding.regiEmailEdtxt.text.toString()
        val pass_edt = binding.regiPassEdtxt.text.toString()
        val confirmpass_edt = binding.regiConfirmPassEdtxt.text.toString()

        //            firebase register code for authentication (registration)
        if (email_edt.isNotEmpty() && pass_edt.isNotEmpty() && confirmpass_edt.isNotEmpty()) {
            if (pass_edt == confirmpass_edt) {
                firebaseAuth.createUserWithEmailAndPassword(email_edt, pass_edt)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                                if (loadingDialog != null) {
                                    alert.dismissLoadingDialog(loadingDialog)
                                }
                            firebaseAuth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener {
                                    binding.regiEmailEdtxt.text = null
                                    binding.regiPassEdtxt.text = null
                                    binding.regiConfirmPassEdtxt.text = null
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this,
                                        "Check your mail for verification.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            try {
                                throw it.exception!!
                            } catch (it: FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this,
                                    "This email is already in register.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (it: FirebaseAuthWeakPasswordException) {

//                                    if (loadingDialog != null && loadingDialog.isShowing) {
//                                        alert.dismissLoadingDialog(loadingDialog)
//                                    }

                                Toast.makeText(
                                    this, "Password is too weak.", Toast.LENGTH_SHORT
                                ).show()
                            } catch (it: Exception) {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }finally {

                                    if (loadingDialog != null && loadingDialog.isShowing) {
                                        alert.dismissLoadingDialog(loadingDialog)
                                    }
                            }

                        }
                    }
            } else {
                Toast.makeText(this, "Password do not match.", Toast.LENGTH_SHORT).show()
                runOnUiThread {
                    if (loadingDialog != null) {
                        alert.dismissLoadingDialog(loadingDialog)
                    }
                }
            }
        } else {
            Toast.makeText(this, "Empty field are not allowed.", Toast.LENGTH_SHORT).show()
            runOnUiThread {
                if (loadingDialog != null) {
                    alert.dismissLoadingDialog(loadingDialog)
                }
            }
        }
    }
}
