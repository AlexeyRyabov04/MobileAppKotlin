package com.example.mobileapp

class User(
    val email: String = "",
    val name: String = "",
    val nick: String = "",
    val phone: String = "",
    val surname: String = "",
    val favourites: MutableList<Int> = mutableListOf()
)