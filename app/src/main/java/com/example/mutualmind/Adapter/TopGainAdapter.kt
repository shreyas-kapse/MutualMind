package com.example.mutualmind.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.mutualmind.Model.TopGain
import com.example.mutualmind.R
import org.w3c.dom.Text

class TopGainAdapter(private val context:Context,val list:ArrayList<TopGain>):ArrayAdapter<TopGain>(context,
    R.layout.home_single_item,list){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view=LayoutInflater.from(context).inflate(R.layout.home_single_item,null)
        val viewC:View=view.findViewById(R.id.flag)
        val name:TextView=view.findViewById(R.id.fundname)
        val returns:TextView=view.findViewById(R.id.returns)

        var color: String ="#FF0000"
        if(list[position].flag==true)
            color="#FF0000"
        else
            color="#00FF29"
        viewC.setBackgroundColor(parseColor(color))
        name.text=list[position].name
        returns.text=list[position].returns.toString()
        return view
    }
}