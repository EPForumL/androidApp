package com.github.ybecker.epforuml.account

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import java.io.File

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
        var view = inflater.inflate(
            R.layout.fragment_account,
            container,
            false
        )

        val signOutButton = view.findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener { authenticator.signOut() }

        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccoutButton)
        deleteAccountButton.setOnClickListener { authenticator.deleteUser() }

        view = profilePictureManagement(view)

        return view
    }

    private fun profilePictureManagement(view: View): View {
        val profilePic = view.findViewById<ImageView>(R.id.profilePicture)
        DatabaseManager.db.getUserById(DatabaseManager.user?.userId!!).thenAccept {
            val ppPath = it?.profilePic
            if (ppPath != "") {
                val uri = Uri.parse(Uri.decode(ppPath))
                profilePic.setImageURI(uri)
            } else {
                profilePic.setImageResource(R.drawable.nav_account)
            }
        }

        val profilePickEdit = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            if (it != null) {
                profilePic.setImageURI(it)
                DatabaseManager.user?.userId?.let { it1 ->
                    DatabaseManager.db.getUserById(it1).thenAccept { user ->
                        val newUser = user?.copy(profilePic = it.toString())
                        if (newUser != null) {
                            DatabaseManager.user = newUser
                            DatabaseManager.db.updateUser(newUser)
                        }
                    }
                }
                DatabaseManager.user?.profilePic = it.toString()
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