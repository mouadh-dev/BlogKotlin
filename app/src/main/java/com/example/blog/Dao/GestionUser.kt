package com.example.blog.Dao

import com.example.blog.Entity.UserItem

interface GestionUser {

    fun insertUser(userItem: UserItem)
}