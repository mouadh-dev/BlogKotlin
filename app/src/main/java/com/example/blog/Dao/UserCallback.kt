package com.example.blog.Dao

import com.example.blog.Entity.UserItem

interface UserCallback {
    fun onSuccess(userItem: UserItem)
    fun failure()
}