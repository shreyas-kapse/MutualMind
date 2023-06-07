package com.example.mutualmind.Fragments

import VolleySingleTon
import android.content.Context
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

import com.android.volley.Request.Method
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mutualmind.Adapter.TopGainAdapter
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureNanoTime

class Homepage : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homepage, container, false)
        val list = ArrayList<TopGainModel>()
        list.add(
            TopGainModel(
                true,
                "Aditya Birla Sun Life Income Fund - Growth - Regular Plan",
                0.10
            )
        )
        list.add(
            TopGainModel(
                false,
                "shreyas Birla Sun Life Income Fund - Growth - Regular Plan",
                0.60
            )
        )
        list.add(
            TopGainModel(
                true,
                "test Birla Sun Life Income Fund - Growth - Regular Plan",
                0.40
            )
        )
        list.add(
            TopGainModel(
                true,
                "android Birla Sun Life Income Fund - Growth - Regular Plan",
                0.20
            )
        )
        firebaseAuth = FirebaseAuth.getInstance()
        val listview = view.findViewById<ListView>(R.id.listview)
        listview.adapter = TopGainAdapter(requireActivity(), list)
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            val data = uiUpdate()
//            Log.d("firebase data ", "data from firebase i got this$data")

        }
        return view
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

    private suspend fun getDataFromDb(): ArrayList<FirebaseDataModel> = withContext(Dispatchers.IO){
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

            database.addValueEventListener(getData)
        }

    }


    private suspend fun updateValues(data: ArrayList<FirebaseDataModel>) {
        var totalInvestment: Double = 0.0
        var returns: Double = 0.0

        for (i in data) {
            totalInvestment += i.investmentAmount.toDouble()
            val currentNav = findCurrentPrice(i.fundCode, requireContext())
            val units = i.units.toDouble()
            returns += currentNav * units
            returns -= i.investmentAmount.toDouble()
            Log.d("RES", "Total Returns: $returns")

            val returnsTxt = view?.findViewById<TextView>(R.id.hom_total_returns)
            if (returnsTxt != null) {
                returnsTxt.text = returns.toString()
            }
            val totalInvestmentTxt = view?.findViewById<TextView>(R.id.hom_total_investment)
            if (totalInvestmentTxt != null) {
                totalInvestmentTxt.text = totalInvestment.toString() + "0"
            }

//        Log.d("RES", "Total investment:${returns.toString()} ")
        }
    }

    private suspend fun findCurrentPrice(fundCode: String, context: Context): Double = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->
            var currentNav: Double = 0.0

            val url = "https://api.mfapi.in/mf/$fundCode"
            val jsonObject =
                JsonObjectRequest(
                    com.android.volley.Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        val priceData = ArrayList<PriceData>()
                        val jsonArray = it.getJSONArray("data")
                        for (i in 0 until 1) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val date = jsonObject.getString("date")
                            val nav = jsonObject.getString("nav")
                            val info = PriceData(date, nav)
                            priceData.add(info)
                        }
                        currentNav = priceData.get(0).price.toDouble()

                        Log.d("REST RES", " $currentNav")
                        continuation.resume(currentNav)
                    }) { error ->
                    continuation.resumeWithException(error)

                }
            VolleySingleTon.getInstance(context)
                .addToRequestQueue(jsonObject)

//        Log.d("REST RES", " $currentNav")

        }

    }
}





