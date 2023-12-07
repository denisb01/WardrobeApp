package com.example.myapplication.database

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.myapplication.data.FirebaseImageModel
import com.example.myapplication.data.Prediction
import com.example.myapplication.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class FirebaseController(
    private val currentContext: Context?
) {
    private val database = FirebaseDatabase.getInstance().reference
    private val storage = Firebase.storage.reference

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

    private fun addImageToDatabase(user: FirebaseUser?, image: FirebaseImageModel)
    {
        if(user != null) {
            database.child("images").child(user.uid).child(image.name+"_"+image.label).setValue(image)
                .addOnSuccessListener {
                    Log.i("URL", "Success")
                }
                .addOnFailureListener{
                    Log.i("URL", "Failure")
                }
        }
    }

    fun addClothesImageToFirebase(user: FirebaseUser, image: Bitmap, prediction: Prediction) {
        if(user != null){
            val imageUri = getImageUri(image)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
            val uploadTask = storage.child("clothes/${user.uid}/${dateFormat.format(Date())}").putFile(imageUri)

            uploadTask.addOnSuccessListener {
                Toast.makeText(currentContext, "Success", Toast.LENGTH_LONG).show()
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    addImageToDatabase(user, FirebaseImageModel(dateFormat.format(Date()), uri.toString(), prediction.label, prediction.accuracy.toLong()))
                }.addOnFailureListener{
                    Log.i("URL", "Fail")
                }
            }.addOnFailureListener {
                Toast.makeText(currentContext, "Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getImageUri(inImage: Bitmap): Uri {

        val tempFile = File.createTempFile("tempimg", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    fun getImages(user: FirebaseUser): Flow<List<FirebaseImageModel>> = callbackFlow{
        val userImagesReference = database.child("images").child(user.uid)
        val imagesList = mutableListOf<FirebaseImageModel>()

        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.e("FirebaseChiuitStore", "getAll:", p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val images = p0.children

                for(image in images) {
                    val firebaseImage = image.getValue(FirebaseImageModel::class.java)

                    if(firebaseImage != null) {
                        imagesList.add(firebaseImage)
                    }
                }

                trySend(imagesList)
            }

        }
        userImagesReference.addValueEventListener(listener)

        awaitClose { userImagesReference.removeEventListener(listener) }
    }

//    fun getImages(user: FirebaseUser){
//        database.child("images").child(user.uid).get().addOnSuccessListener {
//            val images = it.children
//
//            for(image in images) {
//                Log.i("URL", "Image ${image.key.toString()} = ${image.getValue(String::class.java)}")
//            }
//        }.addOnFailureListener{
//            Log.e("URL", "Error getting data", it)
//        }
//    }

}