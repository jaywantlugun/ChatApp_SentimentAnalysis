package com.example.chatapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ItemAdapter(val context: RecommendationActivity,val itemList: ArrayList<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val recommended_item_name: TextView = itemView.findViewById(R.id.recommended_item_name)
        val recommended_item_image: ImageView = itemView.findViewById(R.id.recommended_item_image)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recommened_item,parent,false)
        return ItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val ItemData = itemList[position]
        val name:String = ItemData.movie_name

        //load image
        val imageURL = ItemData.movie_imageURL
        Picasso.get().load(imageURL).into(holder.recommended_item_image,object : Callback {
            override fun onSuccess() {
                //image loaded successfully
                holder.recommended_item_name.text = name
            }

            override fun onError(e: Exception?) {

                Toast.makeText(context,e?.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}