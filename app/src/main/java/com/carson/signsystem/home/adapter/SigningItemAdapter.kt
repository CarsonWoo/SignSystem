package com.carson.signsystem.home.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.carson.signsystem.R
import com.carson.signsystem.home.model.SigningViewData

class SigningItemAdapter constructor(context: Context, mDataList: List<SigningViewData>): RecyclerView.Adapter<SigningItemAdapter.SigningViewHolder>() {

    private var mDataList: List<SigningViewData>? = null
    private var context: Context? = null

    init {
        this.context = context
        this.mDataList = mDataList
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SigningViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.signing_view_item, p0, false)
        Log.e("Adapter", ">>> viewgroup = $p0")
        return SigningViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList?.size?:0
    }

    override fun onBindViewHolder(holder: SigningViewHolder, pos: Int) {
        val data = mDataList!![pos]
        val timeStr = data.time_begin + data.time_end
        holder.time!!.text = timeStr
        val dateStr = data.date_begin + data.date_end
        holder.date!!.text = dateStr
        val placeStr = data.location
        holder.place!!.text = placeStr
    }

    class SigningViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var place: TextView? = null
        var date: TextView? = null
        var time: TextView? = null

        init {
            place = itemView.findViewById(R.id.signing_item_place_name)
            date = itemView.findViewById(R.id.signing_item_date_range)
            time = itemView.findViewById(R.id.signing_item_time_range)
        }
    }

}