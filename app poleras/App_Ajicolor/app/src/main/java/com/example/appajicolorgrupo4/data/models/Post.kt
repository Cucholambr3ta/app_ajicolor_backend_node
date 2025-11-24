package com.example.appajicolorgrupo4.data.models

data class Post(

    val userId: Int, // id del usuario que creo el post
    val id: Int, // id del post
    val title: String, // titulo del post
    val body: String // cuerpo del post
)