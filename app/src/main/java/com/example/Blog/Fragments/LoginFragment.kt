package com.example.Blog.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.Blog.R
import com.example.Blog.databinding.FragmentLoginBinding
import com.example.Blog.databinding.FragmentRegisterBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.createAccountText.setOnClickListener {
            val registerFragment = RegisterFragment()
            requireFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, registerFragment).commit()
        }
        return binding.root

    }

}