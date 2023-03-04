package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class SignupActivity : AppCompatActivity() {

    lateinit var signup_image: ImageView
    lateinit var edt_signup_name: EditText
    lateinit var edt_signup_number: EditText
    lateinit var edt_signup_email: EditText
    lateinit var edt_signup_password: EditText
    lateinit var btn_signup: Button
    lateinit var btn_already_have_account: Button

    var image_uploaded:Int = 0

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val databaseReference: DatabaseReference = firebaseDatabase.reference.child("UsersList")

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference.child("UsersImages")

    //for imageView
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri?=null
    lateinit var imageURL:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        if(supportActionBar!=null){
            supportActionBar!!.hide()
        }

        //initializing layout variables
        signup_image = findViewById(R.id.signup_image)
        edt_signup_name = findViewById(R.id.edt_signup_name)
        edt_signup_number = findViewById(R.id.edt_signup_number)
        edt_signup_email = findViewById(R.id.edt_signup_email)
        edt_signup_password = findViewById(R.id.edt_signup_password)
        btn_signup = findViewById(R.id.btn_signup)
        btn_already_have_account = findViewById(R.id.btn_already_have_account)

        //register Activity for Result (otherwise it will not work )
        registerActivityForResult()

        btn_already_have_account.setOnClickListener {

            val loginIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginIntent)

        }

        btn_signup.setOnClickListener {

            if(edt_signup_name.text.toString()=="" || edt_signup_number.text.toString()=="" || edt_signup_email.text.toString()=="" || edt_signup_password.text.toString()=="" ||
                !edt_signup_number.text.isDigitsOnly()){
                Toast.makeText(this,"Empty Field", Toast.LENGTH_LONG).show()
            }
            else if(image_uploaded==0){
                Toast.makeText(this,"Choose Image", Toast.LENGTH_LONG).show()
            }
            else {
                upload_image()
            }

        }

        //Selecting image from gallery
        signup_image.setOnClickListener {
            chooseImage()
        }

    }

    //function to select image from gallery
    private fun chooseImage(){

            //permission is already granted
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)

    }

    //If user is requested permission for the first time
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==123 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //permission is  granted for the 1st time
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }

    }

    //register activity for result
    private fun registerActivityForResult(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result->

                val resultCode = result.resultCode
                val imageData = result.data

                if(resultCode== RESULT_OK && imageData!=null){
                    imageUri = imageData.data

                    //using picasso to load image in imageview
                    imageUri?.let {
                        Picasso.get().load(it).into(signup_image)
                        image_uploaded=1
                    }

//                    Another way to load image in ImageView
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                    signup_image.setImageBitmap(bitmap)
                }


            })
    }





    private fun signup(mProgressDialog: ProgressDialog){

        val signup_name:String = edt_signup_name.text.toString()
        val signup_number:Int = edt_signup_number.text.toString().toInt()
        val signup_email:String = edt_signup_email.text.toString()
        val signup_password:String = edt_signup_password.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(signup_email,signup_password).addOnCompleteListener(this) { task->

            if(task.isSuccessful){
                val user = firebaseAuth.currentUser
                val userId:String=user!!.uid

                val userdata = User(signup_name,signup_number,signup_email,imageURL,userId)

                databaseReference.child(userId).child("UserInfo").setValue(userdata).addOnCompleteListener { it->

                    if(it.isSuccessful){

                        if(mProgressDialog.isShowing){
                            mProgressDialog.dismiss()
                        }

                        Toast.makeText(applicationContext,"Account Created Successfully", Toast.LENGTH_LONG).show()

                        val homeIntent = Intent(this,HomeActivity::class.java)
                        homeIntent.putExtra("userid",userId)
                        startActivity(homeIntent)
                        finish()
                    }

                }

            }else{

                if(mProgressDialog.isShowing){
                    mProgressDialog.dismiss()
                }

                Toast.makeText(applicationContext,"Error Creating user", Toast.LENGTH_LONG).show()
            }

        }


    }

    private fun upload_image(){

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("This is TITLE")
        mProgressDialog.show()


        val imageName = UUID.randomUUID().toString()
        val profileImageReference = storageReference.child("ProfileImages").child(imageName)

        imageUri?.let { uri->

            profileImageReference.putFile(uri).addOnSuccessListener {

                //downloadable url
                profileImageReference.downloadUrl.addOnSuccessListener {url->

                    imageURL=url.toString()
                    signup(mProgressDialog)
                }


            }.addOnFailureListener{

                if(mProgressDialog.isShowing){
                    mProgressDialog.dismiss()
                }

                Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_LONG).show()
            }.addOnProgressListener { taskSnapshot->
                var percent:Float = (100*taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount).toFloat()
                mProgressDialog.setMessage("Uploaded : $percent%")

            }

        }


    }



}