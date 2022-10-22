package com.example.Blog.Activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.example.Blog.Dao.SignUpCallback
import com.example.Blog.Dao.UserCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.UserItem
import com.example.Blog.Fragments.LoginFragment
import com.example.Blog.R
import com.example.Blog.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var name: EditText? = null
    private var mail: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var passwordText: String? = null
    private var uri: Uri? = null


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


        val userDao = UserDao()
        val uid = userDao.getCurrentUserId()



        userDao.getUserByUid(uid, object : UserCallback {
            override fun onSuccess(userItem: UserItem) {
                name!!.setText(userItem.fullname)
                mail!!.setText(userItem.mail)
                Log.println(Log.ASSERT, "picture", userItem.profilePhoto!!)
                Glide.with(this@ProfileActivity).load(userItem.profilePhoto).into(binding.updateProfilePicture)
                passwordText = userItem.password
            }

            override fun failure() {
            }
        })

        binding.updateProfilePicture.setOnClickListener {
            Log.println(Log.ASSERT, "selected", "showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            Log.d("Register activity", "Photo was selected successfully")
            uri = data.data
            val resolver = applicationContext.contentResolver
            val picture = MediaStore.Images.Media.getBitmap(resolver, uri)
            val pictureDrawable = BitmapDrawable(picture)

            binding.updateProfilePicture.setBackgroundDrawable(pictureDrawable)
        }
        val userDao = UserDao()
        val uid = userDao.getCurrentUserId()
        val resolver = applicationContext.contentResolver
        val user = UserItem()
        binding.updateProfileButton.setOnClickListener {
            if (validateInput()) {
                user.fullname = name!!.text.toString()
                user.mail = mail!!.text.toString()
                user.id = uid
                if (password!!.text.toString() == "" && confirmPassword!!.text.toString() == ""){
                    user.password = passwordText.toString()
                    user.confirmpassword = passwordText.toString()
                }else{
                    user.password = password!!.text.toString()
                    user.confirmpassword = confirmPassword!!.text.toString()
                }

                user.profilePhoto = uri.toString()
                userDao.updateUser(uid, user, object : UserCallback {
                    override fun onSuccess(userItem: UserItem) {
                        Log.println(Log.ASSERT, "mouadh", user.toString())
                        finish()
                    }
                    override fun failure() {
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
        if (password!!.text.length < 6 && password!!.text.toString() != "") {
            password!!.error = "Password Length must be more than " + 6 + "characters"
            return false
        }
        if (password!!.text.toString() != confirmPassword!!.text.toString()) {
            confirmPassword!!.error = "Password does not match"
            return false
        }
        return true
    }
}