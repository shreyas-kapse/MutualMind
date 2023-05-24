package com.example.mutualmind.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.R

class FundInfoAdapter(val list: ArrayList<BasicFundInfo>) :
    RecyclerView.Adapter<SingleFundViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleFundViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.fund_single_item, parent, false)
        val viewHolder = SingleFundViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size

    }



    override fun onBindViewHolder(holder: SingleFundViewHolder, position: Int) {
        holder.fundcode.setText(list[position].schomecode)
        holder.fundname.setText(list[position].schemename)
    }
}

class SingleFundViewHolder(itemView: View) : ViewHolder(itemView) {
    var fundname = itemView.findViewById<TextView>(R.id.rec_fundname)
    var fundcode = itemView.findViewById<TextView>(R.id.rec_fundcode)
}

