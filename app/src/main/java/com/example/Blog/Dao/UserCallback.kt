package com.example.Blog.Dao

import com.example.Blog.Entity.UserItem

interface UserCallback {
    fun onSuccess(userItem: UserItem)
    fun failure()
}