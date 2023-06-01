package com.example.mutualmind

import VolleySingleTon
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
            addDataToDatabase(
                nav, amount, units.toString(), fundCode.toString(),
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
        val myref =
            FirebaseDatabase.getInstance().getReference(firebaseAuth.currentUser?.uid.toString())
        myref.child(fundName).setValue(FirebaseDataModel(nav, amount, units, fundName, fundCode))
            .addOnCompleteListener {
                binding.funNavEdt.text = null
                binding.funAmountEdt.text = null
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
            calculateReturns(priceData)
            val nav = priceData.get(0).price
            binding.funFundName.text = schemeName
            binding.funCategory.text = category
            binding.funFundHouse.text = fundHouse
            binding.funNav.text = nav
//            Log.d("REST RES", " $priceData")
        }) { error ->
            Log.d("REST RES", "error $error")

        }
        VolleySingleTon.getInstance(applicationContext).addToRequestQueue(jsonObject)
    }

    private fun calculateReturns(priceData: ArrayList<PriceData>) {
        val oneWeek = ArrayList<PriceData>()
        val oneMonth = ArrayList<PriceData>()
        val threeMonths = ArrayList<PriceData>()
        val sixMonths = ArrayList<PriceData>()
        val oneYear = ArrayList<PriceData>()
        for (i in 0 until 7) {
            oneWeek.add(priceData.get(i));
        }
        for (i in 0 until 30) {
            oneMonth.add(priceData.get(i))
        }
        for (i in 0 until 90) {
            threeMonths.add(priceData.get(i))
        }
        for (i in 0 until 180) {
            sixMonths.add(priceData.get(i))
        }
        for (i in 0 until 365) {
            oneYear.add(priceData.get(i))
        }
        val oneDayBtn=findViewById<Button>(R.id.oneDay)
        val oneWeekBtn = findViewById<Button>(R.id.oneWeek)
        val oneMonthBtn = findViewById<Button>(R.id.oneMonth)
        val threeMonthsBtn = findViewById<Button>(R.id.threeMonths)
        val sixMonthsBtn = findViewById<Button>(R.id.sixMonths)
        val oneYearBtn = findViewById<Button>(R.id.oneYear)
        setTrendColor(oneWeek.get(1).price.toDouble()>oneWeek.get(0).price.toDouble(),oneDayBtn)
        setTrendColor(
            oneWeek.get(oneWeek.size - 1).price.toDouble() > oneWeek.get(0).price.toDouble(),
            oneWeekBtn
        )
        setTrendColor(
            oneMonth.get(oneMonth.size - 1).price.toDouble() > oneMonth.get(0).price.toDouble(),
            oneMonthBtn
        )
        setTrendColor(
            threeMonths.get(threeMonths.size - 1).price.toDouble() > threeMonths.get(0).price.toDouble(),
            threeMonthsBtn
        )
        setTrendColor(
            sixMonths.get(sixMonths.size - 1).price.toDouble() > sixMonths.get(0).price.toDouble(),
            sixMonthsBtn
        )
        setTrendColor(
            oneYear.get(oneYear.size - 1).price.toDouble() > oneYear.get(0).price.toDouble(),
            oneYearBtn
        )
//        if (oneWeek.get(oneWeek.size - 1).price.toDouble() > oneWeek.get(0).price.toDouble()) {
//            b1.setBackgroundColor(Color.RED)
//
//        } else {
//            b1.setBackgroundColor(Color.parseColor("#18BC28"))
//        }
    }

    private fun setTrendColor(b: Boolean, b1: Button) {
        if (b) {
            //negative
            b1.setBackgroundColor(Color.RED)
        } else {
            //positive
            b1.setBackgroundColor(Color.parseColor("#18BC28"))
        }
    }
}