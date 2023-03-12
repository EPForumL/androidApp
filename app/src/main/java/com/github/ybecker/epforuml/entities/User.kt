package com.github.ybecker.epforuml.entities

data class User (val uid : Int, val name:String){
    var passwd   : String = ""
    var username : String = ""
}