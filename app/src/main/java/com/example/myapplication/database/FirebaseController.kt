package com.example.myapplication.database

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.myapplication.data.FirebaseClothingItem
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

    private fun addImageToDatabase(user: FirebaseUser?, imageName: String,image: FirebaseImageModel)
    {
        if(user != null) {
            database.child("images").child(user.uid).child(imageName).setValue(image)
                .addOnSuccessListener {
                    Log.i("URL", "Success")
                }
                .addOnFailureListener{
                    Log.i("URL", "Failure")
                }
        }
    }

    fun addClothesImageToFirebase(user: FirebaseUser, imageData: FirebaseImageModel, image: Bitmap) {
        if(user != null){
            val imageUri = getImageUri(image)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss")
            val uploadTask = storage.child("clothes/${user.uid}/${dateFormat.format(Date())}").putFile(imageUri)

            uploadTask.addOnSuccessListener {
                Toast.makeText(currentContext, "Success", Toast.LENGTH_LONG).show()
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    imageData.uri = uri.toString()
                    addImageToDatabase(user, dateFormat.format(Date()), imageData)
                }.addOnFailureListener{
                    Toast.makeText(currentContext, "Failed to add image to storage!", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(currentContext, "Failed to add data to database!", Toast.LENGTH_LONG).show()
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

    fun getImages(user: FirebaseUser): Flow<List<FirebaseClothingItem>> = callbackFlow{
        val userImagesReference = database.child("images").child(user.uid)
        val imagesList = mutableListOf<FirebaseClothingItem>()

        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(currentContext, "Failed to retrieve data!", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val images = p0.children

                for(image in images) {
                    val firebaseImage = image.getValue(FirebaseImageModel::class.java)

                    if(firebaseImage != null) {
                        imagesList.add(FirebaseClothingItem(image?.key.toString(), firebaseImage))
                    }
                }

                trySend(imagesList)
            }

        }
        userImagesReference.addValueEventListener(listener)

        awaitClose { userImagesReference.removeEventListener(listener) }
    }

}