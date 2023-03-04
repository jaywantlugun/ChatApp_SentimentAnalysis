package com.example.chatapp

class User {
    lateinit var name:String
    var phone:Int?=null
    lateinit var email:String
    lateinit var imageURL:String
    lateinit var uid:String
    constructor(name:String,phone: Int,email: String,imageURL:String,uid:String){
        this.name = name
        this.phone = phone
        this.email = email
        this.imageURL=imageURL
        this.uid=uid
    }

    constructor()
}