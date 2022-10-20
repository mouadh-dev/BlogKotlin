package com.example.Blog.Dao

interface SignUpCallback {
    fun success()
    fun failure(error:String)
}