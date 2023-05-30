package com.example.mutualmind.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mutualmind.Model.BasicFundInfo
import com.example.mutualmind.R
import com.example.mutualmind.SelectInterface

//
//class FundInfoAdapter(private val list: ArrayList<BasicFundInfo>, private val listener:OnItemClickListener) :
//    RecyclerView.Adapter<SingleFundViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleFundViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.fund_single_item, parent, false)
//        val viewHolder = SingleFundViewHolder(view)
//        view.setOnClickListener {
//            listener.onItemClick(list.get(viewHolder.adapterPosition),view.context)
//        }
//        return viewHolder
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//
//    }
//
//
//
//    override fun onBindViewHolder(holder: SingleFundViewHolder, position: Int) {
//        holder.fundcode.setText(list[position].schomecode)
//        holder.fundname.setText(list[position].schemename)
//
//    }
//}
//
//class SingleFundViewHolder(itemView: View) : ViewHolder(itemView) {
//    var fundname = itemView.findViewById<TextView>(R.id.rec_fundname)
//    var fundcode = itemView.findViewById<TextView>(R.id.rec_fundcode)
//
//}
//
// FundInfoAdapter.kt
// FundInfoAdapter.kt


class FundInfoAdapter(
    private val list: ArrayList<BasicFundInfo>,
    private val listener: SelectInterface
) : RecyclerView.Adapter<FundInfoAdapter.SingleFundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleFundViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.fund_single_item,
            parent,
            false
        )
        return SingleFundViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SingleFundViewHolder, position: Int) {
        val fundInfo = list[position]
        holder.bind(fundInfo, listener)
    }

    inner class SingleFundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fundName: TextView = itemView.findViewById(R.id.rec_fundname)
        private val fundCode: TextView = itemView.findViewById(R.id.rec_fundcode)

        fun bind(fundInfo: BasicFundInfo, listener: SelectInterface) {
            fundName.text = fundInfo.schemename
            fundCode.text = fundInfo.schomecode

            itemView.setOnClickListener {
                listener.itemClicked(fundInfo)
            }
        }
    }
}

