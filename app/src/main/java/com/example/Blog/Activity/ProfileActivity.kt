package com.example.Blog.Activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import com.bumptech.glide.Glide
import com.example.Blog.Dao.UserCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.UserItem
import com.example.Blog.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var name: EditText? = null
    private var mail: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var passwordText: String? = null
    private var uri: Uri? = null
    private var uid: String? = null
    private val userDao = UserDao()
    private val REQUEST_IMAGE_CAPTURE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        var view = binding.root
        initView()
        setContentView(view)


    }

    private fun initView() {

        name = binding.nameUserUpdateProfile
        mail = binding.mailUserUpdateProfile
        password = binding.PasswordUserUpdateProfile
        confirmPassword = binding.updateProfileConfirmPassword
        uid = userDao.getCurrentUserId()

        userDao.getUserByUid(uid!!, object : UserCallback {
            override fun onSuccess(userItem: UserItem) {
                name!!.setText(userItem.fullname)
                mail!!.setText(userItem.mail)
               // Log.println(Log.ASSERT, "picture", userItem.profilePhoto!!)
                Glide.with(this@ProfileActivity).load(userItem.profilePhoto)
                    .into(binding.updateProfilePicture)
                passwordText = userItem.password
            }

            override fun failure(error: String) {
            }
        })

        binding.updateProfilePicture.setOnClickListener {
            Log.println(Log.ASSERT, "selected", "showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        ////////////////////////////////////////SIGNOUT///////////////////////////::
        binding.signOut.setOnClickListener {
            userDao.signOut()
            val login = AuthActivity()
            val intent = Intent(this@ProfileActivity, login::class.java)
            startActivity(intent)
        }

    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resolver = applicationContext.contentResolver
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Log.d("Register activity", "Photo was selected successfully")
            uri = data.data

            val picture = MediaStore.Images.Media.getBitmap(resolver, uri)
            val pictureDrawable = BitmapDrawable(picture)

            binding.updateProfilePicture.setBackgroundDrawable(pictureDrawable)

        }
        val uid = userDao.getCurrentUserId()
        val user = UserItem()
        binding.updateProfileButton.setOnClickListener {
            if (validateInput()) {
                user.fullname = name!!.text.toString()
                user.mail = mail!!.text.toString()
                user.id = uid
                if (password!!.text.toString() == "" && confirmPassword!!.text.toString() == "") {
                    user.password = passwordText.toString()
                    user.confirmpassword = passwordText.toString()
                } else {
                    user.password = password!!.text.toString()
                    user.confirmpassword = confirmPassword!!.text.toString()
                }

                userDao.updateUser(uid, user, object : UserCallback {
                    override fun onSuccess(userItem: UserItem) {
                        userDao.uploadImageToFirebase(uid, uri!!)
                        Log.println(Log.ASSERT, "mouadh", user.toString())
                        finish()
                    }

                    override fun failure(error: String) {

                    }
                })


            } else {
                Log.println(Log.ASSERT, "error", "error")
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateInput(): Boolean {
        /////////PICTURE

        /////////FULL NAME
        if (name!!.text.toString() == "") {
            name!!.error = "Please Enter Name"
            return false
        }
        /////////EMAIL
        if (mail!!.text.toString() == "") {
            mail!!.error = "Please Enter mail"
            return false
        }
        if (!isEmailValid(mail!!.text.toString())) {
            mail!!.error = "Please Enter Valid Email"
            return false
        }
        /////////PASSWORD

        return true
    }
}