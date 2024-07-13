package com.example.clickchat.RecyclerViewReceiver


import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R


class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mEmail: TextView = itemView.findViewById<TextView>(R.id.email)
    var mReceive: CheckBox = itemView.findViewById<CheckBox>(R.id.receive)
}