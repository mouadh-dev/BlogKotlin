package com.example.Blog.Dao

import android.app.Activity
import android.util.Log
import com.example.Blog.Entity.UserItem
import com.example.Blog.Util.BaseConstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class UserDao: GestionUser {
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference(BaseConstant.instance().userRef)
    private val mAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseDatabase.getInstance().getReference("users")
    private val publicationRef = FirebaseDatabase.getInstance().getReference("Post")

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
       }