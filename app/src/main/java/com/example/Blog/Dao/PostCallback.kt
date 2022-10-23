package com.example.stagepfe.Dao

import com.example.Blog.Entity.PostItem



interface PostCallback {
    fun successPost(postItem: PostItem)
    fun failurePost()
}