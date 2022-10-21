package com.example.Blog.Activity

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.Blog.Dao.SignUpCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.PostItem
import com.example.Blog.Entity.UserItem
import com.example.Blog.Fragments.LoginFragment
import com.example.Blog.R
import com.example.Blog.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var uri: Uri? = null
    private var contentPost: EditText? = null
    private var picturePost: ImageView? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        initView()
        setContentView(view)
    }

    private fun initView() {

        contentPost = binding.contentPostEditText


        binding.imageButton.setOnClickListener {
            Log.println(Log.ASSERT, "selected", "showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0    )
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null){
            Log.d("Home activity", "Photo was selected successfully")
        }
        uri = data!!.data
        val resolver = applicationContext.contentResolver
        val picture = MediaStore.Images.Media.getBitmap(resolver,uri)
        val pictureDrawable = BitmapDrawable(picture)
        binding.imageButton.setBackgroundDrawable(pictureDrawable)
        val userDao = UserDao()

        val postItem = PostItem()
        binding.postButton.setOnClickListener {
            postItem.contentPost = contentPost!!.text.toString()
            postItem.idUser = userDao.getCurrentUserId()
            postItem.picturePost = uri.toString()
            userDao.sendPost(postItem)
            userDao.uploadImageToFirebase(uri!!)
            contentPost!!.text.clear()
            binding.imageButton.background = resources.getDrawable(R.drawable.upload)

        }

    }
}