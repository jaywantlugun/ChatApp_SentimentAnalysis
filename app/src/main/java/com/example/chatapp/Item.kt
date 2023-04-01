package com.example.chatapp

class Item {
    lateinit var movie_name:String
    lateinit var movie_imageURL:String
    constructor(movie_name:String,movie_imageURL:String){
        this.movie_name = movie_name
        this.movie_imageURL = movie_imageURL

    }

    constructor()
}