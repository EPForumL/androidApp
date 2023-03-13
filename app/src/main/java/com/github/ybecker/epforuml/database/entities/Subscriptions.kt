package com.github.ybecker.epforuml.database.entities

data class Subscriptions (val uid : Int, val cid : Int){
    var isValid : Boolean = false
}