package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HomeActivity : AppCompatActivity() {

    lateinit var userId:String

    lateinit var recyclerview:RecyclerView

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    var contactList=ArrayList<User>()
    var messageList=ArrayList<Messages>()
    lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerview = findViewById(R.id.recyclerview)

        userId= intent.getStringExtra("userid").toString()

        retrieveContactsFromDatabase()
        recyclerview.layoutManager = LinearLayoutManager(this)
        contactAdapter = ContactAdapter(this,contactList)
        recyclerview.adapter = contactAdapter

        retrieveAllMessages()

    }

    private fun retrieveAllMessages() {
        var chatReference = databaseReference.child(userId).child("Chats")

        chatReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(data in snapshot.children){
                    for(chats in data.children){
                        val chatdata = chats.getValue(Messages::class.java)
                        if (chatdata != null) {
                            messageList.add(chatdata)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrieveContactsFromDatabase() {
        databaseReference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()

                for(eachContactData in snapshot.children)
                {
                    val data = eachContactData.child("UserInfo").getValue(User::class.java)
                    if(data!=null && data.uid!=userId)
                    {
                        contactList.add(data)
                    }
                }
                contactAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Unable to Fetch Data", Toast.LENGTH_LONG).show()
            }


        })
    }











    //display logout menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.home_menu,menu)
        return true
    }
    //function of logout menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.show_recommendations){

            val intent= Intent(this,RecommendationActivity::class.java)
            intent.putExtra("userid",userId)
            intent.putExtra("message_list",messageList)
            startActivity(intent)

        }

        if(item.itemId == R.id.log_out){

            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()

        }

        return true
    }
}