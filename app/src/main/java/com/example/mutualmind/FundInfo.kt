package com.example.mutualmind

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mutualmind.databinding.ActivityFundInfoBinding

class FundInfo : AppCompatActivity() {
    lateinit var binding:ActivityFundInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFundInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent=intent
        val fundName=intent.getStringExtra("fundName")
        val fundCode=intent.getStringExtra("fundCode")

        binding.funFundName.text=fundName

    }
}