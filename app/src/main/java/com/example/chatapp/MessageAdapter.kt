package com.example.chatapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class MessageAdapter(val context: ChatActivity,val messageList: ArrayList<Messages>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    class SenderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val message_image:ImageView = itemView.findViewById(R.id.message_image)
        val message_text:TextView = itemView.findViewById(R.id.message_text)
        val message_time:TextView = itemView.findViewById(R.id.message_time)

    }

    class ReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val message_image:ImageView = itemView.findViewById(R.id.message_image)
        val message_text:TextView = itemView.findViewById(R.id.message_text)
        val message_time:TextView = itemView.findViewById(R.id.message_time)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType== ITEM_SEND){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.right_message,parent,false)
            return SenderViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.left_message,parent,false)
            return ReceiverViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        if(message.uid==FirebaseAuth.getInstance().uid){
            return ITEM_SEND
        }
        else return ITEM_RECEIVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        val message_text = message.message
        val message_time = message.timeStamp
        val message_image = message.senderImage
        if (holder.javaClass == SenderViewHolder::class.java) {
            val viewHolder = holder as SenderViewHolder
            viewHolder.message_text.setText(message_text)
            viewHolder.message_time.setText(message_time)
            Picasso.get().load(message_image).into(holder.message_image)
        }
        else{
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.message_text.setText(message_text)
            viewHolder.message_time.setText(message_time)
            Picasso.get().load(message_image).into(holder.message_image)
        }
    }

    companion object{
        val ITEM_SEND=1
        val ITEM_RECEIVE=2;
    }




}