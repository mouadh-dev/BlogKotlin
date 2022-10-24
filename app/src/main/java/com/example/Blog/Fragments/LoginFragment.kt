package com.example.Blog.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.Blog.Activity.HomeActivity
import com.example.Blog.Dao.UserCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.UserItem
import com.example.Blog.R
import com.example.Blog.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private var mail: EditText? = null
    private var password: EditText? = null
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        initView()

        return view

    }

    private fun initView() {
        mail = binding.loginMail
        password = binding.loginPassword
        mContext = requireContext()
        var userDao = UserDao()

        binding.loginButton.setOnClickListener {
            if (validateInput()) {
                ////////////////////////////////DIALOG///////////////////////////////
                val v = View.inflate(mContext, R.layout.progress_dialog, null)
                val builder = AlertDialog.Builder(mContext!!)
                builder.setView(v)

                val progressdialog = builder.create()
                progressdialog.show()
                progressdialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                progressdialog.setCancelable(false)
                userDao.signIn(
                    requireActivity() as AppCompatActivity,
                    UserItem(
                        mail = mail!!.text.toString(),
                        password = password!!.text.toString()
                    ),
                    object : UserCallback {
                        override fun onSuccess(userItem: UserItem) {
                            progressdialog.dismiss()
                            requireActivity().run {
                                startActivity(
                                    Intent(this, HomeActivity::class.java)
                                )
                                finish() // If activity no more needed in back stack
                            }
                        }

                        override fun failure(error: String) {
                            val duration = Toast.LENGTH_SHORT
                            progressdialog.dismiss()
                            val toast = Toast.makeText(context, "oops somthing went wrong!! please check your information", duration)
                            toast.show()
                        }
                    })
            }
        }

        binding.createAccountText.setOnClickListener {
            val registerFragment = RegisterFragment()
            requireFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, registerFragment).commit()
        }
    }

    private fun validateInput(): Boolean {

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
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}