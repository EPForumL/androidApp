package com.github.ybecker.epforuml.account

import android.content.Context
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
import com.bumptech.glide.Glide
import com.github.ybecker.epforuml.R
import com.github.ybecker.epforuml.authentication.FirebaseAuthenticator
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.FirebaseDatabaseAdapter

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

        val deleteAccountButton = view.findViewById<Button>(R.id.deleteAccountButton)
        deleteAccountButton.setOnClickListener { authenticator.deleteUser() }

        view = profilePictureManagement(view)

        return view
    }

    private fun profilePictureManagement(view: View): View {
        val localUser = DatabaseManager.user
        if (localUser != null) {
            val profilePic = view.findViewById<ImageView>(R.id.profilePicture)

            // Load the profile picture stored in Firebase storage (if any)
            DatabaseManager.db.getUserById(localUser.userId).thenAccept { user ->
                if (user != null) {
                    loadProfilePictureToView(view.context, user.profilePic, profilePic)
                }
            }

            // Activity used to select a picture in the phone's gallery
            val profilePickEdit = registerForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) { onPickImageResult(it, localUser.userId, view.context, profilePic) }

            // Launches the picture gallery activity when we click on the profile picture
            profilePic.setOnClickListener {
                profilePickEdit.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        }

        return view
    }

    private fun onPickImageResult(uri: Uri?, userId: String, context: Context, profileView: ImageView) {
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver?.takePersistableUriPermission(uri, flag)

            // Upload the image to Firebase storage, change the profile picture image and change
            // the user's profile pic url
            FirebaseDatabaseAdapter.uploadImageToFirebase(uri.toString()).thenAccept { url ->
                loadProfilePictureToView(context, url, profileView)
                DatabaseManager.db.getUserById(userId).thenAccept { user ->
                    if (user != null) {
                        user.profilePic = url
                        DatabaseManager.db.updateUser(user)
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Load the Firebase storage image stored at the given url to the given ImageView
         *
         * @param context the context linked to the ImageView
         * @param url the image url
         * @param imageView the imageView that will contain the image
         */
        fun loadProfilePictureToView(context: Context, url: String, imageView: ImageView) {
            // Loads the profile picture from Firebase and store it into the profileView
            Glide.with(context)
                .load(url)
                .error(R.drawable.nav_account)
                .into(imageView)
        }
    }
}