package com.github.ybecker.epforuml

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.github.ybecker.epforuml.database.DatabaseManager
import com.github.ybecker.epforuml.database.DatabaseManager.db
import com.github.ybecker.epforuml.database.Model
import com.github.ybecker.epforuml.sensor.CameraActivity
import java.io.File
import java.io.FileOutputStream

/**
 * A simple [Fragment] subclass.
 * Use the [NewQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewQuestionFragment : Fragment() {

    private lateinit var questBody : EditText
    private lateinit var questTitle : EditText
    private lateinit var imageURI: TextView
    private lateinit var recordVoiceNote: Button
    private lateinit var takePictureButton: Button
    private lateinit var image_uri : String

    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = DatabaseManager.user
        val view = inflater.inflate(R.layout.fragment_new_question, container, false)

        mainActivity = activity as MainActivity
        image_uri = mainActivity.intent.getStringExtra("uri").toString()


        val spinner = view.findViewById<Spinner>(R.id.subject_spinner)
        // Get the set of available courses from the MockDatabase
        db.availableCourses().thenAccept {
            val coursesList = it
            val courseNamesList = coursesList.map { course -> course.courseName }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                courseNamesList
            )
            spinner.adapter = adapter
            setUpArgs(view,spinner,coursesList,user)
        }
        return view

        }

    private fun setUpArgs(
        view: View,
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?,
    ) {
        setUpArgs(view)
        val submitButton = view.findViewById<Button>(R.id.btn_submit)
        recordVoiceNote = view.findViewById(R.id.voice_note_button)
        submitButton?.setOnClickListener(submitButtonListener(spinner, coursesList, user))
        recordVoiceNote.setOnClickListener{
            onVoiceNoteButtonClick(view)
        }
            setTakeImage(view, questBody, questTitle)
        }

    private fun submitButtonListener(
        spinner: Spinner,
        coursesList: List<Model.Course>,
        user: Model.User?
    ): (v: View) -> Unit = {

        // If the user is not logged in, show a message and don't submit the question
        if (user == null) {
            Toast.makeText(
                requireContext(),
                "You must be logged in to post a question",
                Toast.LENGTH_SHORT
            ).show()
        }
        // If the question title or body is empty, show a message and don't submit the question
        else if (questBody.text.isBlank() || questTitle.text.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Question title or body cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            // Get the selected course from the spinner
            val selectedItemPosition = spinner.selectedItemPosition
            if (selectedItemPosition != Spinner.INVALID_POSITION) {
                val questionSubject = spinner.getItemAtPosition(selectedItemPosition) as String
                // Find the course in the list of available courses
                val course =
                    coursesList.firstOrNull { course -> course.courseName == questionSubject }

                // If the course is found, add the question to the database and navigate to the home screen
                if (course != null) {
                    if (imageURI.text == "null") {
                        db.addQuestion(
                            user.userId,
                            course.courseId,
                            questTitle.text.toString(),
                            questBody.text.toString(),
                            imageURI.text.toString()
                        ).thenAccept {
                            //mainActivity.intent.extras.
                            mainActivity.replaceFragment(HomeFragment())
                        }
                    }
                }
            }
        }
    }
    private fun setTakeImage(
        view: View,
        questBody: EditText,
        questTitle: EditText
    ) {
        takePictureButton = view.findViewById(R.id.takeImage)
        takePictureButton.setOnClickListener {
            val questionDetails = questBody.text.toString()
            val questionTitle = questTitle.text.toString()
            val intent = Intent(this.mainActivity, CameraActivity::class.java)
            intent.putExtra("questionTitle", questionTitle)
            intent.putExtra("questionDetails", questionDetails)
            startActivity(intent)
        }
    }

    private fun setUpArgs(view: View): Triple<EditText, EditText, TextView> {
         questBody = view.findViewById(R.id.question_details_edittext)
         questTitle = view.findViewById(R.id.question_title_edittext)
         imageURI = view.findViewById(R.id.image_uri)
        questBody.setText(this.mainActivity.intent.getStringExtra("questionDetails"))
        questTitle.setText(this.mainActivity.intent.getStringExtra("questionTitle"))
        imageURI.text = image_uri
        return Triple(questBody, questTitle, imageURI)
    }

    private fun onVoiceNoteButtonClick(view: View) {
        // Check if the device has a microphone
        if (mainActivity.packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            // Create an intent to start the voice recording activity
            val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
            // Start the activity and wait for a result
            startActivityForResult(intent, REQUEST_CODE_VOICE_NOTE)
        } else {
            // Show an error message if the device does not have a microphone
            Toast.makeText(this.mainActivity, "No microphone found on the device", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VOICE_NOTE && resultCode == Activity.RESULT_OK) {
            // Get the URI of the recorded audio file from the intent
            val audioUri = data?.data
            if (audioUri != null) {
                // Create a new file to save the recorded audio
                val outputFile = File(mainActivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "voice_note_${System.currentTimeMillis()}.3gp")
                // Create an input stream from the audio URI
                val inputStream = mainActivity.contentResolver.openInputStream(audioUri)
                // Create an output stream to the file
                val outputStream = FileOutputStream(outputFile)
                // Copy the contents of the input stream to the output stream
                inputStream?.copyTo(outputStream)
                // Close the streams
                inputStream?.close()
                outputStream.close()
                // Show a message indicating that the voice note was saved
                Toast.makeText(mainActivity, "Voice note saved to ${outputFile.absolutePath}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_VOICE_NOTE = 1
    }

}