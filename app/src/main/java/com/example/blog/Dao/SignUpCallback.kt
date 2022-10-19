package com.example.blog.Dao

interface SignUpCallback {
    fun success()
    fun failure(error:String)
}