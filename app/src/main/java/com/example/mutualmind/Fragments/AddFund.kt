package com.example.mutualmind.fragments

import VolleySingleTon
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.example.mutualmind.Adapter.FundInfoAdapter
import com.example.mutualmind.FundInfo
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.SelectInterface
import com.example.mutualmind.databinding.FragmentAddFundBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AddFund : Fragment(), SelectInterface {
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


        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            fetchFunds()
        }
        tempdata.addAll(data)
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempdata.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    data.forEach {
                        if (it.schemename.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempdata.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                } else {
                    tempdata.clear()
                    tempdata.addAll(data)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

        })
        adapter = FundInfoAdapter(tempdata, this)
        recyclerView.adapter = adapter
        return binding.root
    }

    //    private suspend fun fetchFunds() {
//        binding.progressbar.visibility=View.VISIBLE
//        val url = "https://api.mfapi.in/mf"
////        val jsonArray= suspendCoroutine<JSONArray> { continuation ->
//            val jsonObject = JsonArrayRequest(Request.Method.GET, url, null, Response.Listener {
//                val jsonArray = JSONArray(it.toString())
//                binding.progressbar.visibility = View.GONE
//                Log.d("res ", "$jsonArray")
//                for (i in 0 until jsonArray.length()) {
//                    val jsonObject = jsonArray.getJSONObject(i)
//                    val schemeName = jsonObject.getString("schemeName")
//                    val schemeCode = jsonObject.getString("schemeCode")
//
//                    val fund = BasicFundInfo(schemeName, schemeCode)
//                    data.add(fund)
//                }
//                tempdata.addAll(data)
//                recyclerView.adapter!!.notifyDataSetChanged()
//            }) { error ->
//                Log.d("res", "$error")
//            }
//            VolleySingleTon.getInstance(requireActivity().applicationContext)
//                .addToRequestQueue(jsonObject)
//        }
//    }
    private suspend fun fetchFunds() {
        withContext(Dispatchers.IO) {
            binding.progressbar.visibility = View.VISIBLE
            val url = "https://api.mfapi.in/mf"
            val jsonArray = suspendCoroutine<JSONArray> { continuation ->
                val jsonObject = JsonArrayRequest(Request.Method.GET, url, null,
                    { response ->
                        val jsonArray = JSONArray(response.toString())
                        binding.progressbar.visibility = View.GONE
                        Log.d("res ", "$jsonArray")
                        continuation.resume(jsonArray)
                    },
                    { error ->
                        binding.progressbar.visibility = View.GONE
                        Log.d("res", "$error")
                        continuation.resumeWithException(error)
                    }
                )
                VolleySingleTon.getInstance(requireActivity().applicationContext)
                    .addToRequestQueue(jsonObject)
            }

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val schemeName = jsonObject.getString("schemeName")
                val schemeCode = jsonObject.getString("schemeCode")

                val fund = BasicFundInfo(schemeName, schemeCode)
                data.add(fund)
            }

            withContext(Dispatchers.Main) {
                tempdata.addAll(data)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }


    override fun itemClicked(item: BasicFundInfo) {
        val intent = Intent(context, FundInfo::class.java)
        intent.putExtra("fundName", item.schemename)
        intent.putExtra("fundCode", item.schemecode)
        startActivity(intent)
    }
}
