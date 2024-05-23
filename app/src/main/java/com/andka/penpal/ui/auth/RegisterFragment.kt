package com.andka.penpal.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andka.penpal.R

class RegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout or this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}