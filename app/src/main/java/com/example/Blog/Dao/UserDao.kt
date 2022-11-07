package com.example.Blog.Dao

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.Blog.Entity.PostItem
import com.example.Blog.Entity.UserItem
import com.example.Blog.Util.BaseConstant
import com.example.stagepfe.Dao.PostCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class UserDao : GestionUser {
    private var database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference(BaseConstant.instance().userRef)
    private val mAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseDatabase.getInstance().getReference("users")
    private val postRef = FirebaseDatabase.getInstance().getReference("Post")
    private val storageReference = FirebaseStorage.getInstance().reference

    ///////////////////////////////////////insert user////////////////////////////////
    override fun insertUser(userItem: UserItem) {
        userItem.id = myRef.push().key.toString()
        myRef.child(userItem.id!!).setValue(userItem)
    }

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().uid.toString()
    }

    ////////////////////////////////////sign up////////////////////////////////////////////:
    fun signUpUser(
        activity: AppCompatActivity,
        userItem: UserItem,
        signUpCallback: SignUpCallback
    ) {
        mAuth.createUserWithEmailAndPassword(userItem.mail, userItem.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Auth activity", "createUserWithEmail:success")
                    userItem.id = mAuth.currentUser!!.uid
                    myRef.child(userItem.id!!).setValue(userItem)

                    signUpCallback.success()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(
                        "Auth activity",
                        "createUserWithEmail:failure",
                        task.exception
                    )
                    signUpCallback.failure(task.exception.toString().substring(65))

                }

                // ...
            }
    }

    //////////////////////////////////upload picture//////////////////////////////////////
    fun uploadImageToFirebase(uid: String, contentUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val image = storageReference.child("pictures/$fileName")
        image.putFile(contentUri).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri ->
                Log.d("tag", "onSuccess: Uploaded Image URl is $uri")
                userRef.child(uid).child("profilePhoto").setValue(uri.toString())

            }.addOnFailureListener {
                Log.d("tag", "onFailureMessage is $it")
            }
        }
    }
    fun uploadImageToFirebaseFromCamera(uid: String, imageEncoded: ByteArray) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val image = storageReference.child("pictures/$fileName")
        image.putBytes(imageEncoded).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri ->
                Log.d("tag", "onSuccess: Uploaded Image URl is $uri")
                userRef.child(uid).child("profilePhoto").setValue(uri.toString())

            }.addOnFailureListener {
                Log.d("tag", "onFailureMessage is $it")
            }
        }
    }
    ///////////////////////////////////////////Sign in//////////////////////////////////////////////////
    fun signIn(activity: AppCompatActivity, userItem: UserItem, userCallback: UserCallback) {
        mAuth.signInWithEmailAndPassword(userItem.mail, userItem.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    val registeredId = user!!.uid
                    getUserByUid(registeredId, userCallback)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)

                    userCallback.failure(task.exception.toString().substring(59))
                }
            }

    }

    ///////////////////////////////////////////////get user by id///////////////////////////////////////
    fun getUserByUid(uid: String, responseCallback: UserCallback) {
        val jLoginDatabase = database.reference.child("users").child(uid)
        jLoginDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userItem = dataSnapshot.getValue(UserItem::class.java)
                if (userItem != null) {
                    responseCallback.onSuccess(userItem as UserItem)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "signInWithEmail:failure", error.toException())
                responseCallback.failure(error.toException().toString())
            }

        })
    }

    ///////////////////////////////////////////Post send and get////////////////////////////////////
    fun sendPost(post: PostItem,  postCallback: PostCallback) {

        post.id = postRef.child(post.idUser!!).push().key.toString()
        postRef.child(post.idUser!!).child(post.id!!).setValue(post)
postCallback.pictureFound(post)
    }

    /////////////////////////////////////////////updateUser/////////////////////////////////////////
    fun updateUser(id: String, userItem: UserItem, userCallback: UserCallback) {

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userRef.child(id).removeValue()
                userRef.child(id).setValue(userItem)
                userCallback.onSuccess(userItem)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /////////////////////////////////////////////get Post/////////////////////////////////////////
    fun getPost(postCallback: PostCallback) {
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listPosts = ArrayList<PostItem>()
                if (snapshot.exists()) {
                    snapshot.children.forEach { ds ->
                        ds!!.children.forEach { post ->
                            val postItems = post.getValue(PostItem::class.java)
                            listPosts.add(postItems!!)
                        }
                    }
                    postCallback.successPost(listPosts)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                postCallback.failurePost(error)
            }
        })
    }
    //////////////////////////////////////////sign out methode////////////////////////////////////////
    fun signOut() {
        mAuth.signOut()
    }
    ///////////
     fun uploadImagePostToFirebase(idUser: String,postId:String, contentUri: Uri)  {

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val image = storageReference.child("posts/$fileName")
        image.putFile(contentUri).addOnSuccessListener {
            image.downloadUrl.addOnSuccessListener { uri ->
                Log.d("tag", "onSuccess: Uploaded Image URl is $uri")

                    postRef.child(idUser).child(postId).child("picturePost").setValue(uri.toString())




            }.addOnFailureListener {
                Log.d("tag", "onFailureMessage is $it")
            }
        }
    }

}