package com.example.chatapp

class Messages:java.io.Serializable {
    lateinit var message:String
    lateinit var uid:String
    lateinit var timeStamp:String
    lateinit var senderImage:String
    constructor(message:String,uid:String,timeStamp:String,senderImage:String){
        this.message=message
        this.uid=uid
        this.timeStamp=timeStamp
        this.senderImage=senderImage
    }

    constructor()
}