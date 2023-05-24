package com.example.mutualmind.Fragments

import VolleySingleTon
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.mutualmind.Adapter.FundInfoAdapter
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.R
import com.example.mutualmind.databinding.FragmentAddFundBinding
import org.json.JSONArray
import java.util.Locale

class AddFund : Fragment() {
    private lateinit var binding: FragmentAddFundBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FundInfoAdapter
    private var data = ArrayList<BasicFundInfo>()
    private var tempdata = ArrayList<BasicFundInfo>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFundBinding.inflate(inflater, container, false)
        recyclerView = binding.fragAddFundRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)


        fetchFunds()
        tempdata.addAll(data)
        binding.searchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempdata.clear()
                val searchText=newText!!.lowercase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    data.forEach {
                        if(it.schemename.lowercase(Locale.getDefault()).contains(searchText)){
                            tempdata.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                else{
                    tempdata.clear()
                    tempdata.addAll(data)
                   recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })
        adapter = FundInfoAdapter(tempdata)
        recyclerView.adapter = adapter
        return binding.root
    }

    private fun fetchFunds() {
        binding.progressbar.visibility=View.VISIBLE
        val url = "https://api.mfapi.in/mf"
        val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {
            val jsonArray = JSONArray(it.toString())
            binding.progressbar.visibility=View.GONE
            Log.d("res ", "$jsonArray")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val schemeName = jsonObject.getString("schemeName")
                val schemeCode = jsonObject.getString("schemeCode")

                val fund = BasicFundInfo(schemeName, schemeCode)
                data.add(fund)
            }
            tempdata.addAll(data)
            recyclerView.adapter!!.notifyDataSetChanged()
        }, { error ->
            Log.d("res", "$error")
        })
        VolleySingleTon.getInstance(requireActivity().applicationContext)
            .addToRequestQueue(jsonObject)
    }
}
