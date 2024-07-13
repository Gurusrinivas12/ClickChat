package com.example.clickchat.RecyclerViewReceiver

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clickchat.R

class ReceiverAdapter(
    private val usersList: List<ReceiverObject>,
    private val context: Context
) : RecyclerView.Adapter<ReceiverViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverViewHolder {
        val layoutView: View = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_receiver_item, parent, false)
        return ReceiverViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ReceiverViewHolder, position: Int) {
        holder.mEmail.text = usersList[position].email
        holder.mReceive.setOnClickListener {
            val receiveState: Boolean = !usersList[holder.layoutPosition].receive
            usersList[holder.layoutPosition].receive = receiveState
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}
