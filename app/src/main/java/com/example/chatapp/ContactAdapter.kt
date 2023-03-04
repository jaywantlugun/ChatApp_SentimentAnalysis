package com.example.chatapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ContactAdapter(val context: HomeActivity,val contactList: ArrayList<User>): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val contact_name: TextView = itemView.findViewById(R.id.contact_name)
        val contact_image: ImageView = itemView.findViewById(R.id.contact_image)
        val contact_last_message: TextView = itemView.findViewById(R.id.contact_last_message)
        val contact_last_message_time: TextView = itemView.findViewById(R.id.contact_last_message_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact,parent,false)
        return ContactViewHolder(view)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        val ContactData = contactList[position]
        val name:String = ContactData.name
        val phone:Int = ContactData.phone.toString().toInt()
        val uid = ContactData.uid

        //load profile image
        val imageURL = ContactData.imageURL
        Picasso.get().load(imageURL).into(holder.contact_image,object : Callback {
            override fun onSuccess() {
                //image loaded successfully
                holder.contact_name.text = name
            }

            override fun onError(e: Exception?) {

                Toast.makeText(context,e?.localizedMessage, Toast.LENGTH_LONG).show()
            }

        })

        holder.itemView.setOnClickListener {

            val chatIntent = Intent(context,ChatActivity::class.java)
            chatIntent.putExtra("Receiver_userid",uid)
            chatIntent.putExtra("Receiver_name",name)
            chatIntent.putExtra(ContactData.imageURL,"Receiver_image")
            context.startActivity(chatIntent)

        }

    }

    override fun getItemCount(): Int {
        return contactList.size
    }


}







