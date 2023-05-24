package com.example.mutualmind.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.mutualmind.Adapter.FundInfoAdapter
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.R
import com.example.mutualmind.databinding.FragmentAddFundBinding
import org.json.JSONArray

//class AddFund : Fragment() {
//    lateinit var binding: FragmentAddFundBinding
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view=inflater.inflate(R.layout.fragment_add_fund, container, false)
//        fetchFunds()
//        val recyclerView = view.findViewById<RecyclerView>(R.id.frag_add_fund_recycler)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//
//        return view
//    }
//
//    private fun fetchFunds() {
//        val data = ArrayList<BasicFundInfo>()
//        val url = "https://api.mfapi.in/mf"
//        val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {
//            val jsonArray = JSONArray(it.toString())
//            Log.d("res ", "$jsonArray")
//            for (i in 0 until jsonArray.length()) {
//                val jsonObject = jsonArray.getJSONObject(i)
//                val schemename = jsonObject.getString("schemeName")
//                val schemecode = jsonObject.getString("schemeCode")
//
//                val fund = BasicFundInfo(schemename, schemecode)
//                data.add(fund)
//                binding.fragAddFundRecycler.adapter=FundInfoAdapter(data)
//            }
//        }, { error ->
//            Log.d("res", "$error")
//        })
//        VolleySingleTon.getInstance(requireActivity().applicationContext).addToRequestQueue(jsonObject)
//    }
//
//
//}

class AddFund : Fragment() {
    private lateinit var binding: FragmentAddFundBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FundInfoAdapter
    private val data = ArrayList<BasicFundInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFundBinding.inflate(inflater, container, false)
        recyclerView = binding.fragAddFundRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FundInfoAdapter(data)
        recyclerView.adapter = adapter

        fetchFunds()

        return binding.root
    }

    private fun fetchFunds() {
        val url = "https://api.mfapi.in/mf"
        val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {
            val jsonArray = JSONArray(it.toString())
            Log.d("res ", "$jsonArray")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val schemeName = jsonObject.getString("schemeName")
                val schemeCode = jsonObject.getString("schemeCode")

                val fund = BasicFundInfo(schemeName, schemeCode)
                data.add(fund)
            }
            adapter.notifyDataSetChanged()
        }, { error ->
            Log.d("res", "$error")
        })
        VolleySingleTon.getInstance(requireActivity().applicationContext).addToRequestQueue(jsonObject)
    }
}
