package com.github.ybecker.epforuml.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * The signed-in account fragment.
 */
class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val authenticator = FirebaseAuthenticator(requireActivity(), this)

        // Prepares the view for the fragment
        val view = inflater.inflate(
            R.layout.fragment_account,
            container,
            false
        )

        val signOutButton = view.findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener { authenticator.signOut() }

        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccoutButton)
        deleteAccountButton.setOnClickListener { authenticator.deleteUser() }

        return view
    }
}