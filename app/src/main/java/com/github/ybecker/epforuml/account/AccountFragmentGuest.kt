package com.github.ybecker.epforuml.account

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragmentGuest.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragmentGuest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authenticator = FirebaseAuthenticator(requireActivity(), this)

        val view = inflater.inflate(
            R.layout.fragment_account_guest,
            container,
            false)

        val signInButton = view.findViewById<Button>(R.id.signInButtonAccount)
        signInButton.setOnClickListener { authenticator.signIn() }

        return view
    }
}