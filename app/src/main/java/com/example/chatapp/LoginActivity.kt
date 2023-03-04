package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var edt_login_email: EditText
    lateinit var edt_login_password: EditText
    lateinit var btn_forgot_password: TextView
    lateinit var btn_login: Button
    lateinit var btn_create_account: Button

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //hiding the Action Bar
        if(supportActionBar!=null){
            supportActionBar!!.hide()
        }

        //initialising layout variables
        edt_login_email = findViewById(R.id.edt_login_email)
        edt_login_password = findViewById(R.id.edt_login_password)
        btn_forgot_password = findViewById(R.id.btn_forgot_password)
        btn_login = findViewById(R.id.btn_login)
        btn_create_account = findViewById(R.id.btn_create_account)

        //create account button
        btn_create_account.setOnClickListener {
            val signupIntent = Intent(this,SignupActivity::class.java)
            startActivity(signupIntent)
            //finish()
        }

        //Login Button
        btn_login.setOnClickListener {
            login()
        }

        //forgot passwaord ** Not implemented yet **
        btn_forgot_password.setOnClickListener {
            forgot_password()
        }




    }

    //login function checking in firebase
    private fun login(){

        val login_email:String = edt_login_email.text.toString()
        val login_password:String = edt_login_password.text.toString()

        firebaseAuth.signInWithEmailAndPassword(login_email,login_password).addOnCompleteListener(this) {task->


            if(task.isSuccessful){

                val user = firebaseAuth.currentUser
                val userId = user!!.uid

                Toast.makeText(applicationContext,"Login Successfull", Toast.LENGTH_LONG).show()

                val homeIntent= Intent(this,HomeActivity::class.java)
                homeIntent.putExtra("userid",userId)
                startActivity(homeIntent)
                finish()

            }
            else{
                Toast.makeText(applicationContext,""+task.exception!!.message, Toast.LENGTH_LONG).show()
            }


        }

    }

    private fun forgot_password(){
        Toast.makeText(applicationContext,"Not Implemented Yet", Toast.LENGTH_LONG).show()
    }



}