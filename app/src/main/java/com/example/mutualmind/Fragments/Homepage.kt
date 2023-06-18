package com.example.mutualmind.fragments

import VolleySingleTon
import android.content.Context
import android.graphics.Color
import kotlinx.coroutines.launch
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.android.volley.Request

import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mutualmind.Adapter.TopGainAdapter
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.Model.FirebaseDataModel
import com.example.mutualmind.Model.PriceData
import com.example.mutualmind.Model.TopGainModel
import com.example.mutualmind.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.w3c.dom.Text
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Homepage : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    private var GainList = ArrayList<TopGainModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)

        GainList.add(
            TopGainModel(
                true,
                "Aditya Birla Sun Life Income Fund - Growth - Regular Plan",
                "10201",
                0.9,
                0.10,
                0.1
            )
        )
        GainList.add(
            TopGainModel(
                false,
                "shreyas Birla Sun Life Income Fund - Growth - Regular Plan",
                "121242",
                0.10,
                0.10,
                0.1
            )
        )
        firebaseAuth = FirebaseAuth.getInstance()
        val GainAdapter = TopGainAdapter(requireActivity(), GainList)
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        coroutineScope.launch {
            fetchFunds(requireContext())

            val listview = view.findViewById<ListView>(R.id.listview)
//            list.clear()
//            list.addAll(fundDataList)
//            adapter.notifyDataSetChanged()
            listview.adapter = GainAdapter
            uiUpdate()

        }


        return view
    }


    //    private fun fetchFunds(context: Context) {
//        val basicFundData = ArrayList<BasicFundInfo>()
//        val url = "https://api.mfapi.in/mf"
//        val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, { response ->
//            val jsonArray = JSONArray(response.toString())
//            for (i in 0 until jsonArray.length()) {
//                val jsonObj = jsonArray.getJSONObject(i)
//                val schemeName = jsonObj.getString("schemeName")
//                val schemeCode = jsonObj.getString("schemeCode")
//                val fund = BasicFundInfo(schemeName, schemeCode)
//                basicFundData.add(fund)
//            }
//            val fundDataList = ArrayList<TopGainModel>()
//            val deferred = GlobalScope.async(Dispatchers.IO) {
//                for (i in 0 until fundDataList.size) {
//                    val (currentRs, preNav) =
//                        findCurrentPrice(basicFundData[i].schemecode, context)
//                    val fundData = TopGainModel(
//                        true,
//                        basicFundData[i].schemename,
//                        basicFundData[i].schemecode,
//                        currentRs,
//                        preNav,
//                        0.0
//                    )
//                    fundDataList.add(fundData)
//                }
//                fundDataList
//            }
//            GlobalScope.launch(Dispatchers.Main) {
//                val result = deferred.await()
//                topGainnerNLossers(result)
//            }
//        }, { error ->
//            Log.d("Fetch Funds res", "fetchFunds: $error")
//        })
//        VolleySingleTon.getInstance(context)
//            .addToRequestQueue(jsonObject)
//
//    }
    private suspend fun fetchFunds(context: Context) = coroutineScope {
        val basicFundData = ArrayList<BasicFundInfo>()
        val fundDataList = ArrayList<TopGainModel>()

        val url = "https://api.mfapi.in/mf"
        val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            val jsonArray = JSONArray(response.toString())
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                val schemeName = jsonObj.getString("schemeName")
                val schemeCode = jsonObj.getString("schemeCode")
                val fund = BasicFundInfo(schemeName, schemeCode)
                basicFundData.add(fund)
            }

            GlobalScope.launch(Dispatchers.IO) {
                for (i in 0 until 10) {
                    val (currentRs, preNav) = withContext(Dispatchers.IO) {
                        findCurrentPrice(basicFundData[i].schemecode, context)
                    }
                    val fundData = TopGainModel(
                        true,
                        basicFundData[i].schemename,
                        basicFundData[i].schemecode,
                        currentRs,
                        preNav,
                        0.0
                    )
                    fundDataList.add(fundData)
                }
                withContext(Dispatchers.Main) {
                    topGainnerNLossers(fundDataList)
                }
            }
        }, { error ->
            Log.d("Fetch Funds res", "fetchFunds: $error")
        })

        VolleySingleTon.getInstance(context).addToRequestQueue(jsonObject)
    }

    private fun topGainnerNLossers(fundDataList: ArrayList<TopGainModel>) {
        Log.d("Fetch Funds res", "fetchFunds: $fundDataList")
        val numGainList = 5
        val numLossList = 5
        val topGainersList =
            fundDataList.sortedByDescending { it.currentNav - it.prevNav }.take(numGainList)
        val topLosserList = fundDataList.sortedBy { it.currentNav - it.prevNav }.take(numLossList)
        topGainersList.forEach {
            it.flag = false
            Log.d(
                "Fetch Funds res",
                "gainers :prev nav is  ${it.prevNav} and current nav is ${it.currentNav}"
            )

        }
        topLosserList.forEach {
            it.flag = true
            Log.d(
                "Fetch Funds res",
                "loosers :prev nav is  ${it.prevNav} and current nav is ${it.currentNav}"
            )
        }
        activity?.runOnUiThread {
            val topGainTxt = view?.findViewById<TextView>(R.id.hom_top_gainer)
            val topLossTxt = view?.findViewById<TextView>(R.id.hom_top_losser)

            topGainTxt?.setBackgroundColor(Color.parseColor("#ffc107"))
            topLossTxt?.setBackgroundColor(Color.TRANSPARENT)

            GainList.clear()
            GainList.addAll(topGainersList)
            val adapter = TopGainAdapter(requireActivity(), GainList)
            val listView = view?.findViewById<ListView>(R.id.listview)
            listView?.adapter = adapter
            adapter.notifyDataSetChanged()
            topGainTxt?.setOnClickListener {
                topGainTxt.setBackgroundColor(Color.parseColor("#ffc107"))
                topLossTxt?.setBackgroundColor(Color.TRANSPARENT)

                GainList.clear()
                GainList.addAll(topGainersList)
                adapter.notifyDataSetChanged()
            }
            topLossTxt?.setOnClickListener {
                topLossTxt.setBackgroundColor(Color.parseColor("#ffc107"))
                topGainTxt?.setBackgroundColor(Color.TRANSPARENT)

                GainList.clear()
                GainList.addAll(topLosserList)
                adapter.notifyDataSetChanged()
            }
        }


        Log.d("Fetch Funds res", "gainers : $topGainersList")

        Log.d("Fetch Funds res", "losers: $topLosserList")

    }


    private suspend fun uiUpdate(): ArrayList<FirebaseDataModel>? {
        var data: ArrayList<FirebaseDataModel>? = null
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val job = coroutineScope.launch {
            val d: Deferred<ArrayList<FirebaseDataModel>> =
                async(Dispatchers.IO) { getDataFromDb() }
            data = d.await()
        }
        job.join()
        updateValues(data!!)
        return data

    }

    private suspend fun getDataFromDb(): ArrayList<FirebaseDataModel> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCoroutine { continuation ->
                val database = FirebaseDatabase.getInstance()
                    .getReference(firebaseAuth.currentUser?.uid.toString())

                val data = ArrayList<FirebaseDataModel>()

                val getData = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            val fundName = i.child("fundName").value
                            val fundCode = i.child("fundCode").value
                            val fundPrice = i.child("fundPrice").value
                            val investmentAmount = i.child("investmentAmount").value
                            val units = i.child("units").value

                            val d = FirebaseDataModel(
                                fundPrice.toString(),
                                investmentAmount.toString(),
                                units.toString(),
                                fundName.toString(),
                                fundCode.toString()
                            )
                            data.add(d)
                        }

                        continuation.resume(data)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWithException(error.toException())
                    }
                }

                database.addListenerForSingleValueEvent(getData)
            }

        }


    private suspend fun updateValues(data: ArrayList<FirebaseDataModel>) {
        var totalInvestment = 0.0
        var returns = 0.0

        for (i in data) {
            totalInvestment += i.investmentAmount.toDouble()
            val currentNav = findCurrentPrice(i.fundCode, requireContext()).first
            val units = i.units.toDouble()
            returns += currentNav * units
            returns -= i.investmentAmount.toDouble()
            Log.d("RES", "Total Returns: $returns")

            val returnsTxt = view?.findViewById<TextView>(R.id.hom_total_returns)
//            if (returnsTxt != null) {
//                if (returns + totalInvestment > totalInvestment) {
//                    returnsTxt.post {
//                        returnsTxt.text = returns.toString()
//                        returnsTxt.setTextColor(Color.parseColor("#39FF14"))
//                    }
//                } else {
//                    returnsTxt.post {
//
//
//                        returnsTxt.text = returns.toString()
//                        returnsTxt.setTextColor(Color.parseColor("#FF0000"))
//                    }
//                }
//            }
//            val totalInvestmentTxt = view?.findViewById<TextView>(R.id.hom_total_investment)
//            if (totalInvestmentTxt != null) {
//                totalInvestmentTxt.text = totalInvestment.toString() + "0"
//            }
            if (returnsTxt != null) {
                val formatedValues = String.format("%.2f", returns)
                if (returns + totalInvestment > totalInvestment) {
                    activity?.runOnUiThread {
                        returnsTxt.text = formatedValues
                        returnsTxt.setTextColor(Color.parseColor("#39FF14"))
                    }
                } else {
                    activity?.runOnUiThread {
                        returnsTxt.text = formatedValues
                        returnsTxt.setTextColor(Color.parseColor("#FF0000"))
                    }
                }
            }

            val totalInvestmentTxt = view?.findViewById<TextView>(R.id.hom_total_investment)
            if (totalInvestmentTxt != null) {
                activity?.runOnUiThread {
                    totalInvestmentTxt.text = totalInvestment.toString() + "0"
                }
            }

//        Log.d("RES", "Total investment:${returns.toString()} ")
        }
    }

    private suspend fun findCurrentPrice(fundCode: String, context: Context): Pair<Double, Double> =
        withContext(Dispatchers.IO) {
            return@withContext suspendCoroutine { continuation ->
                var currentNav: Double
                var prevNav: Double
                val url = "https://api.mfapi.in/mf/$fundCode"
                val jsonObject =
                    JsonObjectRequest(
                        com.android.volley.Request.Method.GET,
                        url,
                        null,
                        {
                            val priceData = ArrayList<PriceData>()
                            val jsonArray = it.getJSONArray("data")
                            for (i in 0 until 2) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val date = jsonObject.getString("date")
                                val nav = jsonObject.getString("nav")
                                val info = PriceData(date, nav)
                                priceData.add(info)
                            }
                            currentNav = priceData[0].price.toDouble()
                            prevNav = priceData[1].price.toDouble()
//                            Log.d("REST RES", " $currentNav")
                            continuation.resume(Pair(currentNav, prevNav))
                        }) { error ->
                        continuation.resumeWithException(error)

                    }
                VolleySingleTon.getInstance(context)
                    .addToRequestQueue(jsonObject)

//        Log.d("REST RES", " $currentNav")

            }

        }
}







