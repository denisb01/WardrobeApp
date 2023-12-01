package com.example.myapplication.database

import android.content.Context
import com.example.myapplication.data.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class FirebaseController(
    private val currentContext: Context?
) {
    private val database = FirebaseDatabase.getInstance().reference

    private fun checkIfExists(uid: String) : Boolean
    {
        var exists = false

        database.child("users").child(uid).get().addOnSuccessListener {
            exists = it != null
        }.addOnFailureListener{
            exists = false
        }

        return exists
    }

    fun addUserData(user: FirebaseUser?, fullName: String? = null) {
        if(user != null){

            val newUser: User = if(fullName != null){
                User(fullName)
            } else{
                User(user.displayName)
            }

            if(!checkIfExists(user.uid)) {
                database.child("users").child(user.uid).setValue(newUser)
            }
        }
    }

}