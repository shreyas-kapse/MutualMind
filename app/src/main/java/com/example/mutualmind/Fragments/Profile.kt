package com.example.mutualmind.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.mutualmind.Login
import com.example.mutualmind.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class Profile : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseAuth=FirebaseAuth.getInstance()
        val logout=view.findViewById<LinearLayout>(R.id.logout)
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        logout.setOnClickListener {
//            Toast.makeText(context, "This is logout btn", Toast.LENGTH_SHORT).show()
            coroutineScope.launch { signout()}
        }
        return view
    }

    private suspend fun signout(){
        val coroutineScope= CoroutineScope(Dispatchers.IO)
        coroutineScope.launch { val signout=async {  firebaseAuth.signOut()}
        signout.await()
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Logout Successful", Toast.LENGTH_SHORT).show()
                val intent=Intent(context,Login::class.java)
                startActivity(intent)
            }
        }

    }

}