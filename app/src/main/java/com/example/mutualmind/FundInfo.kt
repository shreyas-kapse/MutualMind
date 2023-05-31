package com.example.mutualmind

import VolleySingleTon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mutualmind.Model.FundDetails
import com.example.mutualmind.Model.PriceData
import com.example.mutualmind.databinding.ActivityFundInfoBinding
import org.json.JSONArray
import org.json.JSONObject

class FundInfo : AppCompatActivity() {
    lateinit var binding: ActivityFundInfoBinding
    private var data = ArrayList<FundDetails>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFundInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val fundName = intent.getStringExtra("fundName")
        val fundCode = intent.getStringExtra("fundCode")

        binding.funFundName.text = fundName
        fetchFundDetails(fundCode)
    }

    private fun fetchFundDetails(fundCode: String?) {
        val url = "https://api.mfapi.in/mf/$fundCode"
        val jsonObject = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

            val details=it.getJSONObject("meta")
            val fundHouse=details.getString("fund_house")
            val category=details.getString("scheme_category")
            val schemeName=details.getString("scheme_name")
            val priceData=ArrayList<PriceData>()
            val jsonArray=it.getJSONArray("data")
            for(i in 0 until jsonArray.length()){
                val jsonObject=jsonArray.getJSONObject(i)
                val date=jsonObject.getString("date")
                val nav=jsonObject.getString("nav")
                val info=PriceData(date,nav)
                priceData.add(info)
            }
            val nav=priceData.get(0).price
            binding.funFundName.text=schemeName
            binding.funCategory.text=category
            binding.funFundHouse.text=fundHouse
            binding.funNav.text=nav
            Log.d("REST RES", " $priceData")
        }) {
            error->
            Log.d("REST RES", "error $error")

        }
        VolleySingleTon.getInstance(applicationContext).addToRequestQueue(jsonObject)
    }
}