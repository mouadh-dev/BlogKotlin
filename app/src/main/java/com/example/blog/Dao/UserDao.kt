package com.example.blog.Dao

import com.example.blog.Entity.UserItem

class UserDao: GestionUser {
    private val database = FirebaseDatabase.getInstance()
    override fun insertUser(userItem: UserItem) {
        TODO("Not yet implemented")
    }
}