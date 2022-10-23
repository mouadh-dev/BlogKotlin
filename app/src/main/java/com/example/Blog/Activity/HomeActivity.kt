package com.example.Blog.Activity

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.Blog.Adapters.PostAdapter
import com.example.Blog.Dao.UserCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.PostItem
import com.example.Blog.Entity.UserItem
import com.example.Blog.R
import com.example.Blog.databinding.ActivityHomeBinding
import com.example.stagepfe.Dao.PostCallback
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var uri: Uri? = null
    private var contentPost: EditText? = null
    private var adapterPost : PostAdapter? = null
    private var listViewPost: ListView? = null
    var listPostArray = ArrayList<PostItem>()
    var userDao = UserDao()
    private var post:PostItem? =  null
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDateTime: LocalDateTime = LocalDateTime.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root

        initView()
        post = PostItem()
        setContentView(view)
    }

    private fun initView() {
        contentPost = binding.contentPostEditText

        var listViewPost = binding.listPost
        initAdapter()

        adapterPost!!.clear()
        userDao.getPost(object : PostCallback {
            override fun successPost(postItem: PostItem) {

                listPostArray.add(postItem)
                adapterPost!!.notifyDataSetChanged()

            }

            override fun failurePost() {
            }
        })


        binding.imageButton.setOnClickListener {
            Log.println(Log.ASSERT, "selected", "showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        val userDao = UserDao()
        val uid = userDao.getCurrentUserId()






        userDao.getUserByUid(uid, object : UserCallback {
            override fun onSuccess(userItem: UserItem) {

                Log.println(Log.ASSERT, "picture", userItem.profilePhoto!!)
                Glide.with(this@HomeActivity).load(userItem.profilePhoto).into(binding.profilePhotoIV)

            }

            override fun failure() {
            }
        })

        binding.profilePhotoIV.setOnClickListener {
            val profile = ProfileActivity()
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }
    }

    private fun initAdapter() {

        adapterPost = PostAdapter(this,R.layout.list_post,listPostArray)
        binding.listPost.adapter = adapterPost
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Log.d("Home activity", "Photo was selected successfully")
            uri = data.data
            val resolver = applicationContext.contentResolver
            val picture = MediaStore.Images.Media.getBitmap(resolver, uri)
            val pictureDrawable = BitmapDrawable(picture)
            binding.imageButton.setBackgroundDrawable(pictureDrawable)
        }



    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        val uid = userDao.getCurrentUserId()
        userDao.getUserByUid(uid,object : UserCallback {
            override fun onSuccess(userItem: UserItem) {

                    Glide
                        .with(this@HomeActivity)
                        .load(userItem.profilePhoto)
                        .into(binding.profilePhotoIV)

            }
            override fun failure() {
            }
        })

        binding.postButton.setOnClickListener {

            post!!.datePost = currentDateTime.format(DateTimeFormatter.ISO_DATE)
            post!!.hourPPost = currentDateTime.format(DateTimeFormatter.ISO_TIME)
            post!!.contentPost = contentPost!!.text.toString()
            post!!.idUser = userDao.getCurrentUserId()
            post!!.picturePost = uri.toString()
            userDao.uploadImageToFirebase(userDao.getCurrentUserId().toString(), uri!!)
            contentPost!!.text.clear()
            binding.imageButton.background = resources.getDrawable(R.drawable.upload)
            userDao.sendPost(post!!)

        }
        super.onResume()
    }
}