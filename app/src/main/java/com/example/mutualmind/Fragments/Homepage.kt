package com.example.mutualmind.Fragments

import android.app.VoiceInteractor.Request
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonArrayRequest
import com.example.mutualmind.Adapter.TopGainAdapter
import com.example.mutualmind.Model.TopGainModel
import com.example.mutualmind.R

class Homepage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_homepage, container, false)
        val list=ArrayList<TopGainModel>()
        list.add(TopGainModel(true,"Aditya Birla Sun Life Income Fund - Growth - Regular Plan",0.10))
        list.add(TopGainModel(false,"shreyas Birla Sun Life Income Fund - Growth - Regular Plan",0.60))
        list.add(TopGainModel(true,"test Birla Sun Life Income Fund - Growth - Regular Plan",0.40))
        list.add(TopGainModel(true,"android Birla Sun Life Income Fund - Growth - Regular Plan",0.20))

        val listview=view.findViewById<ListView>(R.id.listview)
        listview.adapter=TopGainAdapter(requireActivity(),list)



        return view
    }


}