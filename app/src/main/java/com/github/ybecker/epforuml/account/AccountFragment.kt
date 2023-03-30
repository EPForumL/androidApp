package com.github.ybecker.epforuml.account

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

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

        val profilePic = view.findViewById<ImageView>(R.id.profilePicture)
        val ppUri = DatabaseManager.user?.profilePic
        if (ppUri != Uri.EMPTY) {
            profilePic.setImageURI(ppUri)
        } else {
            profilePic.setImageResource(R.drawable.nav_account)
        }

        val profilePickEdit = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            if (it != null) {
                profilePic.setImageURI(it)
                // TODO: modify user in database
                DatabaseManager.user?.profilePic = it
            }
        }

        profilePic.setOnClickListener {
            profilePickEdit.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        return view
    }
}