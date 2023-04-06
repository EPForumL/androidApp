package com.github.ybecker.epforuml.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager

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
        // Get the user profile picture and set profilePic to it or just sets it to the
        // standard image when no profile picture was added to the user.

        val user = DatabaseManager.user
        val picturePath = user?.profilePic
        if (picturePath != "") {
            val uri = Uri.parse((Uri.decode(picturePath)))
            profilePic.setImageURI(uri)
        } else {
            // Standard image when no profile picture was added
            profilePic.setImageResource(R.drawable.nav_account)
        }

        // Activity used to select a picture in the phone's gallery
        val profilePickEdit = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            if (it != null) {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context?.contentResolver?.takePersistableUriPermission(it, flag)
                profilePic.setImageURI(it)
                DatabaseManager.user?.profilePic = it.toString()
                DatabaseManager.db.updateUser(DatabaseManager.user!!)
            }
        }

        // Launches the picture gallery activity when we click on the profile picture
        profilePic.setOnClickListener {
            profilePickEdit.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        return view
    }
}