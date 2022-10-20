package com.example.Blog.Dao

import android.app.Activity
import android.net.Uri
import android.util.Log
import com.example.Blog.Entity.UserItem
import com.example.Blog.Util.BaseConstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class UserDao: GestionUser {
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference(BaseConstant.instance().userRef)
    private val mAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseDatabase.getInstance().getReference("users")
    private val publicationRef = FirebaseDatabase.getInstance().getReference("Post")
    private val storageReference = FirebaseStorage.getInstance().reference

///////////////////////////////////////insert user////////////////////////////////
    override fun insertUser(userItem: UserItem) {
        userItem.id = myRef.push().key.toString()
        myRef.child(userItem.id!!).setValue(userItem)
    }

   ////////////////////////////////////sign up////////////////////////////////////////////:
   fun signUpUser(activity: Activity, userItem: UserItem, signUpCallback: SignUpCallback) {
       mAuth.createUserWithEmailAndPassword(userItem.mail,userItem.password)
           .addOnCompleteListener(activity) { task ->
                       if (task.isSuccessful) {
                           // Sign in success, update UI with the signed-in user's information
                           Log.d("Auth activity", "createUserWithEmail:success")
                           userItem.id = mAuth.currentUser!!.uid
                           myRef.child(userItem.id!!).setValue(userItem)
                           signUpCallback.success()

                       }
                       else {
                           // If sign in fails, display a message to the user.
                           Log.w(
                               "Auth activity",
                               "createUserWithEmail:failure",
                               task.exception
                           )
                           signUpCallback.failure(task.exception.toString())

                       }

                       // ...
                   }
           }
    //////////////////////////////////upload picture//////////////////////////////////////
    fun uploadImageToFirebase(contentUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val image = storageReference.child("pictures/$fileName")
        image.putFile(contentUri).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri ->
                Log.d("tag", "onSuccess: Uploaded Image URl is $uri")
            }.addOnFailureListener {
                Log.d("tag", "onFailureMessage is $it")
            }
        }
    }

       }