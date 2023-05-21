package com.example.mutualmind

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.mutualmind.Adapter.TopGainAdapter
import com.example.mutualmind.Model.TopGain
import com.example.mutualmind.databinding.ActivityHomeBinding
import com.example.mutualmind.databinding.FragmentHomepageBinding

class Homepage : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_homepage, container, false)
        val list=ArrayList<TopGain>()
        list.add(TopGain(true,"Aditya Birla Sun Life Income Fund - Growth - Regular Plan",0.10))
        list.add(TopGain(false,"shreyas Birla Sun Life Income Fund - Growth - Regular Plan",0.60))
        list.add(TopGain(true,"test Birla Sun Life Income Fund - Growth - Regular Plan",0.40))
        list.add(TopGain(true,"android Birla Sun Life Income Fund - Growth - Regular Plan",0.20))

        val listview=view.findViewById<ListView>(R.id.listview)
        listview.adapter=TopGainAdapter(requireActivity(),list)
        return view
    }
}