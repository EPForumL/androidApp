package com.github.ybecker.epforuml.database.entities

import com.github.ybecker.epforuml.database.Model

data class User(val userId: String, val username: String, val name:String, var password: String){
    constructor() : this("", "","","")
}