package com.github.ybecker.epforuml.basicEntities.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.features.authentication.FirebaseAuthenticator

/**
 * The guest account fragment
 */
class AccountFragmentGuest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authenticator = FirebaseAuthenticator(requireActivity(), this)

        // Prepares the view for the fragment
        val view = inflater.inflate(
            R.layout.fragment_account_guest,
            container,
            false
        )

        val signInButton = view.findViewById<Button>(R.id.signInButtonAccount)
        signInButton.setOnClickListener { authenticator.signIn() }

        return view
    }
}
