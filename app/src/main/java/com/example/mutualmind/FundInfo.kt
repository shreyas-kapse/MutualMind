package com.example.mutualmind

import VolleySingleTon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mutualmind.Model.FirebaseDataModel
import com.example.mutualmind.Model.FirebaseUserDataModel
import com.example.mutualmind.Model.FundDetails
import com.example.mutualmind.Model.PriceData
import com.example.mutualmind.databinding.ActivityFundInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FundInfo : AppCompatActivity() {
    lateinit var binding: ActivityFundInfoBinding
    private var data = ArrayList<FundDetails>()
    var fundCode: String? = null
    var fundName: String? = null
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        val intent = intent
        fundName = intent.getStringExtra("fundName")
        fundCode = intent.getStringExtra("fundCode")

        binding.funFundName.text = fundName
        fetchFundDetails(fundCode)

        binding.funAddToFolioBtn.setOnClickListener {
            val nav = binding.funNavEdt.text.toString()
            val amount = binding.funAmountEdt.text.toString()
            val NAV = nav.toLong()
            val investment = amount.toLong()
            val units = investment / NAV.toInt()
            addDataToDatabase(nav, amount, units.toString(), fundCode.toString(),
                fundName.toString()
            )

            Log.d("user", "onCreate:$units ")
        }

    }

    private fun addDataToDatabase(
        nav: String,
        amount: String,
        units: String,
        fundCode: String,
        fundName: String
    ) {
        val database = FirebaseDatabase.getInstance().getReference("Users")
        val email = firebaseAuth.currentUser?.email
        database.child(firebaseAuth.currentUser?.uid.toString()).setValue(
            FirebaseUserDataModel(
                email.toString(),
                firebaseAuth.currentUser?.uid.toString()
            )
        )
        val myref=FirebaseDatabase.getInstance().getReference(firebaseAuth.currentUser?.uid.toString())
        myref.child(fundName).setValue(FirebaseDataModel(nav, amount, units, fundName, fundCode)).addOnCompleteListener {
            binding.funNavEdt.text=null
            binding.funAmountEdt.text=null
        }

    }

    private fun fetchFundDetails(fundCode: String?) {
        val url = "https://api.mfapi.in/mf/$fundCode"
        val jsonObject = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

            val details = it.getJSONObject("meta")
            val fundHouse = details.getString("fund_house")
            val category = details.getString("scheme_category")
            val schemeName = details.getString("scheme_name")
            val priceData = ArrayList<PriceData>()
            val jsonArray = it.getJSONArray("data")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val date = jsonObject.getString("date")
                val nav = jsonObject.getString("nav")
                val info = PriceData(date, nav)
                priceData.add(info)
            }
            val nav = priceData.get(0).price
            binding.funFundName.text = schemeName
            binding.funCategory.text = category
            binding.funFundHouse.text = fundHouse
            binding.funNav.text = nav
            Log.d("REST RES", " $priceData")
        }) { error ->
            Log.d("REST RES", "error $error")

        }
        VolleySingleTon.getInstance(applicationContext).addToRequestQueue(jsonObject)
    }
}