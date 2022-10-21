package com.example.Blog.Fragments

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.println
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.Blog.Dao.SignUpCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.UserItem
import com.example.Blog.R
import com.example.Blog.databinding.FragmentRegisterBinding
import com.example.Blog.databinding.FragmentRegisterBinding.inflate


class RegisterFragment : Fragment() {

    private var mContext: Context? = null
    private lateinit var binding: FragmentRegisterBinding
    private var fullName: EditText? = null
    private var mail: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var uri: Uri? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflate(inflater, container, false)

        var view = binding.root
        initView(view)
        return binding.root

    }

    private fun initView(view: View) {

        fullName = binding.nameUserInscription
        mail = binding.mailUserInscription
        password = binding.PasswordUserInscription
        confirmPassword = binding.InscriptionConfirmPassword



        binding.registerPicture.setOnClickListener {
            println(Log.ASSERT,"selected","showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0    )
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null){
            Log.d("Register activity", "Photo was selected successfully")
        }
        uri = data!!.data
        val resolver = requireActivity().contentResolver
        val picture = MediaStore.Images.Media.getBitmap(resolver,uri)
        val pictureDrawable = BitmapDrawable(picture)
        binding.registerPicture.setBackgroundDrawable(pictureDrawable)

        /////sign up function
        val userDao = UserDao()
        val user = UserItem()
        binding.signUpButton.setOnClickListener {
            if (validateInput()) {
                user.fullname = fullName!!.text.toString()
                user.mail = mail!!.text.toString()
                user.password = password!!.text.toString()
                user.confirmpassword = confirmPassword!!.text.toString()
                user.profilePhoto = uri.toString()
                println(Log.ASSERT, "mouadh", user.toString())
                val loginFragment = LoginFragment()
                userDao.signUpUser(requireActivity() as AppCompatActivity,user,object:SignUpCallback{
                    override fun success() {
                        requireFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, loginFragment).commit()
                    }
                    override fun failure(error: String) {

                    }
                })


            } else {
                println(Log.ASSERT, "error", "error")
            }
        }

    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateInput(): Boolean {
        /////////PICTURE
        if(binding.registerPicture.background === null){
            binding.registerPicture.error = "Please pick a picture"
            return false
        }
        /////////FULL NAME
        if (fullName!!.text.toString() == "") {
            fullName!!.error = "Please Enter Name"
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
        if (password!!.text.toString() == "") {
            password!!.error = "Please Enter password"
            return false
        }
        if (password!!.text.length < 6) {
            password!!.error = "Password Length must be more than " + 6 + "characters"
            return false
        }
        if (confirmPassword!!.text.toString() == "") {
            confirmPassword!!.error = "Please Enter password"
            return false
        }
        if (password!!.text.toString() != confirmPassword!!.text.toString()) {
            confirmPassword!!.error = "Password does not match"
            return false
        }
        return true
    }
}