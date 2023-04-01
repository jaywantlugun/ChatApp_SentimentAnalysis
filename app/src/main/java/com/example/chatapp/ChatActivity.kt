package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    lateinit var chat_person_image:ImageView
    lateinit var chat_person_name:TextView
    lateinit var chat_recyclerview:RecyclerView
    lateinit var chat_message:EditText
    lateinit var btn_send_chat_message:Button

    lateinit var Sender_uid:String
    lateinit var Receiver_uid:String
    lateinit var Sender_image:String

    var messageList=ArrayList<Messages>()
    lateinit var messageAdapter: MessageAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chat_person_image = findViewById(R.id.chat_person_image)
        chat_person_name = findViewById(R.id.chat_person_name)
        chat_recyclerview = findViewById(R.id.chat_recyclerview)
        chat_message = findViewById(R.id.chat_message)
        btn_send_chat_message = findViewById(R.id.btn_send_chat_message)

        val Receiver_name=intent.getStringExtra("Receiver_name")
        val Receiver_image=intent.getStringExtra("Receiver_image")
        Receiver_uid=intent.getStringExtra("Receiver_userid").toString()
        Sender_uid = firebaseAuth.uid.toString()
        val img_ref= databaseReference.child(Sender_uid).child("UserInfo").child("imageURL")
        img_ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Sender_image = snapshot.value.toString()
                Picasso.get().load(Receiver_image).into(chat_person_image,object :Callback{
                    override fun onSuccess() {

                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(this@ChatActivity,e!!.message,Toast.LENGTH_LONG).show()
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        chat_person_name.setText(Receiver_name)


        btn_send_chat_message.setOnClickListener {
            if(chat_message.text.toString()==""){

                Toast.makeText(applicationContext,"Type Message",Toast.LENGTH_LONG).show()
            }
            else {
                val message:String = chat_message.text.toString()
                chat_message.setText("")
                addChatToDatabase(message)
            }
        }


        //Adapter Part
        retrieveMessagesFromDatabase()
        chat_recyclerview.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this,messageList)
        chat_recyclerview.adapter = messageAdapter



    }

    //retrieving messages
    private fun retrieveMessagesFromDatabase() {
        databaseReference.child(Sender_uid).child("Chats").child(Sender_uid+Receiver_uid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for(eachContactData in snapshot.children)
                {
                    val data = eachContactData.getValue(Messages::class.java)
                    if(data!=null)
                    {
                        messageList.add(data)
                    }
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Unable to Fetch Data", Toast.LENGTH_LONG).show()
            }


        })
    }


    //Adding chats in database
    private fun addChatToDatabase(message:String){
         val formatter = SimpleDateFormat("yyyy-MM-dd")
         val date = Date()
         val current = formatter.format(date).toString()

        val sender_ref = databaseReference.child(Sender_uid).child("Chats").child(Sender_uid+Receiver_uid)
        val receiver_ref = databaseReference.child(Receiver_uid).child("Chats").child(Receiver_uid+Sender_uid)

        val messageData = Messages(message,Sender_uid,current,Sender_image)

        sender_ref.push().setValue(messageData)
        receiver_ref.push().setValue(messageData)

    }



    //Top menu
    //display logout menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.chat_menu,menu)
        return true
    }
    //function of logout menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.show_sentiment_result){

            val i = Intent(this@ChatActivity, SentimentActivity::class.java)
            i.putExtra("message_list",messageList)
            i.putExtra("sender_uid",Sender_uid)
            i.putExtra("receiver_uid",Receiver_uid)
            startActivity(i)


        }

        return true
    }


}