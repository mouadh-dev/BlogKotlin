package com.example.stagepfe.Dao

import com.example.Blog.Entity.PostItem
import com.google.firebase.database.DatabaseError
import java.lang.Error


interface PostCallback {
    fun successPost(listPosts: ArrayList<PostItem>)
    fun failurePost(error: DatabaseError)
    fun pictureFound(postItem:PostItem)
}