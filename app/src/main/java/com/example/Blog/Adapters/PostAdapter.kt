package com.example.Blog.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.Blog.Dao.UserCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.PostItem
import com.example.Blog.Entity.UserItem
import com.example.Blog.R

class PostAdapter(var mCtx: Context, var resources: Int, var items: List<PostItem>) :
    ArrayAdapter<PostItem>(mCtx, resources, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {


        var layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        var view: View = layoutInflater.inflate(resources, null)
        initView(view, position)
        return view
    }

    private fun initView(view: View, position: Int) {
        val userDao = UserDao()
        var userPicture: ImageView = view.findViewById(R.id.userPicturePost)
        var ownerPost: TextView = view.findViewById(R.id.ownerPost)
        var contentPost: TextView = view.findViewById(R.id.contentPost)
        var picturePost: ImageView = view.findViewById(R.id.picturePost)
        var hourPostpatient: TextView = view.findViewById(R.id.timePost)
        var datePostpatient: TextView = view.findViewById(R.id.datePost)

        var mItem: PostItem = items[position]

        datePostpatient.text = mItem.datePost
        hourPostpatient.text = mItem.hourPPost!!.substring(0, 5)
        contentPost.text = mItem.contentPost

        Glide.with(mCtx)
            .load(mItem.picturePost)
            .into(picturePost)
        userDao.getUserByUid(mItem.idUser!!, object : UserCallback {
            override fun onSuccess(userItem: UserItem) {
                ownerPost.text = userItem.fullname
                Glide.with(mCtx)
                    .load(userItem.profilePhoto)
                    .into(userPicture)
            }

            override fun failure() {

            }

        })


    }
}